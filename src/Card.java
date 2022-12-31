
/*
 * <summary>
 * カード情報（Model）
 * </summary>
 */
import java.io.Serializable;

public class Card implements Serializable {
    // #region field
    /* <summary> スーツ </summary> */
    private String suit_str;
    private int suit_num;
    /* <summary> マジックナンバー（スーツ） </summary> */
    private final int SPADE = 0, HEART = 1, DIAMOND = 2, CLOVER = 3;
    /* <summary> 数位 </summary> */
    private int number;
    /* <summary> マジックナンバー（ロイヤル） </summary> */
    private final int J = 11, Q = 12, K = 13, A = 1;
    /* <summary> 所持状況 </summary> */
    private boolean isHave;
    // #endregion field

    // #region constructor
    public Card(int suit_num, int number) {
        setSuit(suit_num);
        this.number = number;
        this.isHave = false;// 最初は全て手札ではない
    }
    // #endregion constructor

    // #region getter setter
    private String getSuit() {
        return suit_str;
    }

    public int getSuit_Integer() {
        return suit_num;
    }

    private void setSuit(int suit_num) {
        switch (suit_num) {
            case SPADE:
                suit_str = "♠︎";
                this.suit_num = SPADE;
                break;
            case HEART:
                suit_str = "♥";
                this.suit_num = HEART;
                break;
            case DIAMOND:
                suit_str = "♦︎";
                this.suit_num = DIAMOND;
                break;
            case CLOVER:
                this.suit_str = "♣︎";
                this.suit_num = CLOVER;
                break;
            default:
                break;
        }
    }

    public int getNumber() {
        return number;
    }

    private String getNumber_String() {
        switch (number) {
            case A:
                return "A";
            case J:
                return "J";
            case Q:
                return "Q";
            case K:
                return "K";
            default:
                return String.valueOf(number);
        }
    }

    public boolean getIsHave() {
        return isHave;
    }

    public void setIsHave(boolean isHave) {
        this.isHave = isHave;
    }
    // #endregion getter setter

    // #region public function
    public String toString() {
        return getSuit() + " : " + getNumber_String();
    }
    // #endregion public function
}