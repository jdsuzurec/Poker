import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    final protected static int MAX_CONNECTION = 2;
    protected static Dealer dealer;
    protected static String[] userNames = new String[MAX_CONNECTION];

    public static void main(String[] args) {
        // TCPポートを指定してサーバソケットを作成
        ServerSocket serverSocket;
        int connection_number = 0; // 接続者数
        // ディーラーを生成
        dealer = new Dealer();

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
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 名前が送られてくるので登録
            Server.userNames[number] = in.readLine();
            System.out.println(number + "番目：" + Server.userNames[number] + "さんが入室しました");

            // 2人目のプレイヤーだった場合、2人を選手登録する
            if (number == Server.MAX_CONNECTION - 1) {
                Server.dealer.setPlayerNames(Server.userNames);
            }
            // 名前を配る
            // 手札を配る

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