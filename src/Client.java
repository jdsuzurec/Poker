import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
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
    final private int MAX_CONNECTION = 2;
    private static String userName;
    private static GUI gui;
    private static PrintWriter out;

    public static void main(String[] args) {
        gui = new GUI();
        gui.setVisible(true);
        new Client();
    }

    // ソケット作成、ユーザー名を取得してスレッド生成
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
        private int userNumber;

        public ClientThread(Socket socket, String userName) {
            this.socket = socket;
            this.userName = userName;
        }

        public void run() {
            try {
                // サーバーからの受取用
                DataInputStream data_in = new DataInputStream(socket.getInputStream());
                ObjectInputStream obj_in = new ObjectInputStream(socket.getInputStream());
                // BufferedReader in = new BufferedReader(new
                // InputStreamReader(socket.getInputStream()));
                // サーバーへの送信用
                out = new PrintWriter(socket.getOutputStream(), true);
                // 最初に名前の登録を行う
                out.println(userName);
                // ユーザ番号を取得
                userNumber = data_in.readInt();
                System.out.println("あなたは" + userNumber + "番目：" + userName + "です");

                // ゲームウィンドウを作成
                gui.createGameWindow();
                // プレイヤーたちの名前を反映
                // 配布されたカードを反映、

                // 無限ループでソケットへの入力を監視して、送られてきたデータを処理する
                while (true) {
                    // 送られてきたメッセージ読み込み
                    // String message = in.readLine();
                    String message = data_in.readUTF();
                    if (message != null) {
                        System.out.println("サーバーからのメッセージ：" + message);
                        switch (message) {
                            case "START":
                                System.out.println("ゲーム開始ですよ");
                                try {
                                    Dealer dealer = (Dealer) obj_in.readObject();
                                    // 名前を配って表示させる
                                    String[] playerNames = dealer.getPlayerNames();
                                    for (int i = 0; i < MAX_CONNECTION; i++) {
                                        if (i == userNumber) {
                                            gui.setUserLabel(playerNames[i]);
                                        } else {
                                            gui.setOpponentLabel(playerNames[i]);
                                        }
                                    }
                                    // 手札を配る
                                    Card[][] hands = dealer.getHands();
                                    System.out.print("最初の手札：");
                                    for (int i = 0; i < hands[userNumber].length; i++) {
                                        // guiに反映
                                        gui.setUserCard(i, hands[userNumber][i]);
                                        System.out.print(hands[userNumber][i].toString() + ", ");
                                    }
                                    System.out.println();
                                    // 先行後行反映
                                    gui.setMessageLabel((dealer.getNum_Of_TurnUser() == userNumber ? true : false));
                                    // ターン数反映
                                    gui.setTurnLabel(dealer.getTurn());

                                } catch (Exception e) {
                                    e.printStackTrace();
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
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // finally {
            // if (socket != null) {
            // try {
            // socket.close();
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
            // }
            // }
        }
    }

    /* <summary> GUIからカード交換やターン終了の命令を受け取ってサーバに送る </summary> */
    public static void sendOpperation(String opperation) {
        String[] opperation_split = opperation.split(" ");
        switch (opperation_split[0]) {
            case "EXCHANGE":
                System.out.println(opperation_split[0]);
                System.out.println(opperation_split[1]);
                out.println(opperation_split[0]);
                out.println(opperation_split[1]);
                break;
            case "TURNEND":
                System.out.println(opperation);
                out.println(opperation);
                break;

            default:
                break;
        }
    }
}