import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//スレッド部（各クライアントに応じて）
class ClientProcThread extends Thread {
    private int number;//自分の番号
    private Socket incoming;
    private InputStreamReader myIsr;
    private BufferedReader myIn;
    private PrintWriter myOut;
    private String myName;//接続者の名前


    public ClientProcThread(int n, Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out) {
        number = n;
        incoming = i;
        myIsr = isr;
        myIn = in;
        myOut = out;
    }

    public void run() {
        try {
            myOut.println(number);//初回だけ呼ばれる

            myName = myIn.readLine();//初めて接続したときの一行目は名前
            Poker_Server.userNameList.add(myName);//名前をuserNameListに追加

            if(number == 2){//通信番号が2なら
                //通信メンバーの名前を全員に送る
                Poker_Server.sendAllUserName( Poker_Server.userNameList);

                //最初の手札を全員に配る
                Poker_Server.sendFirstCards();
            }


            while (true) {//無限ループで，ソケットへの入力を監視する

                String str = myIn.readLine();//送られてきたデータを読み込む
                String[] splitStr = str.split(",");
                String cmd = splitStr[0];

                if (str != null) {//データが空でなければ
                    System.out.println();
                    System.out.println(myName + "send :"+str);//自分のところに出力して確認

                    if(cmd.equals("FINISH")){//ゲーム終了コマンドなら
                        //処理記述
                        if(number == 2){//後手の人が終了コマンドをおくったなら
                            System.out.println("全ユーザーが5ターンを終了しました");

                            System.out.println(myName + "の最終的な手札は");
                            for(int i = 1; i <= 5; i++) {//2人目の最終的な手札を登録
                                Poker_Server.cardList2[i-1] = splitStr[i];
                                System.out.println(Poker_Server.cardList2[i-1]);
                            }
                            System.out.println();

                            //ここでそれぞれの手札を入手したリストを使って強さを順位比較して
                            //それぞれの手札と勝利したユーザーの番号を送る
                            Poker_Server.sendWinner();

                            }else{
                            //1人が終了コマンドを送っただけなら
                            //操作するプレイヤーを入れ替える指令を送る
                            System.out.println(myName + "の最終的な手札は");
                            for(int i = 1; i <= 5; i++) {//1人目の最終的な手札を登録
                                Poker_Server.cardList1[i-1] = splitStr[i];
                                System.out.println(Poker_Server.cardList1[i-1]);
                            }
                            System.out.println();
                            Poker_Server.sendUserChange(myName , number);
                        }

                    }else if(cmd.equals("EXCHANGE")){//交換ボタンが押されたなら
                        //処理記述(配り直すカードを取得)
                        String newCard = Poker_Server.exchangeCard(myName , splitStr[1]);

                        //カード情報を送り返す記述 コマンド(SET)+カード情報
                        myOut.println("SET," + newCard);
                        myOut.flush();
                        System.out.println(myName + "に"+newCard +"を配りました");

                        //isHaveの状況を確かめる
                        int count = 0;
                        for(int i = 0; i < 4; i++){
                            for(int j = 0; j < 13; j++){
                                if(Poker_Server.isHave[i][j])count++;
                            }
                        }
                        System.out.println("現在手札化されているカードは"+count+"枚です");
                        System.out.println();

                    } else if(cmd.equals("CHANGE")){//操作
                        //操作するプレイヤーを入れ替える指令を送る処理
                        Poker_Server.sendUserChange(myName , number);
                    }

                    else{

                    }


                }

            }
        } catch (Exception e) {
            //ここにプログラムが到達するときは，接続が切れたとき
            System.out.println("Disconnect from client No."+number+"("+myName+")");
            Poker_Server.SetFlag(number, false);//接続が切れたのでフラグを下げる
        }
    }
}

class Poker_Server{
    static String[] cardList1 = new String[5];//NO1の最終的なカードリスト
    static String[] cardList2 = new String[5];//NO2の最終的なカードリスト
    static ArrayList<String> userNameList = new ArrayList<>();//プレイヤー名を格納
    private static String[][]trunp = new String[4][13];//トランプ格納する二次元配列 [種][位]
    public static int mark;
    public static int number;
    private static final int SPADE = 0;
    private static final int HEART = 1;
    private static final int DIAMOND = 2;
    private static final int CLOVER = 3;
    public static boolean[][] isHave = new boolean[4][13];//手札にあるかの判定
    public static Random random = new Random();

