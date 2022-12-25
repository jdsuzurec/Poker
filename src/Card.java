import java.util.Arrays;

/*
 * <summary>
 * カードに関する情報を保持
 * </summary>
 */
public class Card {
    private final int MARK_NUMBER = 4, CARD_NUMBER = 13;
    private final int SPADE = 0, HEART = 1, DIAMOND = 2, CLOVER = 3;
    private final int A = 1, J = 11, Q = 12, K = 13;
    private boolean[][] deck = new boolean[MARK_NUMBER][CARD_NUMBER];

    public Card() {
        for (boolean[] isHave : deck) {
            Arrays.fill(isHave, false);
        }
    }
}
