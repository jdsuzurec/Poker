
/**
 * カード情報（Model）
 */
import java.io.Serializable;

public class Card implements Serializable {
    // start field
    /*  スーツ  */
    private String suit_str;
    private int suit_num;
    /*  マジックナンバー（スーツ）  */
    private final int SPADE = 0, HEART = 1, DIAMOND = 2, CLOVER = 3;
    /*  数位  */
    private int number;
    /*  マジックナンバー（ロイヤル）  */
    private final int J = 11, Q = 12, K = 13, A = 1;
    /*  所持状況  */
    private boolean isHave;
    // end field

    // start constructor
    public Card(int suit_num, int number) {
        setSuit(suit_num);
        this.number = number;
        this.isHave = false;// 最初は全て手札ではない
    }
    // end constructor

    // start getter setter
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
    // end getter setter

    // start public function
    public String toString() {
        return getSuit() + " : " + getNumber_String();
    }
    // end public function
}