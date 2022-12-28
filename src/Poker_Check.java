import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Poker_Check {

    private static String[][] trunp = new String[4][13];// トランプ格納する二次元配列 [種][位]
    private static final int SPADE = 0;
    private static final int HEART = 1;
    private static final int DIAMOND = 2;
    private static final int CLOVER = 3;

    static ArrayList<String> spadeCard = new ArrayList<>();
    static ArrayList<String> heartCard = new ArrayList<>();
    static ArrayList<String> diamondCard = new ArrayList<>();
    static ArrayList<String> cloverCard = new ArrayList<>();
    static String[] sortCard = new String[5];

    public static void main(String[] args) {
        // トランプ生成、格納
        for (int i = 0; i < 4; i++) {// 役の数繰り返す
            for (int j = 0; j <= 8; j++) {// 数字カードは2~10まで
                if (i == SPADE) {
                    int number = j + 2;
                    trunp[SPADE][j] = "♠ : " + number;
                    trunp[SPADE][9] = "♠ : J";
                    trunp[SPADE][10] = "♠ : Q";
                    trunp[SPADE][11] = "♠ : K";
                    trunp[SPADE][12] = "♠ : A";
                } else if (i == HEART) {
                    int number = j + 2;
                    trunp[HEART][j] = "♥ : " + number;
                    trunp[HEART][9] = "♥ : J";
                    trunp[HEART][10] = "♥ : Q";
                    trunp[HEART][11] = "♥ : K";
                    trunp[HEART][12] = "♥ : A";
                } else if (i == DIAMOND) {
                    int number = j + 2;
                    trunp[DIAMOND][j] = "♦ : " + number;
                    trunp[DIAMOND][9] = "♦ : J";
                    trunp[DIAMOND][10] = "♦ : Q";
                    trunp[DIAMOND][11] = "♦ : K";
                    trunp[DIAMOND][12] = "♦ : A";
                } else if (i == CLOVER) {
                    int number = j + 2;
                    trunp[CLOVER][j] = "♣ : " + number;
                    trunp[CLOVER][9] = "♣ : J";
                    trunp[CLOVER][10] = "♣ : Q";
                    trunp[CLOVER][11] = "♣ : K";
                    trunp[CLOVER][12] = "♣ : A";
                }
            }
        }

        // テスト
        for (int t = 0; t < 10; t++) {

            String[] testCard = new String[5];
            boolean[][] isHand = new boolean[4][13];
            Random random = new Random();

            for (int i = 0; i < 5; i++) {
                while (true) {
                    int mark = random.nextInt(4);
                    int number = random.nextInt(13);
                    if (!isHand[mark][number]) {
                        isHand[mark][number] = true;
                        testCard[i] = trunp[mark][number];
                        break;
                    }
                }
            }

            for (String test : testCard) {
                System.out.print(test + ",");
            }
            System.out.println();

            while (true) {
                if (isRoyalStraightFlash(testCard)) {
                    System.out.println("ロイヤルストレートフラッシュ");
                    break;
                } else if (isStraightFlash(testCard)) {
                    System.out.println("ストレートフラッシュ");
                    break;
                } else if (isFoursCard(testCard)) {
                    System.out.println("フォアカード");
                    break;
                } else if (isFullHouse(testCard)) {
                    System.out.println("フルハウス");
                    break;
                } else if (isFlash(testCard)) {
                    System.out.println("フラッシュ");
                    break;
                } else if (isStraight(testCard)) {
                    System.out.println("ストレート");
                    break;
                } else if (isThreeCard(testCard)) {
                    System.out.println("スリーカード");
                    break;
                } else if (isTwoPair(testCard)) {
                    System.out.println("ツウ・ペア");
                    break;
                } else if (isOnePair(testCard)) {
                    System.out.println("ワン・ペア");
                    break;
                } else {
                    System.out.println("ノーペア！");
                    break;
                }
            }
            System.out.println();
        }
    }

    // ①ロイヤルストレートフラッシュかどうか判断する
    public static boolean isRoyalStraightFlash(String[] cardList) {
        if (isFlash(cardList)) {// 全て同じマーク
            sortNumber(cardList);// 順番を強さ順にソート

            if (sortCard.length == 5 && sortCard[0].equals(trunp[SPADE][8])) {
                return true;
            } else if (heartCard.size() != 0 && sortCard[0].equals(trunp[HEART][8])) {
                return true;
            } else if (diamondCard.size() != 0 && sortCard[0].equals(trunp[DIAMOND][8])) {
                return true;
            } else if (cloverCard.size() != 0 && sortCard[0].equals(trunp[CLOVER][8])) {
                return true;
            }
        }
        return false;
    }

    // ②ストレートフラッシュかどうか判断する
    public static boolean isStraightFlash(String[] cardList) {
        if (isFlash(cardList) && !isRoyalStraightFlash(cardList)) {// 全て同種 かつ ロイヤルストレートフラッシュではない
            sortNumber(cardList);// 順番を強さ順にソートしてsortCardに格納する

            // 数字が順番ならストレートフラッシュ
            int m = 0;
            int n = 0;
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 13; k++) {
                    if (sortCard[0].equals(trunp[j][k])) {
                        m = j;
                        n = k;
                    }
                }
            }

            if (sortCard[1].equals(trunp[m][n + 1]) && sortCard[2].equals(trunp[m][n + 2]) &&
                    sortCard[3].equals(trunp[m][n + 3]) && sortCard[4].equals(trunp[m][n + 4])) {
                return true;
            }
        }
        return false;
    }

    // ③フォアカードかどうか判断する
    public static boolean isFoursCard(String[] cardList) {
        if (sameNumberCount(cardList) == 3) {
            return true;
        } else {
            return false;
        }
    }

    // ④フルハウスかどうか判断する
    public static boolean isFullHouse(String[] cardList) {
        String[] num = new String[5];
        for (int i = 0; i < 5; i++) {// カードリストの位部分だけ取り出してnumに格納
            String[] splitStr = cardList[i].split(" : ");
            num[i] = splitStr[1];
        }

        // numを精査して同じ位だったらカウントする
        int[] count = { 0, 0, 0, 0, 0 };
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (num[i].equals(num[j])) {
                    count[i]++;
                }
            }
        }

        // count == 2 が2こ かつ count == 3が3こ ならフルハウス
        int pairCount2 = 0;
        int pairCount3 = 0;
        for (int i = 0; i < 5; i++) {
            if (count[i] == 2) {
                pairCount2++;
            } else if (count[i] == 3) {
                pairCount3++;
            }
        }
        if (pairCount2 == 2 && pairCount3 == 3) {
            return true;

        }

        return false;
    }

    // ⑤フラッシュかどうか判断する
    public static boolean isFlash(String[] cardList) {
        separateMark(cardList);
        if (spadeCard.size() == 5 || heartCard.size() == 5 || diamondCard.size() == 5 || cloverCard.size() == 5) {
            return true;
        } else {
            return false;
        }

    }

    // ⑥ストレートかどうか判断する
    public static boolean isStraight(String[] cardList) {
        // 数字部分だけをソートして順番になってたらストレート
        // ポーカーでの位の強さを入れるリスト
        int[] numInt = new int[5];

        for (int i = 0; i < 5; i++) {
            for (int k = 0; k < 4; k++) {
                for (int j = 0; j < 13; j++) {
                    if (cardList[i].equals(trunp[k][j])) {
                        numInt[i] = j;
                    }
                }
            }
        }

        // for(int number: numInt){
        // System.out.print(number + ",");
        // }
        // System.out.println();

        Arrays.sort(numInt);

        // for(int number: numInt){
        // System.out.print(number + ",");
        // }
        // System.out.println();

        if (numInt[1] == numInt[0] + 1 && numInt[2] == numInt[1] + 1 && numInt[3] == numInt[2] + 1
                && numInt[4] == numInt[3] + 1) {
            return true;
        }
        return false;
    }

    // ⑦スリーカードかどうか判断する
    public static boolean isThreeCard(String[] cardList) {
        if (sameNumberCount(cardList) == 2) {
            return true;
        } else {
            return false;
        }
    }

    // ⑧ツウ・ペアかどうか判断する
    public static boolean isTwoPair(String[] cardList) {
        String[] num = new String[5];
        for (int i = 0; i < 5; i++) {// カードリストの位部分だけ取り出してnumに格納
            String[] splitStr = cardList[i].split(" : ");
            num[i] = splitStr[1];
        }

        // numを精査して同じ位だったらカウントする
        int[] count = { 0, 0, 0, 0, 0 };
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (num[i].equals(num[j])) {
                    count[i]++;
                }
            }
        }

        // count == 2が2こあったらツウペア
        int pairCount = 0;
        for (int i = 0; i < 5; i++) {
            if (count[i] == 2) {
                pairCount++;
            }
        }
        if (pairCount == 4) {
            return true;
        }

        return false;
    }

    // ⑨ワン・ペアかどうか判断する
    public static boolean isOnePair(String[] cardList) {
        if (sameNumberCount(cardList) == 1) {
            return true;
        } else {
            return false;
        }
    }

    // ペアの数を数えて返すメソッド
    public static int sameNumberCount(String[] cardList) {
        String[] num = new String[5];
        for (int i = 0; i < 5; i++) {// カードリストの位部分だけ取り出してnumに格納
            String[] splitStr = cardList[i].split(" : ");
            num[i] = splitStr[1];
        }

        // numを精査して同じ位だったらカウントする
        int[] count = { 0, 0, 0, 0, 0 };
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (num[i].equals(num[j])) {
                    count[i]++;
                }
            }
        }

        int maxCount = 0;// 最大のペアの数をそのカードリストのペアの数+1とする
        for (int i = 0; i < 5; i++) {
            if (maxCount <= count[i]) {
                maxCount = count[i];
            }
        }
        return maxCount - 1;// カードリストのペア数を返す
    }

    // 種類別にするメソッド
    public static void separateMark(String[] cardList) {
        spadeCard.clear();
        heartCard.clear();
        diamondCard.clear();
        cloverCard.clear();
        // cardListを種類別にする
        for (int i = 0; i < cardList.length; i++) {
            for (int j = 0; j < 13; j++) {
                if (cardList[i].equals(trunp[SPADE][j])) {
                    spadeCard.add(cardList[i]);
                } else if (cardList[i].equals(trunp[HEART][j])) {
                    heartCard.add(cardList[i]);
                } else if (cardList[i].equals(trunp[DIAMOND][j])) {
                    diamondCard.add(cardList[i]);
                } else if (cardList[i].equals(trunp[CLOVER][j])) {
                    cloverCard.add(cardList[i]);
                }
            }
        }
    }

    // 種類別にして順番をソートしてsortCardListに格納するメソッド
    public static void sortNumber(String[] cardList) {
        separateMark(cardList);// 種類別にする
        // 種類別のリストをソートする
        // ♠︎
        if (spadeCard.size() != 0 && spadeCard.size() != 1) {// リストに2枚以上あるなら
            Collections.sort(spadeCard);// まずは辞書順にソート
            // 10とJとQとKとAを後ろに
            for (int i = 0; i < spadeCard.size(); i++) {
                if (spadeCard.get(i).equals(trunp[SPADE][8])) {// 10なら
                    // 一番後ろにいく
                    String temp = spadeCard.get(i);
                    spadeCard.remove(i);
                    spadeCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < spadeCard.size(); i++) {
                if (spadeCard.get(i).equals(trunp[SPADE][9])) {// Jなら
                    // 一番後ろにいく
                    String temp = spadeCard.get(i);
                    spadeCard.remove(i);
                    spadeCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < spadeCard.size(); i++) {
                if (spadeCard.get(i).equals(trunp[SPADE][10])) {// Qなら
                    // 一番後ろにいく
                    String temp = spadeCard.get(i);
                    spadeCard.remove(i);
                    spadeCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < spadeCard.size(); i++) {
                if (spadeCard.get(i).equals(trunp[SPADE][11])) {// Kなら
                    // 一番後ろにいく
                    String temp = spadeCard.get(i);
                    spadeCard.remove(i);
                    spadeCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < spadeCard.size(); i++) {
                if (spadeCard.get(i).equals(trunp[SPADE][12])) {// Aなら
                    // 一番後ろにいく
                    String temp = spadeCard.get(i);
                    spadeCard.remove(i);
                    spadeCard.add(temp);
                    break;
                }
            }
        }
        // ♥
        if (heartCard.size() != 0 && heartCard.size() != 1) {// リストに2枚以上あるなら
            Collections.sort(heartCard);// まずは辞書順にソート
            for (int i = 0; i < heartCard.size(); i++) {
                if (heartCard.get(i).equals(trunp[HEART][8])) {// 10なら
                    // 一番後ろにいく
                    String temp = heartCard.get(i);
                    heartCard.remove(i);
                    heartCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < heartCard.size(); i++) {
                if (heartCard.get(i).equals(trunp[HEART][9])) {// Jなら
                    // 一番後ろにいく
                    String temp = heartCard.get(i);
                    heartCard.remove(i);
                    heartCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < heartCard.size(); i++) {
                if (heartCard.get(i).equals(trunp[HEART][10])) {// Qなら
                    // 一番後ろにいく
                    String temp = heartCard.get(i);
                    heartCard.remove(i);
                    heartCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < heartCard.size(); i++) {
                if (heartCard.get(i).equals(trunp[HEART][11])) {// Kなら
                    // 一番後ろにいく
                    String temp = heartCard.get(i);
                    heartCard.remove(i);
                    heartCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < heartCard.size(); i++) {
                if (heartCard.get(i).equals(trunp[HEART][12])) {// Aなら
                    // 一番後ろにいく
                    String temp = heartCard.get(i);
                    heartCard.remove(i);
                    heartCard.add(temp);
                    break;
                }
            }
        }
        // ♦︎
        if (diamondCard.size() != 0 && diamondCard.size() != 1) {// リストに2枚以上あるなら
            Collections.sort(diamondCard);// まずは辞書順にソート
            for (int i = 0; i < diamondCard.size(); i++) {
                if (diamondCard.get(i).equals(trunp[DIAMOND][8])) {// 10なら
                    // 一番後ろにいく
                    String temp = diamondCard.get(i);
                    diamondCard.remove(i);
                    diamondCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < diamondCard.size(); i++) {
                if (diamondCard.get(i).equals(trunp[DIAMOND][9])) {// Jなら
                    // 一番後ろにいく
                    String temp = diamondCard.get(i);
                    diamondCard.remove(i);
                    diamondCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < diamondCard.size(); i++) {
                if (diamondCard.get(i).equals(trunp[DIAMOND][10])) {// Qなら
                    // 一番後ろにいく
                    String temp = diamondCard.get(i);
                    diamondCard.remove(i);
                    diamondCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < diamondCard.size(); i++) {
                if (diamondCard.get(i).equals(trunp[DIAMOND][11])) {// Kなら
                    // 一番後ろにいく
                    String temp = diamondCard.get(i);
                    diamondCard.remove(i);
                    diamondCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < diamondCard.size(); i++) {
                if (diamondCard.get(i).equals(trunp[DIAMOND][12])) {// Aなら
                    // 一番後ろにいく
                    String temp = diamondCard.get(i);
                    diamondCard.remove(i);
                    diamondCard.add(temp);
                    break;
                }
            }
        }
        // ♣︎
        if (cloverCard.size() != 0 && cloverCard.size() != 1) {// リストに2枚以上あるなら
            Collections.sort(cloverCard);// まずは辞書順にソート
            for (int i = 0; i < cloverCard.size(); i++) {
                if (cloverCard.get(i).equals(trunp[CLOVER][8])) {// 10なら
                    // 一番後ろにいく
                    String temp = cloverCard.get(i);
                    cloverCard.remove(i);
                    cloverCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < cloverCard.size(); i++) {
                if (cloverCard.get(i).equals(trunp[CLOVER][9])) {// Jなら
                    // 一番後ろにいく
                    String temp = cloverCard.get(i);
                    cloverCard.remove(i);
                    cloverCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < cloverCard.size(); i++) {
                if (cloverCard.get(i).equals(trunp[CLOVER][10])) {// Qなら
                    // 一番後ろにいく
                    String temp = cloverCard.get(i);
                    cloverCard.remove(i);
                    cloverCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < cloverCard.size(); i++) {
                if (cloverCard.get(i).equals(trunp[CLOVER][11])) {// Kなら
                    // 一番後ろにいく
                    String temp = cloverCard.get(i);
                    cloverCard.remove(i);
                    cloverCard.add(temp);
                    break;
                }
            }
            for (int i = 0; i < cloverCard.size(); i++) {
                if (cloverCard.get(i).equals(trunp[CLOVER][12])) {// Aなら
                    // 一番後ろにいく
                    String temp = cloverCard.get(i);
                    cloverCard.remove(i);
                    cloverCard.add(temp);
                    break;
                }
            }
        }

        // sortCardListに格納していく
        int i = 0;
        if (spadeCard.size() != 0) {
            for (int j = i; j < spadeCard.size(); j++) {
                sortCard[i] = spadeCard.get(j);
                i++;
            }
        }
        if (heartCard.size() != 0) {
            for (int j = i; j < heartCard.size(); j++) {
                sortCard[i] = heartCard.get(j);
                i++;
            }
        }
        if (diamondCard.size() != 0) {
            for (int j = i; j < diamondCard.size(); j++) {
                sortCard[i] = diamondCard.get(j);
                i++;
            }
        }
        if (cloverCard.size() != 0) {
            for (int j = i; j < cloverCard.size(); j++) {
                sortCard[i] = cloverCard.get(j);
                i++;
            }
        }
        //
        // for(String card : sortCard){
        // System.out.print(card);
        // }
        // System.out.println();
    }

}

/* テスト用 */
// String[] card1 = {"♠ : A", "♠ : J", "♠ : 10", "♠ : Q", "♠ :
// K"};//ロイヤルストレートフラッシュ
// System.out.println("<ロイヤルストレートフラッシュ>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card1));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card1));
// System.out.println("③フォアカード："+ isFoursCard(card1));
// System.out.println("④フルハウス：" + isFullHouse(card1));
// System.out.println("⑤フラッシュ："+isFlash(card1));
// System.out.println("⑥ストレート："+isStraight(card1));
// System.out.println("⑦スリーカード：" + isThreeCard(card1));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card1));
// System.out.println("⑨ワン・ペア：" + isOnePair(card1));
// System.out.println();
//
// String[] card2 = {"♠ : 9", "♠ : 10", "♠ : J", "♠ : K", "♠ : Q"};//ストレートフラッシュ
// System.out.println("<ストレートフラッシュ>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card2));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card2));
// System.out.println("③フォアカード："+ isFoursCard(card2));
// System.out.println("④フルハウス：" + isFullHouse(card2));
// System.out.println("⑤フラッシュ："+isFlash(card2));
// System.out.println("⑥ストレート："+isStraight(card2));
// System.out.println("⑦スリーカード：" + isThreeCard(card2));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card2));
// System.out.println("⑨ワン・ペア：" + isOnePair(card2));
// System.out.println();
//
//
// String[] card5 = {"♠ : 5","♠ : J","♥ : 5","♣ : 5","♦ : 5"};//フォアカード
// System.out.println("<フォアカード>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card5));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card5));
// System.out.println("③フォアカード："+ isFoursCard(card5));
// System.out.println("④フルハウス：" + isFullHouse(card5));
// System.out.println("⑤フラッシュ："+isFlash(card5));
// System.out.println("⑥ストレート："+isStraight(card5));
// System.out.println("⑦スリーカード：" + isThreeCard(card5));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card5));
// System.out.println("⑨ワン・ペア：" + isOnePair(card5));
// System.out.println();
//
//
// String[] card9 = {"♠ : 5","♦ : 5","♠ : 7","♣ : 5","♦ : 7"};//フルハウス
// System.out.println("<フルハウス>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card9));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card9));
// System.out.println("③フォアカード："+ isFoursCard(card9));
// System.out.println("④フルハウス：" + isFullHouse(card9));
// System.out.println("⑤フラッシュ："+isFlash(card9));
// System.out.println("⑥ストレート："+isStraight(card9));
// System.out.println("⑦スリーカード：" + isThreeCard(card9));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card9));
// System.out.println("⑨ワン・ペア：" + isOnePair(card9));
// System.out.println();
//
// String[] card11 = {"♥ : 10","♥ : 3","♥ : J","♥ : 6","♥ : 7"};//フラッシュ
// System.out.println("<フラッシュ>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card11));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card11));
// System.out.println("③フォアカード："+ isFoursCard(card11));
// System.out.println("④フルハウス：" + isFullHouse(card11));
// System.out.println("⑤フラッシュ："+isFlash(card11));
// System.out.println("⑥ストレート："+isStraight(card11));
// System.out.println("⑦スリーカード：" + isThreeCard(card11));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card11));
// System.out.println();
//
//
// String[] card10 = {"♠ : Q","♦ : J","♠ : 10","♣ : 9","♦ : 8"};//ストレート
// System.out.println("<ストレート>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card10));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card10));
// System.out.println("③フォアカード："+ isFoursCard(card10));
// System.out.println("④フルハウス：" + isFullHouse(card10));
// System.out.println("⑤フラッシュ："+isFlash(card10));
// System.out.println("⑥ストレート："+isStraight(card10));
// System.out.println("⑦スリーカード：" + isThreeCard(card10));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card10));
// System.out.println("⑨ワン・ペア：" + isOnePair(card10));
// System.out.println();
//
//
// String[] card6 = {"♠ : Q","♠ : J","♠ : 3","♣ : Q","♦ : Q"};//スリーカード
// System.out.println("<スリーカード>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card6));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card6));
// System.out.println("③フォアカード："+ isFoursCard(card6));
// System.out.println("④フルハウス：" + isFullHouse(card6));
// System.out.println("⑤フラッシュ："+isFlash(card6));
// System.out.println("⑥ストレート："+isStraight(card6));
// System.out.println("⑦スリーカード：" + isThreeCard(card6));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card6));
// System.out.println("⑨ワン・ペア：" + isOnePair(card6));
// System.out.println();
//
//
// String[] card8 = {"♠ : 5","♠ : J","♠ : Q","♣ : 5","♦ : Q"};//ツーペア
// System.out.println("<ツーペア>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card8));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card8));
// System.out.println("③フォアカード："+ isFoursCard(card8));
// System.out.println("④フルハウス：" + isFullHouse(card8));
// System.out.println("⑤フラッシュ："+isFlash(card8));
// System.out.println("⑥ストレート："+isStraight(card8));
// System.out.println("⑦スリーカード：" + isThreeCard(card8));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card8));
// System.out.println("⑨ワン・ペア：" + isOnePair(card8));
// System.out.println();
//
//
// String[] card7 = {"♠ : 5","♠ : J","♠ : 3","♣ : 5","♦ : 7"};//ワンペア
// System.out.println("<ワンペア>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card7));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card7));
// System.out.println("③フォアカード："+ isFoursCard(card7));
// System.out.println("④フルハウス：" + isFullHouse(card7));
// System.out.println("⑤フラッシュ："+isFlash(card7));
// System.out.println("⑥ストレート："+isStraight(card7));
// System.out.println("⑦スリーカード：" + isThreeCard(card7));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card7));
// System.out.println("⑨ワン・ペア：" + isOnePair(card7));
// System.out.println();
//
// String[] card3 = {"♥ : 2","♥ : 3","♥ : 4","♣ : 6","♣ : 7"};//ノーペア
// System.out.println("<ノーペア>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card3));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card3));
// System.out.println("③フォアカード："+ isFoursCard(card3));
// System.out.println("④フルハウス：" + isFullHouse(card3));
// System.out.println("⑤フラッシュ："+isFlash(card3));
// System.out.println("⑥ストレート："+isStraight(card3));
// System.out.println("⑦スリーカード：" + isThreeCard(card3));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card3));
// System.out.println("⑨ワン・ペア：" + isOnePair(card3));
// System.out.println();
//
//
// String[] card4 = {"♣ : J","♠ : Q","♠ : A","♠ : 5","♥ : 6"};//ノーペア
// System.out.println("<ノーペア>");//
// System.out.println("①ロイヤルストレートフラッシュ："+isRoyalStraightFlash(card4));
// System.out.println("②ストレートフラッシュ："+isStraightFlash(card4));
// System.out.println("③フォアカード："+ isFoursCard(card4));
// System.out.println("④フルハウス：" + isFullHouse(card4));
// System.out.println("⑤フラッシュ："+isFlash(card4));
// System.out.println("⑥ストレート："+isStraight(card4));
// System.out.println("⑦スリーカード：" + isThreeCard(card4));
// System.out.println("⑧ツウ・ペア：" + isTwoPair(card4));
// System.out.println("⑨ワン・ペア：" + isOnePair(card4));
// System.out.println();