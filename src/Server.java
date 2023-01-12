import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
* 複数クライアントからの接続を待ち受け、通信処理を行うスレッドを作成して起動するControllerクラス
*/
public class Server {
    // start field
    /*  ポート番号  */
    final private static int PORT_NUMBER = 10000;
    /*  最大接続数  */
    final private static int MAX_CONNECTION = 2;
    /*  接続しているプレイヤー名  */
    public static String[] playerNames = new String[MAX_CONNECTION];
    /*  クライアントへの送信用  */
    private static DataOutputStream[] data_out = new DataOutputStream[MAX_CONNECTION];
    private static ObjectOutputStream[] obj_out = new ObjectOutputStream[MAX_CONNECTION];
    /*  ディーラー  */
    private static Dealer dealer = new Dealer();
    // end field

    // start getter
    public static int getMAX_CONNECTION() {
        return MAX_CONNECTION;
    }

    public static Dealer getDealer() {
        return dealer;
    }
    // end getter

    // start public function
    /**
     * ソケット作成、サーバー起動、クライアント待ち受け、スレッド生成 
     * @param args
     */
    public static void main(String[] args) {
        // サーバソケット
        ServerSocket serverSocket;
        // 現状の接続者数
        int connection_number = 0;
        try {
            // サーバーソケット作成
            serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Serverが起動しました(port=" + serverSocket.getLocalPort() + ")");
            while (true) {
                try {
                    // クライアントからの接続を待ち受け
                    Socket socket = serverSocket.accept();
                    // 定員が上限に達していない限り接続を受け付け、スレッドを作成する
                    if (connection_number < MAX_CONNECTION) {
                        data_out[connection_number] = new DataOutputStream(socket.getOutputStream());
                        obj_out[connection_number] = new ObjectOutputStream(socket.getOutputStream());
                        new ServerThread(socket, connection_number).start();
                        connection_number++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * プレイヤー全員に指定の文字列を送信
     * @param 送信する文字列
     */
    public static void sendForAllPlayers_String(String str) {
        for (DataOutputStream dos : data_out) {
            try {
                dos.writeUTF(str);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * プレイヤー全員に指定の数値を送信
     * @param 送信する数値
     */
    public static void sendForAllPlayers_Integer(int num) {
        for (DataOutputStream dos : data_out) {
            try {
                dos.writeInt(num);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * プレイヤー全員に指定のオブジェクトを送信 
     * @param 送信するオブジェクト
     */
    public static void sendForAllPlayers_Object(Object obj) {
        for (ObjectOutputStream oos : obj_out) {
            try {
                oos.writeObject(obj);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // end public function
}

/**
 * 各スレッドの処理
 */
class ServerThread extends Thread {
    // start field
    /*  プレイヤー番号  */
    private int playerNumber;
    /*  ソケット  */
    private Socket socket;
    /*  ディーラー操作  */
    private DealerLogic dealerLogic = new DealerLogic();
    // end field

    // start constructor
    public ServerThread(Socket socket, int playerNumber) {
        this.socket = socket;
        this.playerNumber = playerNumber;
    }
    // end constructor

    // start public function
    public void run() {
        try {
            // クライアントからの受取用
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // クライアントへの送信用
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // 名前が送られてくるので登録
            Server.playerNames[playerNumber] = in.readLine();
            System.out.println(playerNumber + "番目：" + Server.playerNames[playerNumber] + "さんが入室しました");
            // プレイヤー番号を返す
            out.writeInt(playerNumber);
            out.flush();

            // 2人目のプレイヤーだった場合、プレイヤー登録する
            if (playerNumber == Server.getMAX_CONNECTION() - 1) {
                // プレイヤー名登録
                Server.getDealer().setPlayerNames(Server.playerNames);
                // ゲーム開始処理
                Server.sendForAllPlayers_String("START");
                dealerLogic.gameStart(Server.getDealer());
                Server.sendForAllPlayers_Object(Server.getDealer());
            }

            // 無限ループでソケットへの入力を監視する
            while (true) {
                // 送られてきたメッセージ読み込み
                String message = in.readLine();
                if (message != null) {
                    System.out.println(Server.playerNames[playerNumber] + "：" + message);
                    switch (message) {
                        /* カード交換 */
                        case "EXCHANGE":
                            // 交換するカード情報（手札の何枚目か）
                            int changeCardNum = Integer.parseInt(in.readLine());
                            Card exchangeCard = dealerLogic.exchangeCard(Server.getDealer(), changeCardNum, null);
                            Server.sendForAllPlayers_String("EXCHANGE");
                            Server.sendForAllPlayers_Integer(changeCardNum);
                            Server.sendForAllPlayers_Integer(exchangeCard.getSuit_Integer());
                            Server.sendForAllPlayers_Integer(exchangeCard.getNumber());
                            break;
                        /* オペレーション（行動）終了 */
                        case "OPERATIONEND":
                            System.out.println("行動終了だ");
                            switch (dealerLogic.opperationEnd(Server.getDealer())) {
                                case "GAMEEND":
                                    System.out.println("ゲーム終了！");
                                    // 勝敗を決める
                                    dealerLogic.judgeTheWinner(Server.getDealer());
                                    Server.sendForAllPlayers_String("GAMEEND");
                                    break;
                                default:
                                    System.out.println("今" + Server.getDealer().getCount_Of_Turn() + "ターンめ");
                                    System.out.println(
                                            "次は" + Server.getDealer().getPlayerNames()[Server.getDealer()
                                                    .getNum_Of_TurnPlayer()] + "の番");
                                    Server.sendForAllPlayers_String("CONTINUEGAME");
                                    // Server.sendForAllPlayers_Object(Server.getDealer());
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                    // exitだったら終了
                    if (message.equals("exit")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("ここ4");
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("切断されました " + socket.getRemoteSocketAddress());
        }
    }
    // end public function
}