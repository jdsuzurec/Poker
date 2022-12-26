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
    private static String userName;
    private static GUI gui;

    public static void main(String[] args) {
        gui = new GUI();
        new Client();
    }

    public Client() {
        Socket socket = null;
        try {
            socket = new Socket("localhost", PORT_NUMBER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.err.println("ホストの IP アドレスが判定できません: " + e);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("エラーが発生しました: " + e);
        }
        userName = gui.hearingUserName();
        new ClientThread(socket, userName).start();
    }

    public class ClientThread extends Thread {
        private Socket socket;
        private String userName;

        public ClientThread(Socket socket, String userName) {
            this.socket = socket;
            this.userName = userName;
        }

        public void run() {
            try {
                // サーバーからの受取用
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // サーバーへの送信用
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // 最初に名前の登録を行う
                out.println(userName);

                // 返事を取得
                String message = in.readLine();
                System.out.println(message);

                // 無限ループでソケットへの入力を監視する
                // 送られてきたメッセージを処理する
                // while (true) {
                // // 送られてきたメッセージ読み込み
                // String message = in.readLine();
                // if (message != null) {
                // // exitだったら終了
                // if (message.equals("exit")) {
                // break;
                // }
                // System.out.println("サーバーからのメッセージ：" + message);
                // }
                // }
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