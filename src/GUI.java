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
    private final int NUM_OF_PANEL = 8;// 扱うパネルの数
    private final int LEFT = 0, RIGHT = 1, LEFT_UP = 2, LEFT_DOWN = 3,
            RIGHT_UP = 4, RIGHT_DOWN = 5, OPPONENTCARD = 6, USERCARD = 7;
    private Panel[] panels = new Panel[NUM_OF_PANEL];
    // private Panel[] panels;
    // <summary> ウィンドウに表示するボタン </summary>
    // カード系
    private final int NUM_OF_CARD = 5;// プレイヤーが所持できるカードの数
    private Button[] opponentCards = new Button[NUM_OF_CARD];// 相手プレイヤーの所持カード
    private Button[] userCards = new Button[NUM_OF_CARD];// プレイヤーの所持カード
    // 操作系
    private final int NUM_OF_OPERATION = 2;// プレイヤーが選択できる行動数
    private final int EXCHANGE = 0, FINISH = 1;
    private Button[] operationButtons = new Button[NUM_OF_OPERATION];
    // <summary> ウィンドウに表示するラベル </summary>
    private final int NUM_OF_LABEL = 4;
    private final int OPPONENT_NAME = 0, USER_NAME = 1, TURN = 2, MESSAGE = 3;
    private JLabel[] labels = new JLabel[NUM_OF_LABEL];
    // <summary> ウィンドウサイズ </summary>
    private final int WIDTH = 800, HIGHT = 400;
    // <summary> グリッドレイアウト </summary>
    private final int LAYOUT_ROWS = 1, LAYOUT_COLS = 2;// 左がカード表示 右がメッセージと操作ボタン表示
    private final int LAYOUT_LEFT_ROWS = 2, LAYOUT_LEFT_COLS = 1;// 上が相手のカード 下が自分のカードを表示
    private final int LAYOUT_CARD_ROWS = 1, LAYOUT_CARD_COLS = NUM_OF_CARD;// カードは1行 カード枚数列表示
    private final int LAYOUT_RIGHT_ROWS = 2, LAYOUT_RIGHT_COLS = 1;// 上がメッセージ 下が操作ボタン表示
    private final int LAYOUT_MESSAGE_ROWS = 2, LAYOUT_MESSAGE_COLS = 1;// 上がターン数 下が順番と勝者表示
    private final int LAYOUT_OPERATION_ROWS = 1, LAYOUT_OPERATION_COLS = NUM_OF_OPERATION;// 操作ボタンを横並びに表示
    // #endregion field

    private String userName = "";
    private boolean isMyTurn = false;
    private int selectedCardNum = 0;

    // <summary> 名前の入力ダイアログを開いてユーザ名を取得 </summary>
    public String hearingUserName() {
        userName = JOptionPane.showInputDialog(null, "名前を入力してください", "名前の入力", JOptionPane.QUESTION_MESSAGE);
        if (userName.equals("")) {// 名前の入力がなかった場合はYOUと命名
            userName = "YOU";
        }
        return userName;
    }

    // <summary> ゲームウィンドウを作成 </summary>
    public void createGameWindow() {
        setSize(WIDTH, HIGHT);
        setTitle("Poker Game ( " + userName + " )");
        setLayout(new GridLayout(LAYOUT_ROWS, LAYOUT_COLS));
        // <summary> パネルを表示 </summary>
        // 初期化
        for (int i = 0; i < NUM_OF_PANEL; i++) {
            panels[i] = new Panel();
        }
        // 左（カード表示部部分）
        panels[LEFT].setLayout(new GridLayout(LAYOUT_LEFT_ROWS, LAYOUT_LEFT_COLS));
        panels[LEFT_UP].setLayout(new BorderLayout());
        panels[LEFT_DOWN].setLayout(new BorderLayout());
        panels[LEFT].add(panels[LEFT_UP]);
        panels[LEFT].add(panels[LEFT_DOWN]);
        add(panels[LEFT]);
        // 右（メッセージ＋操作ボタン部分）
        panels[RIGHT].setLayout(new GridLayout(LAYOUT_RIGHT_ROWS,
                LAYOUT_RIGHT_COLS));
        panels[RIGHT_UP].setLayout(new GridLayout(LAYOUT_MESSAGE_ROWS,
                LAYOUT_MESSAGE_COLS));
        panels[RIGHT_DOWN].setLayout(new GridLayout(LAYOUT_OPERATION_ROWS,
                LAYOUT_OPERATION_COLS));
        panels[RIGHT].add(panels[RIGHT_UP]);
        panels[RIGHT].add(panels[RIGHT_DOWN]);
        add(panels[RIGHT]);

        // <summary> ボタンを表示 </summary>
        // カード系
        for (int i = 0; i < NUM_OF_CARD; i++) {
            opponentCards[i] = new Button();
            userCards[i] = new Button();
            userCards[i].addActionListener(this); // ボタンを押された時反応する
            userCards[i].setActionCommand(String.valueOf(i));
        }
        panels[OPPONENTCARD].setLayout(new GridLayout(LAYOUT_CARD_ROWS,
                LAYOUT_CARD_COLS));
        panels[LEFT_UP].add(panels[OPPONENTCARD], BorderLayout.CENTER);
        for (Button card : opponentCards) {
            card.setLabel("");// 相手のカードは伏せられる
            panels[OPPONENTCARD].add(card);
        }
        panels[USERCARD].setLayout(new GridLayout(LAYOUT_CARD_ROWS,
                LAYOUT_CARD_COLS));
        panels[LEFT_DOWN].add(panels[USERCARD], BorderLayout.CENTER);
        for (Button card : userCards) {
            card.setLabel("");// まだカード配布されていないので空
            panels[USERCARD].add(card);
        }
        // 操作系
        for (int i = 0; i < NUM_OF_OPERATION; i++) {
            operationButtons[i] = new Button();
        }
        operationButtons[EXCHANGE].setLabel("交換");
        operationButtons[EXCHANGE].addActionListener(this);
        panels[RIGHT_DOWN].add(operationButtons[EXCHANGE]);
        operationButtons[FINISH].setLabel("ターン終了");
        operationButtons[FINISH].addActionListener(this);
        panels[RIGHT_DOWN].add(operationButtons[FINISH]);

        // <summary> ラベルを表示 </summary>
        for (int i = 0; i < NUM_OF_LABEL; i++) {
            labels[i] = new JLabel();
        }
        // 対戦相手の名前
        labels[OPPONENT_NAME].setText("");
        labels[OPPONENT_NAME].setHorizontalAlignment(JLabel.CENTER);
        panels[LEFT_UP].add(labels[OPPONENT_NAME], BorderLayout.NORTH);
        // プレイヤーの名前
        labels[USER_NAME].setText(userName);
        labels[USER_NAME].setHorizontalAlignment(JLabel.CENTER);
        panels[LEFT_DOWN].add(labels[USER_NAME], BorderLayout.SOUTH);
        // ターン数
        labels[TURN].setText("");
        labels[TURN].setHorizontalAlignment(JLabel.CENTER);
        panels[RIGHT_UP].add(labels[TURN]);
        // メッセージ（順番が来ているユーザとか勝者とか）
        labels[MESSAGE].setText("対戦相手を探しています...");
        labels[MESSAGE].setHorizontalAlignment(JLabel.CENTER);
        panels[RIGHT_UP].add(labels[MESSAGE]);

        // <summary> ウィンドウを閉じる処理 </summary>
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public void setUserLabel(String name) {
        labels[USER_NAME].setText(name);
    }

    public void setOpponentLabel(String name) {
        labels[OPPONENT_NAME].setText(name);
    }

    public void setUserCard(int index, Card card) {
        userCards[index].setLabel(card.toString());// まだカード配布されていないので空
    }

    public void setMessageLabel(boolean next) {
        isMyTurn = next;
        if (next) {
            labels[MESSAGE].setText("あなたの番です");
        } else {
            labels[MESSAGE].setText("相手の番です");
        }
    }

    public void setTurnLabel(int next) {
        labels[TURN].setText(next + "ターンめ");
    }

    public void setResult(String winner) {
        if (winner != null) {
            labels[TURN].setText(winner + "の勝ち");
        } else {
            labels[TURN].setText("引き分け");
        }
    }

    // <summary>ボタンが押された時の処理</summary>
    @Override
    public void actionPerformed(ActionEvent ae) {
        // 自分のターンでないなら処理しない
        if (!isMyTurn) {
            return;
        }
        String buttonType = ae.getActionCommand();
        if (buttonType == operationButtons[EXCHANGE].getLabel()) {
            // 交換が押された
            Client.sendOpperation("EXCHANGE " + selectedCardNum);
        } else if (buttonType == operationButtons[FINISH].getLabel()) {
            // ターン終了が押された
            Client.sendOpperation("OPERATIONEND");
        } else {
            // カードが押された
            selectedCardNum = Integer.parseInt(buttonType);
        }
    }

}
