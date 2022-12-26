import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
 * ユーザに表示するGUIを扱う
 */
public class GUI extends Frame implements ActionListener {
    // #region field
    // <summary> ウィンドウに表示するパネル </summary>
    private final int PANEL_NUMBER = 8;// 扱うパネルの数
    private final int LEFT = 0, RIGHT = 1, LEFT_UP = 2, LEFT_DOWN = 3,
            RIGHT_UP = 4, RIGHT_DOWN = 5, OPPONENTCARD = 6, USERCARD = 7;
    private Panel[] panels = new Panel[PANEL_NUMBER];
    // <summary> ウィンドウに表示するボタン </summary>
    // カード系
    private final int CARD_NUMBER = 5;// プレイヤーが所持できるカードの数
    private Button[] opponentCards = new Button[CARD_NUMBER];// 相手プレイヤーの所持カード
    private Button[] userCards = new Button[CARD_NUMBER];// プレイヤーの所持カード
    // 操作系
    private final int OPERATION_NUMBER = 2;// プレイヤーが選択できる行動数
    private final int EXCHANGE = 0, FINISH = 1;
    private Button[] operationButtons = new Button[OPERATION_NUMBER];
    // <summary> ウィンドウに表示するラベル </summary>
    private final int LABEL_NUMBER = 4;
    private final int OPPONENT_NAME = 0, USER_NAME = 1, TURN = 2, MESSAGE = 3;
    private JLabel[] labels = new JLabel[LABEL_NUMBER];
    // <summary> ウィンドウサイズ </summary>
    private final int WIDTH = 800, HIGHT = 400;
    // <summary> グリッドレイアウト </summary>
    private final int LAYOUT_ROWS = 1, LAYOUT_COLS = 2;// 左がカード表示 右がメッセージと操作ボタン表示
    private final int LAYOUT_LEFT_ROWS = 2, LAYOUT_LEFT_COLS = 1;// 上が相手のカード 下が自分のカードを表示
    private final int LAYOUT_CARD_ROWS = 1, LAYOUT_CARD_COLS = CARD_NUMBER;// カードは1行 カード枚数列表示
    private final int LAYOUT_RIGHT_ROWS = 2, LAYOUT_RIGHT_COLS = 1;// 上がメッセージ 下が操作ボタン表示
    private final int LAYOUT_MESSAGE_ROWS = 2, LAYOUT_MESSAGE_COLS = 1;// 上がターン数 下が順番と勝者表示
    private final int LAYOUT_OPERATION_ROWS = 1, LAYOUT_OPERATION_COLS = OPERATION_NUMBER;// 操作ボタンを横並びに表示
    // #endregion field

    private String userName;

    // #region constructor
    public GUI() {
        // // ここでdealerに名前を登録したい

        // // <summary> ウィンドウを作成 </summary>
        // setSize(WIDTH, HIGHT);
        // setTitle("Poker Game ( " + userName + " )");
        // setLayout(new GridLayout(LAYOUT_ROWS, LAYOUT_COLS));

        // // <summary> パネルを表示 </summary>
        // // 左（カード表示部部分）
        // panels[LEFT].setLayout(new GridLayout(LAYOUT_LEFT_ROWS, LAYOUT_LEFT_COLS));
        // panels[LEFT_UP].setLayout(new BorderLayout());
        // panels[LEFT_DOWN].setLayout(new BorderLayout());
        // panels[LEFT].add(panels[LEFT_UP]);
        // panels[LEFT].add(panels[LEFT_DOWN]);
        // add(panels[LEFT]);
        // // 右（メッセージ＋操作ボタン部分）
        // panels[RIGHT].setLayout(new GridLayout(LAYOUT_RIGHT_ROWS,
        // LAYOUT_RIGHT_COLS));
        // panels[RIGHT_UP].setLayout(new GridLayout(LAYOUT_MESSAGE_ROWS,
        // LAYOUT_MESSAGE_COLS));
        // panels[RIGHT_DOWN].setLayout(new GridLayout(LAYOUT_OPERATION_ROWS,
        // LAYOUT_OPERATION_COLS));
        // panels[RIGHT].add(panels[RIGHT_UP]);
        // panels[RIGHT].add(panels[RIGHT_DOWN]);
        // add(panels[RIGHT]);

        // // <summary> ボタンを表示 </summary>
        // // カード系
        // panels[OPPONENTCARD].setLayout(new GridLayout(LAYOUT_CARD_ROWS,
        // LAYOUT_CARD_COLS));
        // panels[LEFT_UP].add(panels[OPPONENTCARD], BorderLayout.CENTER);
        // for (Button card : opponentCards) {
        // card.setLabel("");// 相手のカードは伏せられる
        // panels[OPPONENTCARD].add(card);
        // }
        // panels[USERCARD].setLayout(new GridLayout(LAYOUT_CARD_ROWS,
        // LAYOUT_CARD_COLS));
        // panels[LEFT_DOWN].add(panels[USERCARD], BorderLayout.CENTER);
        // for (Button card : userCards) {
        // card.setLabel("");// まだカード配布されていないので空
        // panels[USERCARD].add(card);
        // }
        // // 操作系
        // operationButtons[EXCHANGE].setLabel("交換");
        // operationButtons[EXCHANGE].addActionListener(this);
        // panels[RIGHT_DOWN].add(operationButtons[EXCHANGE]);
        // operationButtons[FINISH].setLabel("ターン終了");
        // operationButtons[FINISH].addActionListener(this);
        // panels[RIGHT_DOWN].add(operationButtons[FINISH]);

        // // <summary> ラベルを表示 </summary>
        // // 対戦相手の名前
        // labels[OPPONENT_NAME].setText("");// ここでじゃなく後でディーラーに名前をもらいたい
        // labels[OPPONENT_NAME].setHorizontalAlignment(JLabel.CENTER);
        // panels[LEFT_UP].add(labels[OPPONENT_NAME], BorderLayout.NORTH);
        // // プレイヤーの名前
        // labels[USER_NAME].setText(userName);
        // labels[USER_NAME].setHorizontalAlignment(JLabel.CENTER);
        // panels[LEFT_DOWN].add(labels[USER_NAME], BorderLayout.SOUTH);
        // // ターン数
        // labels[TURN].setText("ターン");// ここでディーラーにターンをもらいたい
        // labels[TURN].setHorizontalAlignment(JLabel.CENTER);
        // panels[RIGHT_UP].add(labels[TURN]);
        // // メッセージ（順番が来ているユーザとか勝者とか）
        // labels[MESSAGE].setText("対戦相手を探しています");
        // labels[MESSAGE].setHorizontalAlignment(JLabel.CENTER);
        // panels[RIGHT_UP].add(labels[MESSAGE]);

        // // <summary> ウィンドウを閉じる処理 </summary>
        // addWindowListener(new WindowAdapter() {
        // public void windowClosing(WindowEvent we) {
        // System.exit(0);
        // }
        // });
    }
    // #endregion constructor

    // // #region getter
    // public String getUserName() {
    // return this.userName;
    // }

    // <summary> 名前の入力ダイアログを開いてユーザ名を取得 </summary>
    public String hearingUserName() {
        userName = JOptionPane.showInputDialog(null, "名前を入力してください", "名前の入力", JOptionPane.QUESTION_MESSAGE);
        if (userName.equals("")) {// 名前の入力がなかった場合はYOUと命名
            userName = "YOU";
        }
        return userName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }

}
