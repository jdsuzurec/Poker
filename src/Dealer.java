/*
 * <summary>
 * ゲームの情報と進行を管理
 * </summary>
 */
public class Dealer {
    private final int MARK_NUMBER = 4, CARD_NUMBER = 13;
    private final int SPADE = 0, HEART = 1, DIAMOND = 2, CLOVER = 3;

    private String[] playerNames;
    private Card[] deck;

    public Dealer() {
        for (int mark = 0; mark < MARK_NUMBER; mark++) {
            for (int number = 0; number < CARD_NUMBER; number++) {
                // 山札を作成
            }
        }
    }

    public void setPlayerNames(String[] playerNames) {
        this.playerNames = playerNames;
    }
}
