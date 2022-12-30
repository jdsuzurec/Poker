import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JudgementHand {
    private final int ROYAL_FLUSH = 0, STRAIGHT_FLUSH = 1, FOUR_OF_A_KIND = 2, FULL_HOUSE = 3,
            FLUSH = 4, STRAIGHT = 5, THREE_OF_A_KIND = 6, TWO_PAIR = 7, A_PAIR = 8, HIGH_CARD = 9;

    /* <summary> 手札の判定 </summary> */
    public int judgementHand(Card[] cards) {
        // booleanに応じて上記intを返す 数字が小さい方がつよい
        if (isRoyal_Flush(cards)) {
            return ROYAL_FLUSH;
        }
        if (isFlush(cards)) {
            return FLUSH;
        }
        return HIGH_CARD;

    }

    /* <summary> ロイヤルストレートフラッシュ（一種類のスーツで最も数位の高い5枚が揃った役）か判定 </summary> */
    private boolean isRoyal_Flush(Card[] cards) {
        // 一種類のスーツ
        if (!isFlush(cards)) {
            return false;
        }
        List<Card> sortNumCardList = sortCardNum(cards);
        if (sortNumCardList.get(0).getNumber() == 10 && sortNumCardList.get(4).getNumber() == 1) {
            return true;
        }
        return false;
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

    private List<Card> sortCardNum(Card[] cards) {
        System.out.println("数位で昇順ソート");
        List<Card> cardList = Arrays.asList(cards);
        Collections.sort(cardList, (card1, card2) -> {
            // Aが一番強い
            if (card1.getNumber() == 1 || card2.getNumber() == 1) {
                return card2.getNumber() - card1.getNumber();
            }
            return card1.getNumber() - card2.getNumber();
        });
        System.out.println(cardList);
        return cardList;
    }

    private List<Card> sortCardMark(Card[] cards) {
        System.out.println("マークでソート");
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
        Card[] cards1 = { new Card(0, 1), new Card(0, 6), new Card(0, 2), new Card(0, 3), new Card(0, 4) };
        System.out.println(judgementHand.judgementHand(cards1));
        Card[] cards2 = { new Card(0, 1), new Card(0, 10), new Card(0, 12), new Card(0, 11), new Card(0, 13) };
        System.out.println(judgementHand.judgementHand(cards2));
        // judgementHand.judgementHand(hands[1]);
    }

}
