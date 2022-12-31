import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Poker_C extends Frame implements ActionListener {
    /* パネル変数 */
    // おけ
    Panel leftPanel, left_upPanel, left_downPanel, rightPanel, right_upPanel, right_downPanel;
    Panel enemyCardPanel, myCardPanel;
    /* ユーザー名 */
    private static String enemyName = "対戦相手を探しています";
    public String myName;
    public static String[] userName;
    JLabel enemyNameLabel, myNameLabel;
    /* 相手のカード */
    // おけ
    static Button EnemyCard1, EnemyCard2, EnemyCard3, EnemyCard4, EnemyCard5;
    //
    static String hideCard[] = { "", "", "", "", "" };
    /* 自分のカード */
    // おk
    static Button myCard1, myCard2, myCard3, myCard4, myCard5;
    //
    static String[] openCard = new String[5];
    //
    boolean[] isSelected = new boolean[5];// カードが選択されているか
    //
    static int selectedCardNumber = 0;// 選択されているカードが何枚目かを保持する
    /* ターン */
    private int myNumber;
    //
    static String[] turnNumber = { "ターン:1", "ターン:2", "ターン:3", "ターン:4", "ターン:5" };// ●
    //
    static String[] turnUser = { "あなたの番です", "相手の番です" };// ●
    // おk turnuserとwinner場所同じだから合体
    static JLabel turnNumberLabel, turnUserLabel, winnerLabel;
    /* 交換操作 */
    // おk
    Button exchangeButton, finishedButton;
    String exchangeCommand = "交換", finishedCommand = "ターン終了";

    PrintWriter out;// 出力用のライター

    // end of 変数設定

    public Poker_C() {
        // 名前の入力ダイアログを開く
        myName = JOptionPane.showInputDialog(null, "名前を入力してください", "名前の入力", JOptionPane.QUESTION_MESSAGE);
        if (myName.equals("")) {
            myName = "No name";// 名前がないときは，"No name"とする
        }

        // ウィンドウを作成する
        setSize(800, 400);
        setTitle(myName + "_Poker");
        setLayout(new GridLayout(1, 2));

        /* 左のパネル作成 ok */
        leftPanel = new Panel();
        leftPanel.setLayout(new GridLayout(2, 1));
        left_upPanel = new Panel();
        left_upPanel.setLayout(new BorderLayout());
        leftPanel.add(left_upPanel);
        left_downPanel = new Panel();
        left_downPanel.setLayout(new BorderLayout());
        leftPanel.add(left_downPanel);
        add(leftPanel);
        // 対戦相手の名前表示 おk
        enemyNameLabel = new JLabel(enemyName);
        enemyNameLabel.setHorizontalAlignment(JLabel.CENTER);
        left_upPanel.add(enemyNameLabel, BorderLayout.NORTH);
        // 対戦相手のカード表示 おk
        enemyCardPanel = new Panel();
        enemyCardPanel.setLayout(new GridLayout(1, 5));
        left_upPanel.add(enemyCardPanel, BorderLayout.CENTER);
        EnemyCard1 = new Button(hideCard[0]); // カードボタンの作成
        enemyCardPanel.add(EnemyCard1);
        EnemyCard2 = new Button(hideCard[1]);
        enemyCardPanel.add(EnemyCard2);
        EnemyCard3 = new Button(hideCard[2]);
        enemyCardPanel.add(EnemyCard3);
        EnemyCard4 = new Button(hideCard[3]);
        enemyCardPanel.add(EnemyCard4);
        EnemyCard5 = new Button(hideCard[4]);
        enemyCardPanel.add(EnemyCard5);
        // 自分の名前表示 おk
        myNameLabel = new JLabel(myName);
        myNameLabel.setHorizontalAlignment(JLabel.CENTER);
        left_downPanel.add(myNameLabel, BorderLayout.SOUTH);
        // 自分のカード表示 ok
        myCardPanel = new Panel();
        myCardPanel.setLayout(new GridLayout(1, 5));
        left_downPanel.add(myCardPanel, BorderLayout.CENTER);
        myCard1 = new Button(openCard[0]); // カードボタンの作成
        myCard1.addActionListener(this); // ボタンを押された時反応する
        myCardPanel.add(myCard1);
        myCard2 = new Button(openCard[1]);
        myCard2.addActionListener(this);
        myCardPanel.add(myCard2);
        myCard3 = new Button(openCard[2]);
        myCard3.addActionListener(this);
        myCardPanel.add(myCard3);
        myCard4 = new Button(openCard[3]);
        myCard4.addActionListener(this);
        myCardPanel.add(myCard4);
        myCard5 = new Button(openCard[4]);
        myCard5.addActionListener(this);
        myCardPanel.add(myCard5);

        /* 右のパネル作成 おk */
        rightPanel = new Panel();
        rightPanel.setLayout(new GridLayout(2, 1));
        right_upPanel = new Panel();
        right_upPanel.setLayout(new GridLayout(3, 1));
        rightPanel.add(right_upPanel);
        right_downPanel = new Panel();
        right_downPanel.setLayout(new GridLayout(1, 2));
        rightPanel.add(right_downPanel);
        add(rightPanel);
        // ターン数の表示 おk
        turnNumberLabel = new JLabel(turnNumber[0]);
        turnNumberLabel.setHorizontalAlignment(JLabel.CENTER);
        right_upPanel.add(turnNumberLabel);
        // 自分の番か表示
        turnUserLabel = new JLabel("");// まだ空白
        turnUserLabel.setHorizontalAlignment(JLabel.CENTER);
        right_upPanel.add(turnUserLabel);
        // 勝者の表示
        winnerLabel = new JLabel("");// まだ空白
        winnerLabel.setHorizontalAlignment(JLabel.CENTER);
        right_upPanel.add(winnerLabel);
        // 交換ボタンの作成
        exchangeButton = new Button(exchangeCommand);
        exchangeButton.addActionListener(this);// 押された時反応する
        right_downPanel.add(exchangeButton);
        // 終了ボタンの作成
        finishedButton = new Button(finishedCommand);
        finishedButton.addActionListener(this);// 押された時反応する
        right_downPanel.add(finishedButton);

        // ウィンドウを閉じる処理
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        // サーバに接続する
        Socket socket = null;
        try {
            // "localhost"は，自分内部への接続．localhostを接続先のIP
            // Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
            // 10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
            socket = new Socket("localhost", 10000);
        } catch (UnknownHostException e) {
            System.err.println("ホストの IP アドレスが判定できません: " + e);
        } catch (IOException e) {
            System.err.println("エラーが発生しました: " + e);
        }

        MesgRecvThread mrt = new MesgRecvThread(socket, myName);
        mrt.start();
    }// end of コンストラクタ

    // ターンが来たプレイヤー名を書き換えるメソッド
    public void setTurnUser(int turn) {
        turnUserLabel.setText(turnUser[turn]);
    }

    // selectedCard(int)と受信したカード情報(String)を使ってボタンラベルを変える
    public void setButtonLabel(int cardNumber, String newCard) {
        openCard[cardNumber] = newCard;// 交換するカードのコマンドを書き換える
        // カードのラベルを更新
        myCard1.setLabel(openCard[0]);
        myCard2.setLabel(openCard[1]);
        myCard3.setLabel(openCard[2]);
        myCard4.setLabel(openCard[3]);
        myCard5.setLabel(openCard[4]);

        System.out.println("現在の手札は");
        System.out.println(
                openCard[0] + " , " + openCard[1] + " , " + openCard[2] + "," + openCard[3] + "," + openCard[4]);
        System.out.println("です");
        System.out.println();
    }

    // メッセージ受信のためのスレッド
    public class MesgRecvThread extends Thread {

        Socket socket;
        String myName;

        public MesgRecvThread(Socket s, String n) {
            socket = s;
            myName = n;
        }

        // 通信状況を監視し，受信データによって動作する
        public void run() {
            try {
                /* 接続の最初に行う */
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(myName);// 自分の名前を送る

                String myNumberStr = reader.readLine();
                myNumber = Integer.parseInt(myNumberStr);// 接続した順番を取得
                System.out.println("No" + myNumber);

                // 自分の番か相手の番か表示する
                if (myNumber == 1) {
                    setTurnUser(0);
                } else {
                    setTurnUser(1);
                }

                // ユーザーの名前が羅列してある文を受信したので読み取って
                // 対戦相手の名前をラベルにセットする
                String userNameList = reader.readLine();
                userName = userNameList.split(" ");// ユーザの名前ごとに分ける
                if (myNumber == 1) {
                    enemyNameLabel.setText(userName[1]);// 対戦相手ラベルにセットする
                    System.out.println("対戦相手は " + userName[1] + " さんです");
                } else {
                    enemyNameLabel.setText(userName[0]);// 対戦相手ラベルにセットする
                    System.out.println("対戦相手は " + userName[0] + " さんです");
                }

                // 最初に配るカードの種類と位が羅列してある文を受信したので読み取って
                // ボタンのラベルにセットし、コマンドも変える
                String firstCard = reader.readLine();
                String[] firstCardList = firstCard.split(",");

                if (myNumber == 1) {// 1なら最初の5枚をボタンにセット
                    for (int i = 0; i < 5; i++) {// ボタンのコマンドを変える
                        openCard[i] = firstCardList[i];
                    }
                    myCard1.setLabel(openCard[0]);
                    myCard2.setLabel(openCard[1]);
                    myCard3.setLabel(openCard[2]);
                    myCard4.setLabel(openCard[3]);
                    myCard5.setLabel(openCard[4]);
                    System.out.println("最初に配られたカードは");
                    for (int i = 0; i < 5; i++) {
                        System.out.print(openCard[i] + ",");
                    }
                    System.out.println();
                    System.out.println("です");
                    System.out.println();

                } else {// 2なら残りの5枚をボタンにセット
                    for (int i = 0; i < 5; i++) {// ボタンのコマンドも変える
                        openCard[i] = firstCardList[i + 5];
                    }
                    myCard1.setLabel(openCard[0]);
                    myCard2.setLabel(openCard[1]);
                    myCard3.setLabel(openCard[2]);
                    myCard4.setLabel(openCard[3]);
                    myCard5.setLabel(openCard[4]);

                    System.out.println("最初に配られたカードは");
                    for (int i = 0; i < 5; i++) {
                        System.out.print(openCard[i] + ",");
                    }
                    System.out.println();
                    System.out.println("です");
                }

                while (true) {// 送って送り返された時受け取る場所
                    String inputLine = reader.readLine();// データを一行分だけ読み込んでみる
                    if (inputLine != null) {// 読み込んだときにデータが読み込まれたかどうかをチェックする
                        System.out.println("サーバーから " + inputLine + "のコマンドが送られました");// デバッグ（動作確認用）にコンソールに出力する
                        System.out.println();
                        String[] inputTokens = inputLine.split(","); // 入力データを解析するために、","で切り分ける
                        String cmd = inputTokens[0];// コマンドの取り出し．１つ目の要素を取り出す

                        // コマンドがSETならカード情報をボタンとコマンドにセット+ターンラベルと番を変える
                        if (cmd.equals("SET")) {
                            String newCard = inputTokens[1];// (例：♠︎ : 10)新たに配られたカード情報を取得
                            setButtonLabel(selectedCardNumber, newCard);// カードのコマンドとラベル変える

                            // ボタン交換が終わったらターンエンド処理

                            // ターン数を+1する
                            // 現在のターン:数を取得
                            String TurnStr = turnNumberLabel.getText();// (例：ターン:1)
                            String[] TurnSplit = TurnStr.split(":");// (例：ターンと1に分けられる)
                            int currentTurn = Integer.parseInt(TurnSplit[1]);// 数字の部分だけ取り出す
                            if (currentTurn == 5) {// 今の表示がターン:5なら
                                out.println("FINISH," + openCard[0] + "," + openCard[1] + "," +
                                        openCard[2] + "," + openCard[3] + "," + openCard[4]);// 全5ターン終了したことと現在の手札をサーバーに送る
                                out.flush();
                                System.out.println("全5ターンが終了したことをサーバーに伝えました");
                            } else {
                                turnNumberLabel.setText(turnNumber[currentTurn]);// ラベル取り替え
                                System.out.println(turnNumberLabel.getText() + " になりました");// ターン数確認

                                // 操作ユーザーをチェンジする
                                out.println("CHANGE," + myNumber);
                                out.flush();
                            }

                        } else if (cmd.equals("CHANGE USER")) {
                            int endNumber = Integer.parseInt(inputTokens[1]);// 操作が終わったユーザーのnumber

                            if (endNumber == myNumber) {
                                System.out.println("操作が終わったユーザーは" + myName + "です");
                                turnUserLabel.setText(turnUser[1]);// 相手の番ですに切り替える
                            } else if (endNumber != myNumber) {
                                System.out.println("操作が終わったユーザーは" + enemyNameLabel.getText() + "です");
                                turnUserLabel.setText(turnUser[0]);// あなたの番ですに切り替える
                            }
                            System.out.println();
                        } else if (cmd.equals("WINNER")) {

                            if (myNumber == 1) {
                                EnemyCard1.setLabel(inputTokens[6]);// 相手のカードを表示する
                                EnemyCard2.setLabel(inputTokens[7]);
                                EnemyCard3.setLabel(inputTokens[8]);
                                EnemyCard4.setLabel(inputTokens[9]);
                                EnemyCard5.setLabel(inputTokens[10]);
                                turnNumberLabel.setText(userName[1] + "：" + inputTokens[12]);
                                turnUserLabel.setText(myName + "：" + inputTokens[11]);
                            } else if (myNumber == 2) {
                                EnemyCard1.setLabel(inputTokens[1]);
                                EnemyCard2.setLabel(inputTokens[2]);
                                EnemyCard3.setLabel(inputTokens[3]);
                                EnemyCard4.setLabel(inputTokens[4]);
                                EnemyCard5.setLabel(inputTokens[5]);
                                turnNumberLabel.setText(userName[0] + "：" + inputTokens[11]);
                                turnUserLabel.setText(myName + "：" + inputTokens[12]);
                            }

                            if (inputTokens[13].equals("1")) {// No1が勝利したなら
                                winnerLabel.setText(userName[0] + "の勝ち");
                            } else if (inputTokens[13].equals("2")) {
                                winnerLabel.setText(userName[1] + "の勝ち");
                            } else if (inputTokens[13].equals("0")) {
                                winnerLabel.setText("引き分け");
                            }

                        }

                    } else {
                        break;
                    }

                }
                socket.close();
            } catch (IOException e) {
                System.err.println("エラーが発生しました: " + e);
            }
        }
    }

    @Override
    // ボタンが押された時に行う処理
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();// コマンドを取得

        // カードが押されたらisSelectedをtrueにする
        if (command.equals(openCard[0])) {// カード1枚目
            for (int i = 0; i < 5; i++) {
                if (i == 0) {
                    isSelected[0] = true;
                    System.out.println("カード1が選ばれています。");
                } else {
                    isSelected[i] = false;
                }
            }
        } else if (command.equals(openCard[1])) {// カード2枚目
            for (int i = 0; i < 5; i++) {
                if (i == 1) {
                    isSelected[1] = true;
                    System.out.println("カード2が選ばれています");
                } else {
                    isSelected[i] = false;
                }
            }
        } else if (command.equals(openCard[2])) {// カード3枚目
            for (int i = 0; i < 5; i++) {
                if (i == 2) {
                    isSelected[2] = true;
                    System.out.println("カード3が選ばれています");
                } else {
                    isSelected[i] = false;
                }
            }
        } else if (command.equals(openCard[3])) {// カード4枚目
            for (int i = 0; i < 5; i++) {
                if (i == 3) {
                    isSelected[3] = true;
                    System.out.println("カード4が選ばれています");
                } else {
                    isSelected[i] = false;
                }
            }
        } else if (command.equals(openCard[4])) {// カード5枚目
            for (int i = 0; i < 5; i++) {
                if (i == 4) {
                    isSelected[4] = true;
                    System.out.println("カード5が選ばれています");
                } else {
                    isSelected[i] = false;
                }
            }
        }

        // 交換ボタンが押された時の処理
        if (command.equals(exchangeCommand)) {
            if (turnUserLabel.getText().equals(turnUser[0])) {// あなたの番なら
                // 交換ボタンが押されたら
                for (int i = 0; i < 5; i++) {
                    if (isSelected[i]) {// フラグが立っているカードがあったら
                        // 選ばれたカードの情報をサーバに送る(例：♠︎ : 10)
                        selectedCardNumber = i;// 選ばれているカードをこのクラスで保持
                        out.println("EXCHANGE," + openCard[i]);
                        out.flush();
                        System.out.println(openCard[i] + "を交換してください");
                    }
                }
            }
        }

        // ターン終了ボタンが押された時の処理
        if (command.equals(finishedCommand)) {
            if (turnUserLabel.getText().equals(turnUser[0])) {// あなたの番の時なら処理をする
                // ターンエンド処理
                // ターン数を+1する
                // 現在のターン:数を取得
                String TurnStr = turnNumberLabel.getText();// (例：ターン:1)
                String[] TurnSplit = TurnStr.split(":");// (例：ターンと1に分けられる)
                int currentTurn = Integer.parseInt(TurnSplit[1]);// 数字の部分だけ取り出す

                if (currentTurn == 5) {// 今の表示がターン:5なら
                    out.println("FINISH," + openCard[0] + "," + openCard[1] + "," +
                            openCard[2] + "," + openCard[3] + "," + openCard[4]);// 全5ターン終了したことと現在の手札をサーバーに送る
                    out.flush();
                    System.out.println("全5ターンが終了したことをサーバーに伝えました");

                } else {// 5ターンめでないのならターン数を変える
                    turnNumberLabel.setText(turnNumber[currentTurn]);// ラベル取り替え
                    System.out.println(turnNumberLabel.getText() + " になりました");// ターン数確認

                    // 操作ユーザーをチェンジする
                    out.println("CHANGE," + myNumber);
                    out.flush();
                }
            }
        }

    }

    public static void main(String[] args) {
        Poker_C net = new Poker_C();
        net.setVisible(true);
    }

}