import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JudgementHand {
    private final int ROYAL_FLUSH = 0, STRAIGHT_FLUSH = 1, FOUR_OF_A_KIND = 2, FULL_HOUSE = 3,
            FLUSH = 4, STRAIGHT = 5, THREE_OF_A_KIND = 6, TWO_PAIR = 7, A_PAIR = 8, HIGH_CARD = 9;
    private List<Card> sortNumCardList = new ArrayList<Card>();
    private int setSize;

    private void setSetSize(int setSize) {
        this.setSize = setSize;
    }

    private int getSetSize() {
        return setSize;
    }

    private int getSetSize(List<Card> cardList) {
        Set<Integer> cardSet = new HashSet<Integer>();
        for (Card card : cardList) {
            cardSet.add(card.getNumber());
        }
        return cardSet.size();
    }

    private void setSortNumCardList(List<Card> sortNumCardList) {
        this.sortNumCardList = sortNumCardList;
    }

    private List<Card> getSortNumCardList() {
        return sortNumCardList;
    }

    public String getHand_Str(int strength) {
        switch (strength) {
            case ROYAL_FLUSH:
                return "ロイヤルストレートフラッシュ";
            case STRAIGHT_FLUSH:
                return "ストレートフラッシュ";
            case FOUR_OF_A_KIND:
                return "フォーペア";
            case FULL_HOUSE:
                return "フルハウス";
            case FLUSH:
                return "フラッシュ";
            case STRAIGHT:
                return "ストレート";
            case THREE_OF_A_KIND:
                return "スリーペア";
            case TWO_PAIR:
                return "ツーペア";
            case A_PAIR:
                return "ワンペア";
        }
        return "ハイカード";
    }

    /* <summary> 手札の判定 </summary> */
    public int judgementHand(Card[] cards) {
        // 役の強さ
        int hand = HIGH_CARD;

        // 数位昇順ソート(2,3,4,...Q,K,A)
        setSortNumCardList(sortCardNum(cards));
        // booleanに応じて上記intを返す 数字が小さい方がつよい
        // 一種類のスーツ
        if (isFlush(cards)) {
            hand = Math.min(hand, FLUSH);
            // 数位が高位ならロイヤルストレートフラッシュ
            if (isRoyal_Flush(cards)) {
                return ROYAL_FLUSH;
            }
            // フラッシュかつストレートならストレートフラッシュ
            if (isStraight(cards)) {
                return STRAIGHT_FLUSH;
            }
        }
        if (isStraight(cards)) {
            hand = Math.min(hand, STRAIGHT);
        }

        // 同じ数値がいくつあるか
        setSetSize(getSetSize(getSortNumCardList()));
        switch (getSetSize()) {
            case 2:
                // 連長圧縮した時の最大数が4ならフォーカード、3ならフルハウス
                for (int i = 0; i < getSortNumCardList().size();) {
                    int numOfContinue = 0;
                    int j = i;
                    while (j < getSortNumCardList().size()
                            && getSortNumCardList().get(i).getNumber() == getSortNumCardList().get(j).getNumber()) {
                        j++;
                    }
                    numOfContinue = j - i;
                    if (numOfContinue == 4) {
                        return FOUR_OF_A_KIND;
                    }
                    if (numOfContinue == 2 || numOfContinue == 3) {
                        return FULL_HOUSE;
                    }
                    i = j;
                }
                break;
            case 3:
                // 連長圧縮して3ならスリーカード、2ならツーペア
                for (int i = 0; i < getSortNumCardList().size();) {
                    int numOfContinue = 0;
                    int j = i;
                    while (j < getSortNumCardList().size()
                            && getSortNumCardList().get(i).getNumber() == getSortNumCardList().get(j).getNumber()) {
                        j++;
                    }
                    numOfContinue = j - i;
                    if (numOfContinue == 3) {
                        hand = Math.min(hand, THREE_OF_A_KIND);
                    }
                    if (numOfContinue == 2) {
                        hand = Math.min(hand, TWO_PAIR);
                    }
                    i = j;
                }
                break;
            case 4:
                // ワンペア
                hand = Math.min(hand, A_PAIR);
                break;

            default:
                break;
        }

        return hand;
    }

    /* <summary> ロイヤルストレートフラッシュ（一種類のスーツで最も数位の高い5枚が揃った役）か判定 </summary> */
    private boolean isRoyal_Flush(Card[] cards) {
        List<Card> sortNumCardList = getSortNumCardList();
        if (sortNumCardList.get(0).getNumber() == 10 && sortNumCardList.get(4).getNumber() == 1) {
            return true;
        }
        return false;
    }

    /* <summary> フラッシュ（一種類のスーツだけで構成された役）か判定 </summary> */
    private boolean isFlush(Card[] cards) {
        int tempMark = cards[0].getSuit_Integer();
        for (Card card : cards) {
            if (card.getSuit_Integer() != tempMark) {
                return false;
            }
        }
        return true;
    }

    /* <summary> ストレート（5枚の数位が連続して揃った役）か判定 </summary> */
    private boolean isStraight(Card[] cards) {
        // List<Card> sortNumCardList = sortCardNum(cards);
        List<Card> sortNumCardList = getSortNumCardList();
        for (int i = 0; i < sortNumCardList.size() - 1; i++) {
            if (sortNumCardList.get(i).getNumber() + 1 != sortNumCardList.get(i + 1).getNumber()) {
                return false;
            }
        }
        return true;
    }

    private List<Card> sortCardNum(Card[] cards) {
        List<Card> cardList = Arrays.asList(cards);
        Collections.sort(cardList, (card1, card2) -> {
            // Aが一番強い
            if (card1.getNumber() == 1 || card2.getNumber() == 1) {
                return card2.getNumber() - card1.getNumber();
            }
            return card1.getNumber() - card2.getNumber();
        });
        return cardList;
    }

    // public static void main(String[] args) {
    // Dealer dealer = new Dealer();
    // DealerLogic dealerLogic = new DealerLogic();
    // JudgementHand judgementHand = new JudgementHand();
    // dealerLogic.gameStart(dealer);
    // Card[][] hands = dealer.getHands();
    // dealerLogic.printHands(dealer);
    // System.out.println(getHand_Str(judgementHand.judgementHand(hands[0])));
    // System.out.println(getHand_Str(judgementHand.judgementHand(hands[1])));
    // // // フラッシュ
    // // Card[] cards1 = { new Card(0, 1), new Card(0, 6), new Card(0, 2), new
    // Card(0,
    // // 3), new Card(0, 4) };
    // // System.out.println(getHand_Str(judgementHand.judgementHand(cards1)));
    // // // ロイヤルストレートフラッシュ
    // // Card[] cards2 = { new Card(0, 1), new Card(0, 10), new Card(0, 12), new
    // // Card(0, 11), new Card(0, 13) };
    // // System.out.println(getHand_Str(judgementHand.judgementHand(cards2)));
    // // // ストレートフラッシュ
    // // Card[] cards3 = { new Card(0, 2), new Card(0, 4), new Card(0, 3), new
    // Card(0,
    // // 6), new Card(0, 5) };
    // // System.out.println(getHand_Str(judgementHand.judgementHand(cards3)));
    // // // ストレート
    // // Card[] cards4 = { new Card(3, 2), new Card(0, 4), new Card(1, 3), new
    // Card(0,
    // // 6), new Card(0, 5) };
    // // System.out.println(getHand_Str(judgementHand.judgementHand(cards4)));
    // // // フォーペア
    // // Card[] cards5 = { new Card(3, 1), new Card(0, 1), new Card(1, 1), new
    // Card(0,
    // // 6), new Card(2, 1) };
    // // System.out.println(getHand_Str(judgementHand.judgementHand(cards5)));
    // // // ワンペア
    // // Card[] cards6 = { new Card(3, 1), new Card(0, 1), new Card(1, 5), new
    // Card(0,
    // // 10), new Card(2, 12) };
    // // System.out.println(getHand_Str(judgementHand.judgementHand(cards6)));
    // // // フルハウス
    // // Card[] cards7 = { new Card(3, 1), new Card(0, 1), new Card(1, 5), new
    // Card(0,
    // // 5), new Card(2, 5) };
    // // System.out.println(getHand_Str(judgementHand.judgementHand(cards7)));
    // // // スリーカード
    // // Card[] cards8 = { new Card(3, 1), new Card(0, 11), new Card(1, 1), new
    // // Card(0, 3), new Card(2, 1) };
    // // System.out.println(getHand_Str(judgementHand.judgementHand(cards8)));
    // // // ツーペア
    // // Card[] cards9 = { new Card(3, 1), new Card(0, 1), new Card(1, 5), new
    // Card(0,
    // // 5), new Card(2, 8) };
    // // System.out.println(getHand_Str(judgementHand.judgementHand(cards9)));
    // }

}
