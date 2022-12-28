import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    final private static int PORT_NUMBER = 10000;
    final public static int MAX_CONNECTION = 2;
    public static Dealer dealer = Dealer.getInstance();
    public static String[] userNames = new String[MAX_CONNECTION];
    // private static PrintWriter[] out = new PrintWriter[MAX_CONNECTION];
    private static DataOutputStream[] data_out = new DataOutputStream[MAX_CONNECTION];
    private static ObjectOutputStream[] obj_out = new ObjectOutputStream[MAX_CONNECTION];

    public Server() {
        // dealer = Dealer.getInstance();
    }

    public static void main(String[] args) {
        // TCPポートを指定してサーバソケットを作成
        ServerSocket serverSocket;
        int connection_number = 0; // 接続者数

        // ソケット作成、サーバー起動、クライアント待ち受け、スレッド生成
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Serverが起動しました(port=" + serverSocket.getLocalPort() + ")");
            while (true) {
                try {
                    // クライアントからの接続を待ち受け（accept）
                    Socket socket = serverSocket.accept();
                    // 定員が上限に達していない限り接続を受け付け、ソケットを作成する
                    if (connection_number < MAX_CONNECTION) {
                        // out[connection_number] = new PrintWriter(socket.getOutputStream(), true);
                        data_out[connection_number] = new DataOutputStream(socket.getOutputStream());
                        obj_out[connection_number] = new ObjectOutputStream(socket.getOutputStream());
                        new ServerThread(socket, connection_number).start();
                        connection_number++;
                    }
                } catch (IOException e) {
                    System.out.println("ここ1");
                    e.printStackTrace();
                }
                // finally {
                // System.out.println("ここ2");
                // try {
                // if (serverSocket != null) {
                // serverSocket.close();
                // }
                // } catch (IOException e) {
                // e.printStackTrace();
                // }
                // }
            }
        } catch (Exception e) {
            System.out.println("ここ3");
            e.printStackTrace();
        }
    }

    public static void sendForAllPlayers_String(String message) {
        // for (PrintWriter pw : out) {
        // pw.println(message);
        // pw.flush();
        // }
        for (DataOutputStream dos : data_out) {
            try {
                dos.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendForAllPlayers_Object(Object obj) {
        for (ObjectOutputStream oos : obj_out) {
            try {
                oos.writeObject(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Dealer getDealer() {
        return dealer;
    }

    // public static void checkDealer() {
    // System.out.println(getDealer().getName(0));
    // System.out.println(getDealer().getName(1));
    // }
}

/*
 * <summary>
 * 各クライアントの処理
 * </summary>
 */
class ServerThread extends Thread {
    private int number;
    private Socket socket;

    public ServerThread(Socket socket, int number) {
        this.socket = socket;
        this.number = number;
    }

    public void run() {
        try {
            // クライアントからの受取用
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // クライアントへの送信用
            // PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // 名前が送られてくるので登録
            Server.userNames[number] = in.readLine();
            System.out.println(number + "番目：" + Server.userNames[number] + "さんが入室しました");
            // ユーザ番号を返す
            out.writeInt(number);
            // out.println(number);
            // out.flush();

            // 2人目のプレイヤーだった場合、2人を選手登録する
            if (number == Server.MAX_CONNECTION - 1) {
                Server.getDealer().setPlayerNames(Server.userNames);
                Server.dealer.setPlayerNames(Server.userNames);
                Server.getDealer().createDeck();
                // ゲーム開始
                // Server.sendForAllPlayers("START");
                Server.sendForAllPlayers_String("START");
                Server.sendForAllPlayers_Object(Server.userNames);
                // Server.sendForAllPlayers_String("START");
                // Server.sendForAllPlayers(Server.getDealer().getPlayerNames());
                // for (int i = 0; i < 2; i++) {
                // System.out.println(Server.getDealer().getName(i));
                // // System.out.println(Server.dealer.getName(i));
                // }
            }

            // 無限ループでソケットへの入力を監視する
            while (true) {
                // 送られてきたメッセージ読み込み
                String message = in.readLine();
                if (message != null) {
                    System.out.println(Server.userNames[number] + "：" + message);
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
}