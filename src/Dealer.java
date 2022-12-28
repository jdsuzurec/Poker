
/*
 * <summary>
 * ゲームの情報と進行を管理
 * </summary>
 */
import java.util.Random;

public class Dealer {
    private static Dealer instance;
    /* 山札（deck）情報 */
    private final int NUM_OF_MARK = 4, NUM_OF_CARDNUM = 13;// マークの数、数字の数
    // private final int NUM_OF_CARDS = NUM_OF_MARK * NUM_OF_CARDNUM;// 山札総数
    private Card[][] deck = new Card[NUM_OF_MARK][NUM_OF_CARDNUM];
    /* <summary> プレイヤーたちの名前 <summary> */
    private String[] playerNames = new String[Server.getMAX_CONNECTION()];
    private Random random = new Random();
    /* 手札 */
    private final static int NUM_OF_CARD = 5;// プレイヤーが所持できるカードの数
    private Card[][] hands = new Card[playerNames.length][NUM_OF_CARD];

    private Dealer() {
        instance = null;
        System.out.println("Dealer生成！");
    }

    public static synchronized Dealer getInstance() {
        if (instance == null) {
            instance = new Dealer();
            instance.createDeck();
            instance.setHands();
        }
        return instance;
    }

    public void createDeck() {
        /* 山札の作成 */
        for (int mark = 0; mark < NUM_OF_MARK; mark++) {
            for (int number = 1; number <= NUM_OF_CARDNUM; number++) {
                deck[mark][number - 1] = new Card(mark, number);
            }
        }
        /* <summary> 山札生成確認 </summary> */
        System.out.println("山札を生成しました");
        // for (Card[] cards : deck) {
        // for (Card card : cards) {
        // System.out.print(card.toString() + ", ");
        // }
        // System.out.println();
        // }
    }

    // 全ユーザの手札を初期設定
    public void setHands() {
        System.out.println("手札を生成");
        for (int num_of_player = 0; num_of_player < hands.length; num_of_player++) {
            for (int num_of_card = 0; num_of_card < hands[num_of_player].length; num_of_card++) {
                hands[num_of_player][num_of_card] = dealOneCard();
            }
        }
    }

    // 現状手札を返す
    public Card[][] getHands() {
        return hands;
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

    public static int getNUM_OF_CARD() {
        return NUM_OF_CARD;
    }

    // ランダムに山札からカードを配る
    public Card dealOneCard() {
        // ランダムに山札からカードを取得（山札のカードが出てくるまでランダム値を振り直す）
        while (true) {
            int mark = random.nextInt(NUM_OF_MARK);
            int number = random.nextInt(NUM_OF_CARDNUM);
            Card card = deck[mark][number];
            if (!card.getIsHave()) {
                // カードの所持状態を真にしてから返す
                card.setIsHave(true);
                return card;
            }
        }
    }

    // 手札のカードを一枚山札に戻す
    public void releaseOneCard(int player_num, int index) {
        deck[player_num][index].setIsHave(false);
    }

    // 手札のカードと山札のカードを交換する
    public Card exchangeCard(int player_num, int index) {
        releaseOneCard(player_num, index);
        return dealOneCard();
    }
}
