
/*
 * <summary>
 * ゲームの情報と進行を管理
 * </summary>
 */
import java.util.Random;
import java.io.Serializable;

public class Dealer implements Serializable {
    /* <summary> 山札（deck）情報 </summary> */
    private final int NUM_OF_MARK = 4, NUM_OF_CARDNUM = 13;// マークの数、数字の数
    private Card[][] deck = new Card[NUM_OF_MARK][NUM_OF_CARDNUM];
    /* <summary> プレイヤーたちの名前 <summary> */
    private String[] playerNames = new String[Server.getMAX_CONNECTION()];
    private Random random = new Random();
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
        createDeck();
        setFirstHands();
        nextTurn();
    }

    /*
     * <summary>
     * 山札を作成する
     * </summary>
     */
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

    /*
     * <summary>
     * ターンを進行する
     * </summary>
     */
    private void nextTurn() {
        count_of_turn++;
    }

    /*
     * <summary>
     * 現在のターン数を返す
     * </summary>
     */
    public int getTurn() {
        return count_of_turn;
    }

    /*
     * <summary>
     * 現在行動ターンが来ているユーザー番号を返す
     * </summary>
     */
    public int getNum_Of_TurnUser() {
        return count_of_operations % playerNames.length;
    }

    /*
     * <summary>
     * 全プレイヤーの最初の手札を決める
     * </summary>
     */
    public void setFirstHands() {
        System.out.println("手札を生成");
        for (int num_of_player = 0; num_of_player < hands.length; num_of_player++) {
            for (int num_of_card = 0; num_of_card < hands[num_of_player].length; num_of_card++) {
                hands[num_of_player][num_of_card] = dealOneCard();
            }
        }
    }

    /*
     * <summary>
     * 現状の手札を返す
     * </summary>
     */
    public Card[][] getHands() {
        return hands;
    }

    public void setPlayerNames(String[] playerNames) {
        this.playerNames = playerNames;
        System.out.println("プレイヤーを登録しました");
    }

    /*
     * <summary>
     * 全プレイヤー名を返す
     * </summary>
     */
    public String[] getPlayerNames() {
        return playerNames;
    }

    /*
     * <summary>
     * 指定プレイヤー番号のプレイヤー名を返す
     * <param name="playerNumber">プレイヤー番号</param>
     * </summary>
     */
    public String getName(int playerNumber) {
        return playerNames[playerNumber];
    }

    /*
     * <summary>
     * 1プレイヤーが所持できるカード枚数を返す
     * </summary>
     */
    public static int getNUM_OF_CARD() {
        return NUM_OF_CARD;
    }

    /*
     * <summary>
     * ランダムに山札から1枚カードを返す
     * </summary>
     */
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

    /*
     * <summary>
     * 手札のカードを山札に戻す
     * </summary>
     * <param name="numOfExchangedCard">交換するカードが何枚目か</param>
     */
    public void releaseOneCard(int numOfExchangedCard) {
        // 現在行動ターンが来ているユーザのカードの所持状態を偽にする
        System.out.println("返すカード:" + hands[getNum_Of_TurnUser()][numOfExchangedCard].toString());
        deck[getNum_Of_TurnUser()][numOfExchangedCard].setIsHave(false);
        hands[getNum_Of_TurnUser()][numOfExchangedCard] = null;
    }

    /*
     * <summary>
     * 手札のカードと山札のカードを交換する
     * </summary>
     * <param name="numOfExchangedCard">交換するカードが何枚目か</param>
     */
    public Card exchangeCard(int numOfExchangedCard) {
        // 指定のカードを山札に戻す
        releaseOneCard(numOfExchangedCard);
        // 山札から新たなカードを引いて返す
        hands[getNum_Of_TurnUser()][numOfExchangedCard] = dealOneCard();
        System.out.println("新たなカード:" + hands[getNum_Of_TurnUser()][numOfExchangedCard]);
        System.out.println();
        printHands();
        return hands[getNum_Of_TurnUser()][numOfExchangedCard];
    }

    public void setCard(int numOfExchangedCard, int playerNumber, int mark, int number) {
        Card releaseCard = hands[playerNumber][numOfExchangedCard];
        releaseCard.setIsHave(false);
        Card getCard = deck[mark][number - 1];
        getCard.setIsHave(true);
        hands[playerNumber][numOfExchangedCard] = getCard;
    }

    public String TurnEnd() {
        // プレイヤーが操作した総数を増やす
        count_of_operations++;
        // プレイヤー数で割った余りが0なら全員行動した後なので次のターンに進む
        if (count_of_operations % playerNames.length == 0) {
            // ただし最終ターンで全ユーザの行動が終了したらゲーム終了
            if (NUM_OF_OPERATIONS == count_of_operations) {
                return "GAMEEND";
            }
            nextTurn();
            // ターンが変わって操作するプレイヤーも変わる
            return "NEXTTURN";
        }
        // ターンが変わらずに操作するプレイヤーが変わる
        return "CHANGEPLAYER";
    }

    public void printHands() {
        for (Card[] cards : hands) {
            for (Card card : cards) {
                System.out.print(card.toString() + "( " + card.getIsHave() + " ), ");
            }
            System.out.println();
        }
    }

    public void printDeck() {
        for (Card[] cards : deck) {
            for (Card card : cards) {
                System.out.print(card.toString() + "( " + card.getIsHave() + " ), ");
            }
            System.out.println();
        }
    }
}
