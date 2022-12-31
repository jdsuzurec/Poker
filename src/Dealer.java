
/*
 * <summary>
 * ゲーム情報（Model）
 * </summary>
 */
import java.io.Serializable;

public class Dealer implements Serializable {
    // #region field
    /* <summary> 山札（deck）情報 </summary> */
    private final int NUM_OF_SUIT = 4, NUM_OF_NUM = 13;// スーツの数、数字の数
    private Card[][] deck = new Card[NUM_OF_SUIT][NUM_OF_NUM];
    /* <summary> プレイヤーの名前情報 <summary> */
    private final int NUM_OF_PLAYER = Server.getMAX_CONNECTION();
    private String[] playerNames = new String[NUM_OF_PLAYER];
    /* <summary> 手札情報 </summary> */
    private final int NUM_OF_CARD = 5;// プレイヤーのカード所持数
    private Card[][] hands = new Card[NUM_OF_PLAYER][NUM_OF_CARD];// 手札
    /* <summary> 勝敗判断要素 </summary> */
    private int[] handStrength = new int[NUM_OF_PLAYER];// 最終的な役の強さ（数値）
    private String[] handNames = new String[NUM_OF_PLAYER];// 最終的な役の名称
    private int winnerNumber;// 勝者のプレイヤー番号（-1なら引き分け）
    /* <summary> 進行状況情報 </summary> */
    private final int NUM_OF_TURNS = 5;// ゲーム終了までのターン
    private int count_of_turn = 0;// 現在のターン
    private final int NUM_OF_OPERATIONS = NUM_OF_TURNS * NUM_OF_PLAYER;// ゲーム終了までのプレイヤー行動数
    private int count_of_operations = 0;// 現在のプレイヤー行動回数
    // #endregion field

    // #region constructor
    public Dealer() {
        System.out.println("Dealerインスタンス生成");
    }
    // #endregion constructor

    // #region getter setter
    /* <summary> 山札（deck）情報 </summary> */
    public int getNUM_OF_SUIT() {
        return NUM_OF_SUIT;
    }

    public int getNUM_OF_NUM() {
        return NUM_OF_NUM;
    }

    public Card[][] getDeck() {
        return deck;
    }

    public void setDeck(Card[][] deck) {
        this.deck = deck;
    }

    // 指定の場所（山札）に一枚セット
    public void setDeckCard(int mark, int number, Card card) {
        deck[mark][number] = card;
    }

    /* <summary> プレイヤーの名前情報 <summary> */
    public int getNUM_OF_PLAYER() {
        return NUM_OF_PLAYER;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(String[] playerNames) {
        this.playerNames = playerNames;
        System.out.println("プレイヤーを登録しました");
    }

    /* <summary> 手札情報 </summary> */
    public int getNUM_OF_CARD() {
        return NUM_OF_CARD;
    }

    public Card[][] getHands() {
        return hands;
    }

    public void setHands(Card[][] hands) {
        this.hands = hands;
    }

    // 指定場所（手札）に一枚セット
    public void setHand(int playerNum, int cardNum, Card card) {
        hands[playerNum][cardNum] = card;
    }

    /* <summary> 勝敗判断要素 </summary> */
    public int[] getHandStrength() {
        return handStrength;
    }

    public void setHandStrength(int[] handStrength) {
        this.handStrength = handStrength;
    }

    public String[] getHandNames() {
        return handNames;
    }

    public void setHandNames(String[] handNames) {
        this.handNames = handNames;
    }

    public int getWinnerNumber() {
        return winnerNumber;
    }

    public void setWinnerNumber(int winnerNumber) {
        this.winnerNumber = winnerNumber;
    }

    /* <summary> 進行状況情報 </summary> */
    public int getNUM_OF_TURNS() {
        return NUM_OF_TURNS;
    }

    public int getCount_Of_Turn() {
        return count_of_turn;
    }

    public int getNUM_OF_OPERATIONS() {
        return NUM_OF_OPERATIONS;
    }

    public int getCount_Of_Operations() {
        return count_of_operations;
    }

    // 現在行動ターンが来ているユーザー番号を返す
    public int getNum_Of_TurnUser() {
        return count_of_operations % NUM_OF_PLAYER;
    }
    // #region getter setter

    // #region public function
    public void incrementCount_Of_Turn() {
        count_of_turn++;
    }

    public void incrementCount_Of_Operations() {
        count_of_operations++;
    }
    // #endregion public function
}
