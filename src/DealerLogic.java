import java.util.Random;

/**
 * Dealerを操作するLogicクラス
 */
public class DealerLogic {
    private Random random = new Random();

    /**  ゲームをスタートする処理  */
    public void gameStart(Dealer dealer) {
        // 山札を作成
        createDeck(dealer);
        // 最初の手札を決定
        dealFirstHands(dealer);
        // ターンを進行する（1ターンめ）
        dealer.incrementCount_Of_Turn();
    }

    /**  山札を作成する処理  */
    private void createDeck(Dealer dealer) {
        // スーツ数×数位で構成される山札を作成
        Card[][] deck = dealer.getDeck();
        for (int suit = 0; suit < dealer.getNUM_OF_SUIT(); suit++) {
            for (int number = 1; number <= dealer.getNUM_OF_NUM(); number++) {
                deck[suit][number - 1] = new Card(suit, number);
            }
        }
        dealer.setDeck(deck);
        System.out.println("山札を生成しました");
    }

    /**  全プレイヤーの最初の手札を決定  */
    private void dealFirstHands(Dealer dealer) {
        // プレイヤー数×カード所持数で構成されるカードを作成
        Card[][] hands = dealer.getHands();
        for (int num_of_player = 0; num_of_player < dealer.getNUM_OF_PLAYER(); num_of_player++) {
            for (int num_of_card = 0; num_of_card < dealer.getNUM_OF_CARD(); num_of_card++) {
                // 手札は山札から1枚ランダムに選出される
                hands[num_of_player][num_of_card] = dealOneCard(dealer);
            }
        }
        System.out.println("手札を生成しました");
        dealer.setHands(hands);
    }

    /**  
     * ランダムに山札から1枚カードを返す処理
     * @return Card
    */
    private Card dealOneCard(Dealer dealer) {
        // ランダムに山札からカードを取得（山札のカードが出てくるまでランダム値を振り直す）
        while (true) {
            int suit = random.nextInt(dealer.getNUM_OF_SUIT());
            int number = random.nextInt(dealer.getNUM_OF_NUM());
            Card card = dealer.getDeck()[suit][number];
            if (!card.getIsHand()) {
                // カードの所持状態を真にしてから返す
                card.setIsHand(true);
                return card;
            }
        }
    }

    /**
     *  手札のカードと山札のカードを交換する 
     * @param cafdNum(山札に戻すカードが何枚目か,0~4),reservedCard（既に新たなカードが決まっていたら指定する,nullなら山札から引く）
     * @return Card
     */
    public Card exchangeCard(Dealer dealer, int cardNum, Card reservedCard) {
        // 指定のカードを山札に戻す
        leaveOneCard(dealer, cardNum);
        // 山札から新たなカードを引く
        Card newCard;
        if (reservedCard == null) {
            newCard = dealOneCard(dealer);
        } else {
            newCard = reservedCard;
            newCard.setIsHand(true);
        }
        // 手札に新しいカードをセット
        dealer.setHand(dealer.getNum_Of_TurnPlayer(), cardNum, newCard);
        printHands(dealer);
        return newCard;
    }

    /**
     *  指定のカードを山札に戻す 
     * @param cafdNum(山札に戻すカードが何枚目か,0~4)
     */
    private void leaveOneCard(Dealer dealer, int cardNum) {
        Card leaveCard = dealer.getHands()[dealer.getNum_Of_TurnPlayer()][cardNum];
        // 返すカードの所持状態を偽にして
        leaveCard.setIsHand(false);
        // 山札に返す
        dealer.setDeckCard(leaveCard.getSuit_Integer(), cardNum, leaveCard);
        dealer.setHand(dealer.getNum_Of_TurnPlayer(), cardNum, null);
    }

    /**  
     * プレイヤーの行動終了後、ゲームが次にどう進行するかを返す処理
     *  @return 次のゲーム進行コマンド（プレイヤー変更,ターン進行,ゲーム終了）
     *  */
    public String opperationEnd(Dealer dealer) {
        // プレイヤー総行動回数をインクリメント
        dealer.incrementCount_Of_Operations();
        // プレイヤー数で割り切れる行動回数なら、次のターンに進む
        int count_of_operations = dealer.getCount_Of_Operations();
        if (count_of_operations % dealer.getNUM_OF_PLAYER() == 0) {
            // ただし行動回数上限に達した場合はゲーム終了
            if (dealer.getNUM_OF_OPERATIONS() == count_of_operations) {
                return "GAMEEND";
            }
            // ターン数をインクリメント
            dealer.incrementCount_Of_Turn();
            return "NEXTTURN";
        }
        // ターン数は変わらずに操作するプレイヤーが変わる
        return "CHANGEPLAYER";
    }

    /**  勝者を判定する処理  */
    public void judgeTheWinner(Dealer dealer) {
        // 役判定を行うJudgementHandLogicクラスを利用する
        JudgementHandLogic judgementHand = new JudgementHandLogic();
        // プレイヤー全員の手札を取得
        Card[][] hands = dealer.getHands();
        int winnerNumber = 0;// 勝者のプレイヤー番号
        int mostStrength = (int) Double.POSITIVE_INFINITY;// 最も強い役（数値）初期値は最弱値
        int[] handStrength = dealer.getHandStrength();//役の強さ
        String[] handNames = dealer.getHandNames();//役の名前

        for (int i = 0; i < hands.length; i++) {
            // 役の強さ（数値）を取得
            handStrength[i] = judgementHand.judgementHand(hands[i]);
            // 現状最も強い役だったら入れ替える
            if (handStrength[i] < mostStrength) {
                mostStrength = handStrength[i];
                winnerNumber = i;
            }
            // 役の名前を取得
            handNames[i] = judgementHand.getHand_Str(handStrength[i]);
            System.out.println(handStrength[i] + ": " + handNames[i]);
        }
        // 引き分けの可能性を考える
        boolean isDraw = true;
        // プレイヤー全員が現状最も強い役だった場合引き分け
        for (int strength : handStrength) {
            if (strength != mostStrength) {
                isDraw = false;
                break;
            }
        }
        // 判定
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

    /**  デバック用 手札表示  */
    public void printHands(Dealer dealer) {
        for (Card[] cards : dealer.getHands()) {
            for (Card card : cards) {
                if (card == null) {
                    System.out.print("null, ");
                } else {
                    System.out.print(card.toString() + "( " + card.getIsHand() + " ), ");
                }
            }
            System.out.println();
        }
    }

    /**  デバック用 山札表示  */
    // private void printDeck(Dealer dealer) {
    // for (Card[] cards : dealer.getDeck()) {
    // for (Card card : cards) {
    // if (card == null) {
    // System.out.print("null, ");
    // } else {
    // System.out.print(card.toString() + "( " + card.getIsHand() + " ), ");
    // }
    // }
    // System.out.println();
    // }
    // }
}
