import java.util.Random;

public class DealerLogic {
    private Random random = new Random();

    /* <summary> ゲームスタート </summary> */
    public void gameStart(Dealer dealer) {
        createDeck(dealer);
        dealFirstHands(dealer);
        dealer.incrementCount_Of_Turn();
        // printHands(dealer);
        // printDeck(dealer);
    }

    /* <summary> 山札を作成 </summary> */
    private void createDeck(Dealer dealer) {
        Card[][] deck = dealer.getDeck();
        for (int mark = 0; mark < dealer.getNUM_OF_SUIT(); mark++) {
            for (int number = 1; number <= dealer.getNUM_OF_NUM(); number++) {
                deck[mark][number - 1] = new Card(mark, number);
            }
        }
        System.out.println("山札を生成しました");
        dealer.setDeck(deck);
    }

    /* <summary> 全プレイヤーの最初の手札を決める </summary> */
    private void dealFirstHands(Dealer dealer) {
        Card[][] hands = dealer.getHands();
        for (int num_of_player = 0; num_of_player < hands.length; num_of_player++) {
            for (int num_of_card = 0; num_of_card < hands[num_of_player].length; num_of_card++) {
                hands[num_of_player][num_of_card] = dealOneCard(dealer);
            }
        }
        System.out.println("手札を生成しました");
        dealer.setHands(hands);
    }

    /* <summary> ランダムに山札から1枚カードを返す </summary> */
    private Card dealOneCard(Dealer dealer) {
        // ランダムに山札からカードを取得（山札のカードが出てくるまでランダム値を振り直す）
        while (true) {
            int mark = random.nextInt(dealer.getNUM_OF_SUIT());
            int number = random.nextInt(dealer.getNUM_OF_NUM());
            Card card = dealer.getDeck()[mark][number];
            if (!card.getIsHave()) {
                // カードの所持状態を真にしてから返す
                card.setIsHave(true);
                return card;
            }
        }
    }

    /*
     * <summary> 手札のカードと山札のカードを交換する </summary>
     * <param name="numOfExchangedCard"> 交換するカードが何枚目か</param>
     */
    public Card exchangeCard(Dealer dealer, int cardNum, Card card) {
        // 指定のカードを山札に戻す
        leaveOneCard(dealer, cardNum);
        // 山札から新たなカードを引く
        Card newCard;
        if (card == null) {
            newCard = dealOneCard(dealer);
        } else {
            newCard = card;
            newCard.setIsHave(true);
        }
        // 新たな手札にする
        dealer.setHand(dealer.getNum_Of_TurnUser(), cardNum, newCard);
        System.out.println("新たなカード:" + newCard.toString());
        printHands(dealer);
        // printDeck(dealer);
        return newCard;
    }

    /*
     * <summary> 手札のカードを山札に戻す </summary>
     * <param name="numOfExchangedCard">交換するカードが何枚目か</param>
     */
    private void leaveOneCard(Dealer dealer, int cardNum) {
        Card leaveCard = dealer.getHands()[dealer.getNum_Of_TurnUser()][cardNum];
        System.out.println("返すカード:" + leaveCard.toString());
        // 返すカードの所持状態を偽にして
        leaveCard.setIsHave(false);
        // 山札に反映
        dealer.setDeckCard(leaveCard.getSuit_Integer(), cardNum, leaveCard);
        dealer.setHand(dealer.getNum_Of_TurnUser(), cardNum, null);
        // printHands(dealer);
        // printDeck(dealer);
    }

    /* <summary> プレイヤーの行動終了後、ゲームが次にどう進行するかを返す </summary> */
    public String opperationEnd(Dealer dealer) {
        // 総行動回数をインクリメント
        dealer.incrementCount_Of_Operations();
        // プレイヤー数で割り切れる総行動回数なら、次のターンに進む
        if (dealer.getCount_Of_Operations() % dealer.getNUM_OF_PLAYER() == 0) {
            // ただし行動回数上限に達した場合はゲーム終了
            if (dealer.getNUM_OF_OPERATIONS() == dealer.getCount_Of_Operations()) {
                return "GAMEEND";
            }
            // ターン数をインクリメント
            dealer.incrementCount_Of_Turn();
            return "NEXTTURN";
        }
        // ターン数は変わらずに操作するプレイヤーが変わる
        return "CHANGEPLAYER";
    }

    /* <summary> ゲーム終了処理 </summary> */
    public void gameEnd(Dealer dealer) {
        // 勝者判定
        JudgementHand judgementHand = new JudgementHand();
        Card[][] hands = dealer.getHands();
        // 役の強さと名前をセット
        int winnerNumber = 0;
        int mostStrength = 100;
        int[] handStrength = dealer.getHandStrength();
        String[] handNames = dealer.getHandNames();
        for (int i = 0; i < hands.length; i++) {
            handStrength[i] = judgementHand.judgementHand(hands[i]);
            if (handStrength[i] < mostStrength) {
                mostStrength = handStrength[i];
                winnerNumber = i;
            }
            handNames[i] = judgementHand.getHand_Str(handStrength[i]);
            System.out.println(handStrength[i] + ": " + handNames[i]);
        }
        boolean isDraw = true;
        for (int strength : handStrength) {
            if (strength != mostStrength) {
                isDraw = false;
                break;
            }
        }
        if (isDraw) {
            winnerNumber = -1;
        }
        if (winnerNumber != -1) {
            System.out.println("勝者は" + dealer.getPlayerNames()[winnerNumber]);
        } else {
            System.out.println("引き分け");
        }
        dealer.setHandStrength(handStrength);
        dealer.setHandNames(handNames);
        dealer.setWinnerNumber(winnerNumber);
    }

    /* <summary> デバック用 手札表示 </summary> */
    public void printHands(Dealer dealer) {
        for (Card[] cards : dealer.getHands()) {
            for (Card card : cards) {
                if (card == null) {
                    System.out.print("null, ");
                } else {
                    System.out.print(card.toString() + "( " + card.getIsHave() + " ), ");
                }
            }
            System.out.println();
        }
    }

    /* <summary> デバック用 山札表示 </summary> */
    // private void printDeck(Dealer dealer) {
    // for (Card[] cards : dealer.getDeck()) {
    // for (Card card : cards) {
    // if (card == null) {
    // System.out.print("null, ");
    // } else {
    // System.out.print(card.toString() + "( " + card.getIsHave() + " ), ");
    // }
    // }
    // System.out.println();
    // }
    // }
}
