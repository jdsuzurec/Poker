import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/*
* <summary>
* 複数クライアントからの接続を待ち受け、通信処理を行うスレッドを作成して起動
* </summary>
*/
public class Server {
    // #region field
    /* <summary> ポート番号 </summary> */
    final private static int PORT_NUMBER = 10000;
    /* <summary> 最大接続数 </summary> */
    final private static int MAX_CONNECTION = 2;
    /* <summary> 接続しているプレイヤー名 </summary> */
    public static String[] playerNames = new String[MAX_CONNECTION];
    /* <summary> クライアントへの送信用 </summary> */
    private static DataOutputStream[] data_out = new DataOutputStream[MAX_CONNECTION];
    private static ObjectOutputStream[] obj_out = new ObjectOutputStream[MAX_CONNECTION];
    /* <summary> ディーラー </summary> */
    private static Dealer dealer = new Dealer();
    // #endregion field

    // #region getter
    public static int getMAX_CONNECTION() {
        return MAX_CONNECTION;
    }

    public static Dealer getDealer() {
        return dealer;
    }
    // #endregion getter

    // #region public function
    // <summary> ソケット作成、サーバー起動、クライアント待ち受け、スレッド生成 </summary>
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

    // <summary> プレイヤー全員に指定の文字列を送信 </summary>
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

    // <summary> プレイヤー全員に指定の数値を送信 </summary>
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

    // <summary> プレイヤー全員に指定のオブジェクトを送信 </summary>
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
    // #endregion public function
}

/*
 * <summary>
 * 各スレッドの処理
 * </summary>
 */
class ServerThread extends Thread {
    // #region field
    /* <summary> プレイヤー番号 </summary> */
    private int playerNumber;
    /* <summary> ソケット </summary> */
    private Socket socket;
    /* <summary> ディーラー操作 </summary> */
    private DealerLogic dealerLogic = new DealerLogic();
    // #endregion field

    // #region constructor
    public ServerThread(Socket socket, int playerNumber) {
        this.socket = socket;
        this.playerNumber = playerNumber;
    }
    // #endregion constructor

    // #region public function
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

            // // 無限ループでソケットへの入力を監視する
            while (true) {
                // 送られてきたメッセージ読み込み
                String message = in.readLine();
                if (message != null) {
                    System.out.println(Server.playerNames[playerNumber] + "：" + message);
                    switch (message) {
                        /* <summary>カード交換</summary> */
                        case "EXCHANGE":
                            // 交換するカード情報（手札の何枚目か）
                            int changeCardNum = Integer.parseInt(in.readLine());
                            Card exchangeCard = dealerLogic.exchangeCard(Server.getDealer(), changeCardNum, null);
                            Server.sendForAllPlayers_String("EXCHANGE");
                            Server.sendForAllPlayers_Integer(changeCardNum);
                            Server.sendForAllPlayers_Integer(exchangeCard.getSuit_Integer());
                            Server.sendForAllPlayers_Integer(exchangeCard.getNumber());
                            break;
                        /* <summary>オペレーション（行動）終了</summary> */
                        case "OPERATIONEND":
                            System.out.println("行動終了だ");
                            switch (dealerLogic.opperationEnd(Server.getDealer())) {
                                case "GAMEEND":
                                    System.out.println("ゲーム終了！");
                                    // 勝敗を決める
                                    dealerLogic.gameEnd(Server.getDealer());
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
    // #endregion public function
}