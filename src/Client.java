import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.io.IOException;

/*
* <summary>
* サーバーと接続して、メッセージの送受信を行う
* </summary>
*/
public class Client {
    final static int PORT_NUMBER = 10000;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        Socket socket = null;
        try {
            socket = new Socket("localhost", PORT_NUMBER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ClientThread(socket).start();
    }

    public class ClientThread extends Thread {
        private Socket socket;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // サーバーからの受取用
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // サーバーへの送信用
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // 無限ループでソケットへの入力を監視する

                // 送られてきたメッセージを処理する
                while (true) {
                    // 送られてきたメッセージ読み込み
                    String message = in.readLine();
                    if (message != null) {
                        // exitだったら終了
                        if (message.equals("exit")) {
                            break;
                        }
                        System.out.println("サーバーからのメッセージ：" + message);
                    }
                }
                socket.close();
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
            }
        }
    }
}