    private static int maxConnection=100;//最大接続数
    private static Socket[] incoming;//受付用のソケット
    private static boolean[] flag;//接続中かどうかのフラグ
    private static InputStreamReader[] isr;//入力ストリーム用の配列
    private static BufferedReader[] in;//バッファリングをによりテキスト読み込み用の配列
    private static PrintWriter[] out;//出力ストリーム用の配列
    private static ClientProcThread[] myClientProcThread;//スレッド用の配列
    private static int member;//接続しているメンバーの数


    //勝者と手札を全員に送信するメソッド
    public static void sendWinner(){
        //それぞれの最終的な手札の役をチェックして、順位をつける
        //1人め
        int rank1;
        String hand1;
        while(true){
            if(Poker_Check.isRoyalStraightFlash(cardList1)){//ロイヤルストレートフラッシュなら
                rank1 = 1;
                hand1 = "ロイヤルストレートフラッシュ";
                break;
            }else if(Poker_Check.isStraightFlash(cardList1)){//ストレートフラッシュなら
                rank1 = 2;
                hand1 = "ストレートフラッシュ";
                break;
            }else if (Poker_Check.isFoursCard(cardList1)) {//フォーカードなら
                rank1 = 3;
                hand1 = "フォーカード";
                break;
            } else if (Poker_Check.isFullHouse(cardList1)) {//フルハウスなら
                rank1 = 4;
                hand1 = "フルハウス";
                break;
            } else if (Poker_Check.isFlash(cardList1)) {//フラッシュなら
                rank1 = 5;
                hand1 = "フラッシュ";
                break;
            } else if (Poker_Check.isStraight(cardList1)) {//ストレートなら
                rank1 = 6;
                hand1 = "ストレート";
                break;
            } else if (Poker_Check.isThreeCard(cardList1)) {//スリーカードなら
                rank1 = 7;
                hand1 = "スリーカード";
                break;
            } else if (Poker_Check.isTwoPair(cardList1)) {//ツウ・ペアなら
                rank1 = 8;
                hand1 = "ツウ・ペア";
                break;
            } else if (Poker_Check.isOnePair(cardList1)) {//ワン・ペアなら
                rank1 = 9;
                hand1 = "ワン・ペア";
                break;
            } else {//ノーペアなら
                rank1 = 10;
                hand1 = "ノーペア";
                break;
            }
        }

        //2人め
        int rank2;
        String hand2;
        while(true){
            if(Poker_Check.isRoyalStraightFlash(cardList2)){//ロイヤルストレートフラッシュなら
                rank2 = 1;
                hand2 = "ロイヤルストレートフラッシュ";
                break;
            }else if(Poker_Check.isStraightFlash(cardList2)){//ストレートフラッシュなら
                rank2 = 2;
                hand2 = "ストレートフラッシュ";
                break;
            }else if (Poker_Check.isFoursCard(cardList2)) {//フォーカードなら
                rank2 = 3;
                hand2 = "フォーカード";
                break;
            } else if (Poker_Check.isFullHouse(cardList2)) {//フルハウスなら
                rank2 = 4;
                hand2 = "フルハウス";
                break;
            } else if (Poker_Check.isFlash(cardList2)) {//フラッシュなら
                rank2 = 5;
                hand2 = "フラッシュ";
                break;
            } else if (Poker_Check.isStraight(cardList2)) {//ストレートなら
                rank2 = 6;
                hand2 = "ストレート";
                break;
            } else if (Poker_Check.isThreeCard(cardList2)) {//スリーカードなら
                rank2 = 7;
                hand2 = "スリーカード";
                break;
            } else if (Poker_Check.isTwoPair(cardList2)) {//ツウ・ペアなら
                rank2 = 8;
                hand2 = "ツウ・ペア";
                break;
            } else if (Poker_Check.isOnePair(cardList2)) {//ワン・ペアなら
                rank2 = 9;
                hand2 = "ワン・ペア";
                break;
            } else {//ノーペアなら
                rank2 = 10;
                hand2 = "ノーペア";
                break;
            }
        }


        //まずはサーバーにprintしておく
        String lastCards ="";
            for(int j = 0; j < 5; j++){
                lastCards += cardList1[j] + ",";
            }
        for(int j = 0; j < 5; j++){
            lastCards += cardList2[j] + ",";
        }
        System.out.println(lastCards);//最終的な手札print

        System.out.println("NO1：" + hand1);
        System.out.println("NO2：" + hand2);

        int winnerUserNumber = 0;
        if(rank1 < rank2){
            winnerUserNumber = 1;
        }else if(rank2 < rank1){
            winnerUserNumber = 2;
        }else if(rank1 == rank2){
            winnerUserNumber = 0;
        }


        for(int i = 1; i <= member; i++){//全員に送る
            if(flag[i]){
                out[i].println("WINNER,"+lastCards + hand1 + "," + hand2 + ","+ winnerUserNumber);
                out[i].flush();
            }
        }


    }

