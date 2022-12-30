
/*
 * <summary>
 * ゲームの情報と進行を管理
 * </summary>
 */
import java.io.Serializable;

public class Dealer implements Serializable {
    /* <summary> 山札（deck）情報 </summary> */
    private final int NUM_OF_MARK = 4, NUM_OF_CARDNUM = 13;// マークの数、数字の数
    private Card[][] deck = new Card[NUM_OF_MARK][NUM_OF_CARDNUM];
    /* <summary> プレイヤーたちの名前 <summary> */
    private String[] playerNames = new String[Server.getMAX_CONNECTION()];
    // private Random random = new Random();
    /* <summary> 手札 </summary> */
    private final static int NUM_OF_CARD = 5;// プレイヤーが所持できるカードの数
    private Card[][] hands = new Card[playerNames.length][NUM_OF_CARD];
    private int count_of_turn = 0;
    private final int NUM_OF_TURNS = 5;
    // <summary> ユーザが操作した数（全体） 交換もしくは何もせず終了したら増える </summary>
    private int count_of_operations = 0;
    // operationsが↓に達したらゲーム終了
    private final int NUM_OF_OPERATIONS = NUM_OF_TURNS * playerNames.length;

    public Dealer() {
        System.out.println("Dealerインスタンス生成");
        // setDeck();
        // setFirstHands();
        // nextTurn();
    }

    public int getNUM_OF_MARK() {
        return NUM_OF_MARK;
    }

    public int getNUM_OF_CARDNUM() {
        return NUM_OF_CARDNUM;
    }

    /* <summary> 山札を作成する </summary> */
    public void setDeck(Card[][] deck) {
        this.deck = deck;
    }

    public void setDeckCard(int mark, int number, Card card) {
        deck[mark][number] = card;
    }

    public Card[][] getDeck() {
        return deck;
    }

    public void setPlayerNames(String[] playerNames) {
        this.playerNames = playerNames;
        System.out.println("プレイヤーを登録しました");
    }

    /* <summary> 全プレイヤー名を返す </summary> */
    public String[] getPlayerNames() {
        return playerNames;
    }

    /* <summary>1プレイヤーが所持できるカード枚数を返す </summary> */
    public static int getNUM_OF_CARD() {
        return NUM_OF_CARD;
    }

    /* <summary>現状の手札を返す</summary> */
    public Card[][] getHands() {
        return hands;
    }

    public void setHands(Card[][] hands) {
        this.hands = hands;
    }

    /* <summary> 指定場所の手札を指定のカードに入れ替える </summary> */
    public void setHand(int playerNum, int cardNum, Card card) {
        hands[playerNum][cardNum] = card;
    }

    /* <summary>現在のターン数を返す</summary> */
    public int getCount_Of_Turn() {
        return count_of_turn;
    }

    public void incrementCount_Of_Turn() {
        count_of_turn++;
    }

    public int getNUM_OF_TURNS() {
        return NUM_OF_TURNS;
    }

    public int getCount_Of_Operations() {
        return count_of_operations;
    }

    public void incrementCount_Of_Operations() {
        count_of_operations++;
    }

    public int getNUM_OF_OPERATIONS() {
        return NUM_OF_OPERATIONS;
    }

    /* <summary> 現在行動ターンが来ているユーザー番号を返す </summary> */
    public int getNum_Of_TurnUser() {
        return count_of_operations % playerNames.length;
    }

    // /*
    // * <summary>
    // * 指定プレイヤー番号のプレイヤー名を返す
    // * <param name="playerNumber">プレイヤー番号</param>
    // * </summary>
    // */
    // public String getName(int playerNumber) {
    // return playerNames[playerNumber];
    // }
}
