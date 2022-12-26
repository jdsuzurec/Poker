/*
 * <summary>
 * カードに関する情報を保持
 * </summary>
 */
public class Card {
    private final int A = 1, J = 11, Q = 12, K = 13;
    private final int SPADE = 0, HEART = 1, DIAMOND = 2, CLOVER = 3;
    private String mark;
    private int number;
    private boolean isHave;

    public Card(int mark, int number) {
        setMark(mark);
        this.number = number;
        this.isHave = false;// 最初は手札ではない
    }

    public String getMark() {
        return this.mark;
    }

    public void setMark(int mark) {
        switch (mark) {
            case SPADE:
                this.mark = "♠︎";
                break;
            case HEART:
                this.mark = "♥";
                break;
            case DIAMOND:
                this.mark = "♦︎";
                break;
            case CLOVER:
                this.mark = "♣︎";
                break;
            default:
                break;
        }
    }

    public int getNumber() {
        return this.number;
    }

    public String getNumberString() {
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
}

// カード型のものを互いに受け渡す
// GUIに反映するときString型にする