    //手札の役をチェックする
    public static void checkHand(String[] cardList){
        //cardListを種類別に格納するリスト
        ArrayList<String> spadeCard = new ArrayList<>();
        ArrayList<String> heartCard = new ArrayList<>();
        ArrayList<String> diamondCard = new ArrayList<>();
        ArrayList<String> cloverCard = new ArrayList<>();


        //cardListを種類別にする
        for(int i = 0; i < cardList.length; i++) {
                for (int j = 0; j < 13; j++) {
                    if(cardList[i].equals(trunp[SPADE][j])){
                        spadeCard.add(cardList[i]);
                    }else if(cardList[i].equals(trunp[HEART][j])){
                        heartCard.add(cardList[i]);
                    }else if(cardList[i].equals(trunp[DIAMOND][j])){
                        diamondCard.add(cardList[i]);
                    } else if(cardList[i].equals(trunp[CLOVER][j])){
                        cloverCard.add(cardList[i]);
                    }
                }
        }

        for(int i = 0; i < spadeCard.size(); i++){
            System.out.println(spadeCard.get(i));
        }


        //種類別のリストをソートする
        if(spadeCard.size() !=0 && spadeCard.size() != 1){//リストに2枚以上あるなら
            String[] spadeArray = spadeCard.toArray(new String[spadeCard.size()]);
            Arrays.sort(spadeArray);//辞書順
            for(int i = 0; i < spadeArray.length; i++){
                System.out.print(spadeArray[i]);
            }
        }


       // return "NO PAIR";
    }

    //操作が終わったことを全員に送信するメソッド
    public static void sendUserChange(String myName , int number){
        for(int i = 1; i <= member; i++){//全員に送る
            if(flag[i]){
                out[i].println("CHANGE USER,"+number);
                out[i].flush();
            }
        }
        System.out.println(myName +"の操作が終わったことを送信しました");
    }


    //それぞれにユーザーの名前を送るメソッド
    public static void sendAllUserName(ArrayList<String> userNameList){
        for(int i =1; i<=member; i++){
            if(flag[i] == true){
                out[i].println(userNameList.get(0)+" "+userNameList.get(1));
                out[i].flush();
            }
        }
    }

    //最初の手札を決めるメソッド
    public static void sendFirstCards(){
        String firstCard10 = "";
        for(int i = 0; i < 10; i++){//全員に配るカード10枚分繰り返す
            while(true) {
                mark = random.nextInt(4);
                number = random.nextInt(13);
                if(!isHave[mark][number]){//手札にないカードだったら
                    isHave[mark][number] = true;//カードを手札化
                    String cardLabel = trunp[mark][number] + ",";
                    firstCard10 += cardLabel;
                    System.out.println("カード"+i+"番目が決まりました");
                    break;
                }
            }
        }
            System.out.println(firstCard10);//配布したカードを確認

            for(int i = 1; i <= member; i++){//全員にカード情報(10枚分)を送る
                if(flag[i]){
                    out[i].println(firstCard10);
                    out[i].flush();
                }
            }

        //isHaveの状態をチェック
        int count = 0;
        for(int k=0; k<4; k++){
            for(int j = 0; j<13; j++){
                if(isHave[k][j]){//チェックが入ってたら
                    count++;
                }
            }
        }
        System.out.println(count+"枚のカードを配りました");
        System.out.println();

    }

