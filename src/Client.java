import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/*
* <summary>
* サーバーと接続して、メッセージの送受信を行う
* </summary>
*/
public class Client {
    // #region field
    /* <summary> ポート番号 </summary> */
    final static int PORT_NUMBER = 10000;
    /* <summary> 最大接続数 </summary> */
    final private int MAX_CONNECTION = 2;
    /* <summary> プレイヤー名 </summary> */
    private static String playerName;
    /* <summary> ゲーム画面GUI </summary> */
    private static GUI gui;
    /* <summary> サーバーへの送信用 </summary> */
    private static PrintWriter out;
    // #endregion field

    // #region constructor
    public Client() {
        // ソケット作成
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
        // ユーザー名を取得してスレッド生成
        playerName = gui.hearingPlayerName();
        new ClientThread(socket, playerName).start();
    }
    // #endregion constructor

    // #region public function
    public static void main(String[] args) {
        // ゲーム画面を生成
        gui = new GUI();
        gui.setVisible(true);
        // スレッド作成
        new Client();
    }
    // #endregion public function

    /*
     * <summary>
     * 各スレッドの処理
     * </summary>
     */
    public class ClientThread extends Thread {
        // #region field
        /* <summary> ソケット </summary> */
        private Socket socket;
        /* <summary> プレイヤー名 </summary> */
        private String playerName;
        /* <summary> ディーラー </summary> */
        private Dealer dealer;
        /* <summary> ディーラー操作 </summary> */
        private DealerLogic dealerLogic = new DealerLogic();
        // #endregion field

        // #region constructor
        public ClientThread(Socket socket, String playerName) {
            this.socket = socket;
            this.playerName = playerName;
        }
        // #endregion constructor

        // #region public function
        public void run() {
            try {
                // サーバーからの受取用
                DataInputStream data_in = new DataInputStream(socket.getInputStream());
                ObjectInputStream obj_in = new ObjectInputStream(socket.getInputStream());
                // サーバーへの送信用
                out = new PrintWriter(socket.getOutputStream(), true);

                // 最初に名前の登録を行う
                out.println(playerName);
                // ユーザ番号を取得
                int userNumber = data_in.readInt();
                System.out.println("あなたは" + userNumber + "番目：" + playerName + "です");
                // ゲームウィンドウを作成
                gui.createGameWindow();

                // 無限ループでソケットへの入力を監視して、送られてきたデータを処理する
                while (true) {
                    // 送られてきたメッセージ読み込み
                    String message = data_in.readUTF();
                    if (message != null) {
                        System.out.println("サーバーからのメッセージ：" + message);
                        switch (message) {
                            // ゲーム開始
                            case "START":
                                System.out.println("ゲーム開始ですよ");
                                try {
                                    dealer = (Dealer) obj_in.readObject();
                                    // 名前をGUIに反映
                                    String[] playerNames = dealer.getPlayerNames();
                                    for (int i = 0; i < MAX_CONNECTION; i++) {
                                        if (i == userNumber) {
                                            gui.setPlayerLabel(playerNames[i]);
                                        } else {
                                            gui.setOpponentLabel(playerNames[i]);
                                        }
                                    }
                                    // 手札をGUIに反映
                                    gui.setPlayerCard(dealer.getHands()[userNumber]);
                                    // 行動可能プレイヤーをGUIに反映
                                    gui.setChangePlayerLabel(
                                            (dealer.getNum_Of_TurnPlayer() == userNumber ? true : false));
                                    // 現在のターン数をGUIに反映
                                    gui.setTurnLabel(dealer.getCount_Of_Turn());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            /* <summary>交換したカードを受け取ってGUIに反映</summary> */
                            case "EXCHANGE":
                                System.out.println("手札交換！");
                                try {
                                    int cardNum = data_in.readInt();
                                    int mark = data_in.readInt();
                                    int number = data_in.readInt();
                                    Card newCard = dealer.getDeck()[mark][number - 1];
                                    newCard = dealerLogic.exchangeCard(dealer, cardNum, newCard);
                                    gui.setPlayerCard(dealer.getHands()[userNumber]);
                                    if (dealer.getNum_Of_TurnPlayer() == userNumber) {
                                        out.println("OPERATIONEND");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "CONTINUEGAME":
                                try {
                                    dealerLogic.opperationEnd(dealer);
                                    // 先行後行反映
                                    gui.setChangePlayerLabel(
                                            (dealer.getNum_Of_TurnPlayer() == userNumber ? true : false));
                                    // ターン数反映
                                    gui.setTurnLabel(dealer.getCount_Of_Turn());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "GAMEEND":
                                gui.setChangePlayerLabel(false);
                                dealerLogic.gameEnd(dealer);
                                // 勝者をGUIに反映
                                String winnerPlayer = null;
                                if (dealer.getWinnerNumber() != -1) {
                                    winnerPlayer = dealer.getPlayerNames()[dealer.getWinnerNumber()];
                                }
                                gui.setWinnerName(winnerPlayer);
                                // 最終的なカードをGUIに反映
                                gui.setPlayerCard(dealer.getHands()[userNumber]);
                                gui.setOpponentCard(dealer.getHands()[userNumber == 0 ? 1 : 0]);
                                // 最終的な役をGUIに反映
                                gui.setPlayerHand(dealer.getPlayerNames()[userNumber] + " : "
                                        + dealer.getHandNames()[userNumber]);
                                gui.setOpponentHand(dealer.getPlayerNames()[userNumber == 0 ? 1 : 0] + " : "
                                        + dealer.getHandNames()[userNumber == 0 ? 1 : 0]);
                                break;
                            // default:
                            // break;
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
        }
    }

    /* <summary> GUIからカード交換やターン終了の命令を受け取ってサーバに送る </summary> */
    public static void sendOpperation(String opperation) {
        String[] opperation_split = opperation.split(" ");
        switch (opperation_split[0]) {
            case "EXCHANGE":
                System.out.println(opperation_split[0]);
                out.println(opperation_split[0]);// EXHANGE
                out.println(opperation_split[1]);// Card[]のindex
                break;
            case "OPERATIONEND":
                System.out.println(opperation);
                out.println(opperation);
                break;

            default:
                break;
        }
    }
    // #endregion public function
}