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
    // <summary> ウィンドウサイズ </summary>
    private final int WIDTH = 800, HIGHT = 400;
    // <summary> ウィンドウに表示するパネル </summary>
    private final int NUM_OF_PANEL = 8;// 扱うパネルの数
    private final int LEFT = 0, RIGHT = 1, LEFT_UP = 2, LEFT_DOWN = 3,
            RIGHT_UP = 4, RIGHT_DOWN = 5, OPPONENTCARD = 6, USERCARD = 7;
    private Panel[] panels = new Panel[NUM_OF_PANEL];
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
    private final int NUM_OF_LABEL = 5;
    private final int OPPONENT_NAME = 0, USER_NAME = 1, MESSAGE01 = 2, MESSAGE02 = 3, MESSAGE03 = 4;
    private JLabel[] labels = new JLabel[NUM_OF_LABEL];
    // <summary> グリッドレイアウト </summary>
    // 左がカード表示 右がメッセージと操作ボタン表示
    private final int LAYOUT_ROWS = 1, LAYOUT_COLS = 2;
    // 上が相手のカード 下が自分のカードを表示
    private final int LAYOUT_LEFT_ROWS = 2, LAYOUT_LEFT_COLS = 1;
    // カードは1行 カード枚数列表示
    private final int LAYOUT_CARD_ROWS = 1, LAYOUT_CARD_COLS = NUM_OF_CARD;
    // 上がメッセージ 下が操作ボタン表示
    private final int LAYOUT_RIGHT_ROWS = 2, LAYOUT_RIGHT_COLS = 1;
    // 01がターン数と勝者 02が順番と相手の最終的な役 03が自分の最終的な役表示
    private final int LAYOUT_MESSAGE_ROWS = 3, LAYOUT_MESSAGE_COLS = 1;
    // 操作ボタンは横並びに表示
    private final int LAYOUT_OPERATION_ROWS = 1, LAYOUT_OPERATION_COLS = NUM_OF_OPERATION;
    // <summary> プレイヤー名 </summary>
    private String playerName = "";
    // <summary> 行動可能かどうか </summary>
    private boolean isMyTurn = false;
    // <summary> 現状選択しているカードの位置 </summary>
    private final int NOT_SELECT = -1;// 選択されていないときは-1
    private int selectedCardNum = NOT_SELECT;
    // #endregion field

    // #region public function
    // <summary> 名前の入力ダイアログを開いてユーザ名を取得 </summary>
    public String hearingPlayerName() {
        playerName = JOptionPane.showInputDialog(null, "名前を入力してください", "名前の入力", JOptionPane.QUESTION_MESSAGE);
        if (playerName.equals("")) {// 名前の入力がなかった場合はYOUと命名
            playerName = "YOU";
        }
        return playerName;
    }

    // <summary> ゲームウィンドウを作成 </summary>
    public void createGameWindow() {
        // <summary> ゲームウィンドウを表示 </summary>
        setSize(WIDTH, HIGHT);
        setTitle("Poker Game ( " + playerName + " )");
        setLayout(new GridLayout(LAYOUT_ROWS, LAYOUT_COLS));

        // <summary> パネルを設置 </summary>
        // 初期化 → レイアウト設定 → 設置
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

        // <summary> ボタンを設置 </summary>
        // 初期化 → アクションリスナー設定 → レイアウト設定 → ラベル設定 → 設置
        // カード系
        for (int i = 0; i < NUM_OF_CARD; i++) {
            opponentCards[i] = new Button();
            userCards[i] = new Button();
            // 自身のカードのみ押された時反応する
            userCards[i].addActionListener(this);
            // アクションリスナーで取得できる値はラベルではなく左から何枚目か（数値）にする
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
            card.setLabel("");// ウィンドウ作成時はカード配布されていないので無
            panels[USERCARD].add(card);
        }
        // 操作系
        for (int i = 0; i < NUM_OF_OPERATION; i++) {
            operationButtons[i] = new Button();
        }
        operationButtons[EXCHANGE].addActionListener(this);
        operationButtons[EXCHANGE].setLabel("交換");
        panels[RIGHT_DOWN].add(operationButtons[EXCHANGE]);
        operationButtons[FINISH].addActionListener(this);
        operationButtons[FINISH].setLabel("ターン終了");
        panels[RIGHT_DOWN].add(operationButtons[FINISH]);

        // <summary> ラベルを設置 </summary>
        // 初期化 → ラベル設定 → レイアウト設定 → 設置
        for (int i = 0; i < NUM_OF_LABEL; i++) {
            labels[i] = new JLabel();
        }
        // 対戦相手の名前
        labels[OPPONENT_NAME].setText("");
        labels[OPPONENT_NAME].setHorizontalAlignment(JLabel.CENTER);
        panels[LEFT_UP].add(labels[OPPONENT_NAME], BorderLayout.NORTH);
        // プレイヤーの名前
        labels[USER_NAME].setText(playerName);
        labels[USER_NAME].setHorizontalAlignment(JLabel.CENTER);
        panels[LEFT_DOWN].add(labels[USER_NAME], BorderLayout.SOUTH);
        // ターン数と勝者
        labels[MESSAGE01].setText("");
        labels[MESSAGE01].setHorizontalAlignment(JLabel.CENTER);
        panels[RIGHT_UP].add(labels[MESSAGE01]);
        // メッセージ上部（順番が来ているユーザとか最終的な役とか）
        labels[MESSAGE02].setText("対戦相手を探しています...");
        labels[MESSAGE02].setHorizontalAlignment(JLabel.CENTER);
        panels[RIGHT_UP].add(labels[MESSAGE02]);
        // メッセージ下部（最終的な役とか）
        labels[MESSAGE03].setText("");
        labels[MESSAGE03].setHorizontalAlignment(JLabel.CENTER);
        panels[RIGHT_UP].add(labels[MESSAGE03]);

        // <summary> ウィンドウを閉じる処理 </summary>
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }
    // #endregion public function

    // #region getter setter
    // カード系
    // <summary> プレイヤーカードのラベルをセット </summary>
    public void setPlayerCard(Card[] cards) {
        for (int i = 0; i < cards.length; i++) {
            userCards[i].setLabel(cards[i].toString());
        }
    }

    // <summary> 相手カードのラベルをセット </summary>
    public void setOpponentCard(Card[] cards) {
        for (int i = 0; i < cards.length; i++) {
            opponentCards[i].setLabel(cards[i].toString());
        }
    }

    public void setPlayerLabel(String name) {
        labels[USER_NAME].setText(name);
    }

    public void setOpponentLabel(String name) {
        labels[OPPONENT_NAME].setText(name);
    }

    // <summary> 現在のターンをラベルにセット </summary>
    public void setTurnLabel(int next) {
        labels[MESSAGE01].setText(next + "ターンめ");
    }

    // <summary> 勝者をラベルにセット </summary>
    public void setWinnerName(String winner) {
        if (winner != null) {
            labels[MESSAGE01].setText(winner + "の勝ち");
        } else {
            labels[MESSAGE01].setText("引き分け");
        }
    }

    // <summary> 行動可能かラベルにセット </summary>
    public void setChangePlayerLabel(boolean next) {
        isMyTurn = next;
        if (next) {
            labels[MESSAGE02].setText("あなたの番です");
        } else {
            labels[MESSAGE02].setText("相手の番です");
        }
    }

    // <summary> 自分の最終的な役をラベルにセット </summary>
    public void setPlayerHand(String hand) {
        labels[MESSAGE03].setText(hand);
    }

    // <summary> 相手の最終的な役をラベルにセット </summary>
    public void setOpponentHand(String hand) {
        labels[MESSAGE02].setText(hand);
    }
    // #endregion getter setter

    // #region public function
    // <summary>ボタンが押された時の処理</summary>
    @Override
    public void actionPerformed(ActionEvent ae) {
        // 自分のターンでないなら処理しない
        if (!isMyTurn) {
            return;
        }
        String buttonType = ae.getActionCommand();
        // 交換が押されたら、
        if (buttonType == operationButtons[EXCHANGE].getLabel()) {
            // カードがまだ押されていないなら処理しない
            if (selectedCardNum != NOT_SELECT) {
                // クライアントにカード交換処理をお願いする
                Client.sendOpperation("EXCHANGE " + selectedCardNum);
                selectedCardNum = NOT_SELECT;
            }
            // ターン終了が押されたら、
        } else if (buttonType == operationButtons[FINISH].getLabel()) {
            // クライアントに行動終了処理をお願いする
            Client.sendOpperation("OPERATIONEND");
            // カードが押されたら、
        } else {
            // 選ばれているカードの番号を更新する
            selectedCardNum = Integer.parseInt(buttonType);
        }
    }
    // #endregion public function
}