    //カードを山場に戻して、新たなカードを渡すメソッド
    public static String exchangeCard(String myName,String card){
        //まずは受信したカードを山場に戻す(手札化を解除)
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 13; j++){//トランプ二次元配列を精査して
                if(trunp[i][j].equals(card)){//書いてる内容が同じなら
                    isHave[i][j] = false;//カードの手札化を解除する
                    System.out.println(myName + "の"+trunp[i][j] +"を山場に戻しました");
                }
            }
        }

        //次に新たなカードを1枚配る
        while(true){
            mark = random.nextInt(4);//種類と位はランダム関数で決める
            number = random.nextInt(13);
            if(!isHave[mark][number]){//手札にないカードだったら
                isHave[mark][number] = true;//カードを手札化
                System.out.println(myName + "の新しいカードは" + trunp[mark][number] + "です");
                break;
            }
        }

        return trunp[mark][number];
    }


    //フラグの設定を行う
    public static void SetFlag(int n, boolean value){
        flag[n] = value;
    }


    //mainプログラム
    public static void main(String[] args) {
        //トランプ生成、格納
        for (int i = 0; i < 4; i++) {//役の数繰り返す
            for (int j = 0; j <= 8; j++) {//数字カードは2~10まで
                if (i == SPADE) {
                    int number = j + 2;
                    trunp[SPADE][j] = "♠ : " + number;
                    trunp[SPADE][9] = "♠ : J";
                    trunp[SPADE][10] = "♠ : Q";
                    trunp[SPADE][11] = "♠ : K";
                    trunp[SPADE][12] = "♠ : A";
                } else if (i == HEART) {
                    int number = j + 2;
                    trunp[HEART][j] = "♥ : " + number;
                    trunp[HEART][9] = "♥ : J";
                    trunp[HEART][10] = "♥ : Q";
                    trunp[HEART][11] = "♥ : K";
                    trunp[HEART][12] = "♥ : A";
                } else if (i == DIAMOND) {
                    int number = j + 2;
                    trunp[DIAMOND][j] = "♦ : " + number;
                    trunp[DIAMOND][9] = "♦ : J";
                    trunp[DIAMOND][10] = "♦ : Q";
                    trunp[DIAMOND][11] = "♦ : K";
                    trunp[DIAMOND][12] = "♦ : A";
                } else if (i == CLOVER) {
                    int number = j + 2;
                    trunp[CLOVER][j] = "♣ : " + number;
                    trunp[CLOVER][9] = "♣ : J";
                    trunp[CLOVER][10] = "♣ : Q";
                    trunp[CLOVER][11] = "♣ : K";
                    trunp[CLOVER][12] = "♣ : A";
                }
            }
        }
        //最初は全て山札にある判定にしておく
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 13; j++){
                isHave[i][j] = false;
            }
        }


        //生成したカードの中身確認、山札の数を確認
        int count = 0;
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 13; j++){
                System.out.print(trunp[i][j]+",");
                if(!isHave[i][j])count++;
            }
            System.out.println();
        }
        System.out.println("山札にあるカードは"+count+"枚です。");

        //必要な配列を確保する
        incoming = new Socket[maxConnection];
        flag = new boolean[maxConnection];
        isr = new InputStreamReader[maxConnection];
        in = new BufferedReader[maxConnection];
        out = new PrintWriter[maxConnection];
        myClientProcThread = new ClientProcThread[maxConnection];

        int n = 1;
        member = 0;//誰も接続していないのでメンバー数は０

        try {
            System.out.println("The server has launched!");
            ServerSocket server = new ServerSocket(1000);//10000番ポートを利用する
            while (true) {
                incoming[n] = server.accept();
                flag[n] = true;
                System.out.println("Accept client No." + n);
                //必要な入出力ストリームを作成する
                isr[n] = new InputStreamReader(incoming[n].getInputStream());
                in[n] = new BufferedReader(isr[n]);
                out[n] = new PrintWriter(incoming[n].getOutputStream(), true);

                myClientProcThread[n] = new ClientProcThread(n, incoming[n], isr[n], in[n], out[n]);//必要なパラメータを渡しスレッドを作成
                myClientProcThread[n] .start();//スレッドを開始する
                member = n;//メンバーの数を更新する
                n++;
            }
        } catch (Exception e) {
            System.err.println("ソケット作成時にエラーが発生しました: " + e);
        }
    }
}