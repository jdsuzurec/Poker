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
    final static int PORT_NUMBER = 10000;
    final static int MAX_CONNECTION = 2;

    public static void main(String[] args) {
        // TCPポートを指定してサーバソケットを作成
        ServerSocket serverSocket;
        int connection_number = 0; // 接続者数
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
                    e.printStackTrace();
                } finally {
                    try {
                        if (serverSocket != null) {
                            serverSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
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
    private String userName;
    private Socket socket;

    public ServerThread(Socket socket, int number) {
        this.socket = socket;
        this.number = number;
        System.out.println(number + "番目のクライアントからの接続がありました。");
    }

    public void run() {
        try {
            // クライアントからの受取用
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // クライアントへの送信用
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // 無限ループでソケットへの入力を監視する
            while (true) {
                // 送られてきたメッセージ読み込み
                String message = in.readLine();
                if (message != null) {
                    // exitだったら終了
                    if (message.equals("exit")) {
                        break;
                    }
                    System.out.println("クライアントからのメッセージ：" + message);
                }
            }
        } catch (IOException e) {
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