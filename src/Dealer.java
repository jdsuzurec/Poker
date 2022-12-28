/*
 * <summary>
 * ゲームの情報と進行を管理
 * </summary>
 */
public class Dealer {
    private static Dealer instance;
    /* 山札（deck）情報 */
    private final int MARK_NUMBER = 4, CARD_NUMBER = 13;// マークの数、数字の数
    private final int NUMBER_OF_CARDS = MARK_NUMBER * CARD_NUMBER;// 山札総数
    private Card[] deck = new Card[NUMBER_OF_CARDS];
    /* <summary> プレイヤーたちの名前 <summary> */
    private String[] playerNames = new String[Server.MAX_CONNECTION];

    private Dealer() {
        instance = null;
        System.out.println("コンストラクタ！");
    }

    public static synchronized Dealer getInstance() {
        if (instance == null) {
            instance = new Dealer();
        }
        return instance;
    }

    public void createDeck() {
        /* 山札の作成 */
        int number_of_cards = 0;
        for (int mark = 0; mark < MARK_NUMBER; mark++) {
            for (int number = 1; number <= CARD_NUMBER; number++) {
                deck[number_of_cards] = new Card(mark, number);
                number_of_cards++;
            }
        }
        /* <summary> 山札生成確認 </summary> */
        System.out.println("山札を生成しました");
        // for (Card card : deck) {
        // System.out.println(card.getMark() + " : " + card.getNumberString());
        // }
    }

    public void setPlayerNames(String[] playerNames) {
        this.playerNames = playerNames;
        System.out.println("プレイヤーを登録しました");
        // for (String name : playerNames) {
        // System.out.println(name);
        // }
    }

    public String[] getPlayerNames() {
        return this.playerNames;
    }

    public String getName(int playerNumber) {
        return playerNames[playerNumber];
    }

    public static Dealer getDealer() {
        return instance;
    }
    // 手札を配る
}
