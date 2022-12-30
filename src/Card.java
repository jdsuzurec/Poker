
/*
 * <summary>
 * カードに関する情報を保持
 * </summary>
 */
import java.io.Serializable;

public class Card implements Serializable {
    private final int A = 1, J = 11, Q = 12, K = 13;
    private final int SPADE = 0, HEART = 1, DIAMOND = 2, CLOVER = 3;
    private String mark_str;
    private int mark_num;
    private int number;
    private boolean isHave;

    public Card(int mark_num, int number) {
        setMark(mark_num);
        this.number = number;
        this.isHave = false;// 最初は手札ではない
    }

    private String getMark() {
        return mark_str;
    }

    public int getMark_Integer() {
        return mark_num;
    }

    private void setMark(int mark_num) {
        switch (mark_num) {
            case SPADE:
                mark_str = "♠︎";
                mark_num = SPADE;
                break;
            case HEART:
                mark_str = "♥";
                mark_num = HEART;
                break;
            case DIAMOND:
                mark_str = "♦︎";
                mark_num = DIAMOND;

                break;
            case CLOVER:
                this.mark_str = "♣︎";
                mark_num = CLOVER;
                break;
            default:
                break;
        }
    }

    public int getNumber() {
        return this.number;
    }

    private String getNumberString() {
        switch (this.number) {
            case A:
                return "A";
            case J:
                return "J";
            case Q:
                return "Q";
            case K:
                return "K";
            default:
                return String.valueOf(this.number);
        }
    }

    public boolean getIsHave() {
        return this.isHave;
    }

    public void setIsHave(boolean isHave) {
        this.isHave = isHave;
    }

    public String toString() {
        return getMark() + " : " + getNumberString();
    }
}

// カード型のものを互いに受け渡す
// GUIに反映するときString型にする