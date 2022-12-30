import java.util.Arrays;
import java.util.List;

public class JudgementHand {
    /* <summary> 手札の判定 </summary> */
    public void judgementHand(Card[] cards) {
        System.out.println("判定：マークでソート");
        List<Card> cardList = Arrays.asList(cards);
        cardList.stream().sorted((card1, card2) -> {
            System.out.println(card1.getMark_Integer() + "と" + card2.getMark_Integer());
            int markResult = Integer.compare(card1.getMark_Integer(), card2.getMark_Integer());
            System.out.println(markResult);
            return markResult == 0 ? Integer.compare(card1.getNumber(), card2.getNumber()) : markResult;
        });
        for (Card card : cardList) {
            System.out.print(card.toString() + ", ");
        }
        System.out.println();
    }

}
