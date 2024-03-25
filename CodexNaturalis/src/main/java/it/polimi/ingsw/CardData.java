package it.polimi.ingsw;
import java.util.EnumMap;
import java.util.Map;

/**
 * Enumeration with all the data for the different cards
 */

public enum CardData {

    //FOR DRAWABLE CARDS(DATA_TYPE,FRONT_CORNER,CENTER,REQUIREMENTS(IF PRESENT),SYMBOL(IF PRESENT),CARD_POINTS)
    CR1(CardType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.FUNGUS}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR2(CardType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.FUNGUS, Symbol.EMPTY, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR3(CardType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.FUNGUS, Symbol.FUNGUS}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR4(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.FUNGUS, Symbol.FUNGUS, Symbol.EMPTY}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR5(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.FEATHER, Symbol.FUNGUS, Symbol.GRASS}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR6(CardType.RESOURCE, new Symbol[] {Symbol.POTION, Symbol.FUNGUS, Symbol.WOLF, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR7(CardType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.BUTTERFLY, Symbol.EMPTY, Symbol.SCROLL}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR8(CardType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.FUNGUS}, new Symbol[] {Symbol.FUNGUS}, 1),
    CR9(CardType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.EMPTY}, new Symbol[] {Symbol.FUNGUS}, 1),
    CR10(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.EMPTY, Symbol.FUNGUS}, new Symbol[] {Symbol.FUNGUS}, 1),
    CR11(CardType.RESOURCE, new Symbol[] {Symbol.GRASS, Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.GRASS}, new Symbol[] {Symbol.GRASS}, 0),
    CR12(CardType.RESOURCE, new Symbol[] {Symbol.GRASS, Symbol.GRASS, Symbol.EMPTY, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.GRASS}, 0),
    CR13(CardType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.GRASS, Symbol.GRASS}, new Symbol[] {Symbol.GRASS}, 0),
    CR14(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.GRASS, Symbol.GRASS, Symbol.EMPTY}, new Symbol[] {Symbol.GRASS}, 0),
    CR15(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.BUTTERFLY, Symbol.GRASS, Symbol.FEATHER}, new Symbol[] {Symbol.GRASS}, 0),
    CR16(CardType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.GRASS, Symbol.POTION, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.GRASS}, 0),
    CR17(CardType.RESOURCE, new Symbol[] {Symbol.SCROLL, Symbol.NOTAVAILABLE, Symbol.WOLF, Symbol.GRASS}, new Symbol[] {Symbol.GRASS}, 0),
    CR18(CardType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.GRASS}, new Symbol[] {Symbol.GRASS}, 1),
    CR19(CardType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.EMPTY, Symbol.GRASS, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.GRASS}, 1),
    CR20(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.GRASS, Symbol.EMPTY, Symbol.EMPTY}, new Symbol[] {Symbol.GRASS}, 1),
    CR21(CardType.RESOURCE, new Symbol[] {Symbol.WOLF, Symbol.WOLF, Symbol.NOTAVAILABLE, Symbol.EMPTY}, new Symbol[] {Symbol.WOLF}, 0),
    CR22(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.WOLF, Symbol.WOLF}, new Symbol[] {Symbol.WOLF}, 0),
    CR23(CardType.RESOURCE, new Symbol[] {Symbol.WOLF, Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.WOLF}, new Symbol[] {Symbol.WOLF}, 0),
    CR24(CardType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.WOLF, Symbol.WOLF, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.WOLF}, 0),
    CR25(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.BUTTERFLY, Symbol.WOLF, Symbol.POTION}, new Symbol[] {Symbol.WOLF}, 0),
    CR26(CardType.RESOURCE, new Symbol[] {Symbol.GRASS, Symbol.WOLF, Symbol.POTION, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.WOLF}, 0),
    CR27(CardType.RESOURCE, new Symbol[] {Symbol.FEATHER, Symbol.NOTAVAILABLE, Symbol.FUNGUS, Symbol.WOLF}, new Symbol[] {Symbol.WOLF}, 0),
    CR28(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.EMPTY, Symbol.WOLF}, new Symbol[] {Symbol.WOLF}, 1),
    CR29(CardType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.WOLF, Symbol.EMPTY}, new Symbol[] {Symbol.WOLF}, 1),
    CR30(CardType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.WOLF, Symbol.NOTAVAILABLE, Symbol.EMPTY}, new Symbol[] {Symbol.WOLF}, 1),
    CR31(CardType.RESOURCE, new Symbol[] {Symbol.BUTTERFLY, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR32(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.BUTTERFLY, Symbol.BUTTERFLY}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR33(CardType.RESOURCE, new Symbol[] {Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.BUTTERFLY}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR34(CardType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.BUTTERFLY, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR35(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.WOLF}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR36(CardType.RESOURCE, new Symbol[] {Symbol.SCROLL, Symbol.BUTTERFLY, Symbol.FUNGUS, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR37(CardType.RESOURCE, new Symbol[] {Symbol.BUTTERFLY, Symbol.GRASS, Symbol.NOTAVAILABLE, Symbol.POTION}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR38(CardType.RESOURCE, new Symbol[] {Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.EMPTY}, new Symbol[] {Symbol.BUTTERFLY}, 1),
    CR39(CardType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.EMPTY, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.BUTTERFLY}, 1),
    CR40(CardType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.BUTTERFLY, Symbol.EMPTY, Symbol.EMPTY}, new Symbol[] {Symbol.BUTTERFLY}, 1);

    //FOR STARTING CARDS (FRONT CORNERS,BACK CORNERS,CENTER)

    private CardType type;
    private Symbol[] frontCorners;
    private Symbol[] backCorners;
    private Symbol[] center;
    private int cardPoints;
    private Symbol[] requirements;
    private Symbol goldSymbol;

    /**
     * Retrieves the type of the card
     * @return the type of the card
     */
    /**
     * Constructs a CardData instance.
     * @param type the type of the card
     * @param frontCorners the symbols on the front corners of the card
     * @param center the symbols in the center of the back of the card
     * @param cardPoints the points associated with the card
     */
    CardData(CardType type, Symbol[] frontCorners, Symbol[] center, int cardPoints) {
        this.type = type;
        this.frontCorners = frontCorners;
        this.center = center;
        this.cardPoints = cardPoints;
    }

    /**
     * Constructs a CardData instance with requirements (GoldCard).
     * @param type the type of the card
     * @param frontCorners the symbols on the front corners of the card
     * @param center the symbols in the center of the back of the card
     * @param requirements the symbols representing requirements for the card
     * @param cardPoints the points associated with the card
     */
    CardData(CardType type, Symbol[] frontCorners, Symbol[] center, Symbol[] requirements, int cardPoints) {
        this(type, frontCorners, center, cardPoints);
        this.requirements = requirements;
    }

    /**
     * Constructs a CardData instance with requirements and a symbol.
     * @param type the type of the card
     * @param frontCorners the symbols on the front corners of the card
     * @param center the symbols in the center of the back of the card
     * @param requirements the symbols representing requirements for the card
     * @param goldSymbol the symbol modifier for the gold cards
     * @param cardPoints the points associated with the card
     */
    CardData(CardType type, Symbol[] frontCorners, Symbol[] center, Symbol[] requirements, Symbol goldSymbol, int cardPoints) {
        this(type, frontCorners, center, requirements, cardPoints);
        this.goldSymbol = goldSymbol;
    }

    /**
     * Constructs a CardData instance for start cards.
     * @param frontCorners the symbols on the front corners of the card
     * @param backCorners the symbols on the back corners of the card
     * @param center the symbols in the center of the card
     */
    CardData(Symbol[] frontCorners,Symbol[] backCorners, Symbol[] center) {
        this.type = CardType.START;
        this.frontCorners = frontCorners;
        this.backCorners = backCorners;
        this.center = center;
        cardPoints = 0;
    }

    /**
     * Getter for the type of the card
     * @return the type of the card
     */
    public CardType getType() {
        return type;
    }


    /**
     * Retrieves the symbols on the front corners of the card.
     * @return An array containing the symbols on the front corners of the card.
     *         The index corresponds to the angle starting from the top-left corner and moving clockwise.
     *         Index 0 represents the top-left corner, index 1 represents the top-right corner,
     *         index 2 represents the bottom-right corner, and index 3 represents the bottom-left corner.
     */
    public Symbol[] getFrontCorners() {
        return frontCorners.clone();
    }


    /**
     * Retrieves the symbols on the back corners of the card.
     * @return An array containing the symbols on the back corners of the card.
     *         The index corresponds to the angle starting from the top-left corner and moving clockwise.
     *         Index 0 represents the top-left corner, index 1 represents the top-right corner,
     *         index 2 represents the bottom-right corner, and index 3 represents the bottom-left corner.
     */
    public Symbol[] getBackCorners() {
        return backCorners.clone();
    }


    /**
     * Retrieves the symbols in the center of the back of the card.
     * @return An array containing the symbols in the center of the card.
     */
    public Symbol[] getCenter() {
        return center.clone();
    }

    /**
     * Retrieves the points associated with the card.
     * @return The points associated with the card.
     */
    public int getCardPoints() {
        return cardPoints;
    }

    /**
     * Retrieves the symbols representing requirements (if there's any) for the card.
     * @return An array containing the symbols representing requirements for the card.
     */
    public Symbol[] getRequirements() {
        return requirements.clone();
    }

    /**
     * Retrieves the symbol modifier for the gold cards (if there's any)
     * @return the symbol modifier
     */
    public Symbol getGoldSymbol() {
        return goldSymbol;
    }

    /**
     * Calculates the amount of each type of card in the enumeration
     * @return a map in which each type is linked to its counter
     */
    public static Map<CardType,Integer> getCounters(){
        Map<CardType,Integer> counters = new EnumMap<>(CardType.class);
        for(CardType value : CardType.values())
            counters.put(value,0);
        for(CardData data: CardData.values()){
               CardType value = data.getType();
               counters.put(value,counters.get(value)+1);
        }
        return counters;
    }
}