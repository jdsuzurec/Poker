import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JudgementHand {
    private final int ROYAL_FLUSH = 0, STRAIGHT_FLUSH = 1, FOUR_OF_A_KIND = 2, FULL_HOUSE = 3,
            FLUSH = 4, STRAIGHT = 5, THREE_OF_A_KIND = 6, TWO_PAIR = 7, A_PAIR = 8, HIGH_CARD = 9;

    /* <summary> 手札の判定 </summary> */
    public int judgementHand(Card[] cards) {
        // booleanに応じて上記intを返す 数字が小さい方がつよい
        if (isFlush(cards)) {
            return FLUSH;
        }
        return ROYAL_FLUSH;
    }

    /* <summary> フラッシュ（一種類のスーツだけで構成された役）か判定 </summary> */
    private boolean isFlush(Card[] cards) {
        int tempMark = cards[0].getMark_Integer();
        for (Card card : cards) {
            if (card.getMark_Integer() != tempMark) {
                return false;
            }
        }
        return true;
    }

    private List<Card> sortCardMark(Card[] cards) {
        System.out.println("判定：マークでソート");
        List<Card> cardList = Arrays.asList(cards);
        Collections.sort(cardList, (card1, card2) -> {
            if (card1.getMark_Integer() == card2.getMark_Integer()) {
                return card1.getNumber() - card2.getNumber();
            }
            return card1.getMark_Integer() - card2.getMark_Integer();
        });
        System.out.println(cardList);
        return cardList;
    }

    public static void main(String[] args) {
        Dealer dealer = new Dealer();
        DealerLogic dealerLogic = new DealerLogic();
        JudgementHand judgementHand = new JudgementHand();
        // dealerLogic.gameStart(dealer);
        // Card[][] hands = dealer.getHands();
        // dealerLogic.printHands(dealer);
        Card[] cards = { new Card(0, 0), new Card(0, 1), new Card(0, 2), new Card(0, 3), new Card(0, 4) };
        System.out.println(judgementHand.judgementHand(cards));

        // judgementHand.judgementHand(hands[1]);
    }

}
