package it.polimi.ingsw;

public enum CardData {

    //(DATA_TYPE,FRONT_CORNER,CENTER,REQUIREMENTS(IF PRESENT),SYMBOL(IF PRESENT),CARD_POINTS)
    C1(DataType.RESOURCE, new Symbol[] { Symbol.EMPTY, Symbol.EMPTY, Symbol.FUNGUS, Symbol.EMPTY}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    C2(DataType.GOLD, new Symbol[]{Symbol.FUNGUS,Symbol.EMPTY,Symbol.FUNGUS,Symbol.WOLF}, new Symbol[]{Symbol.FUNGUS}, new Symbol[]{Symbol.FUNGUS,Symbol.FUNGUS,Symbol.FUNGUS,Symbol.WOLF}, 5),
    C3(DataType.GOLDSYMBOL, new Symbol[]{Symbol.FUNGUS,Symbol.EMPTY,Symbol.FUNGUS,Symbol.WOLF}, new Symbol[]{Symbol.FUNGUS}, new Symbol[]{Symbol.FUNGUS,Symbol.FUNGUS,Symbol.WOLF},Symbol.FEATHER, 2),
    C4(DataType.GOLDCORNER, new Symbol[]{Symbol.WOLF,Symbol.NOTAVAILABLE,Symbol.FUNGUS,Symbol.WOLF}, new Symbol[]{Symbol.WOLF}, new Symbol[]{Symbol.WOLF,Symbol.WOLF,Symbol.WOLF}, 2),

    CR1(DataType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.FUNGUS}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR2(DataType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.FUNGUS, Symbol.EMPTY, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR3(DataType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.FUNGUS, Symbol.FUNGUS}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR4(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.FUNGUS, Symbol.FUNGUS, Symbol.EMPTY}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR5(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.FEATHER, Symbol.FUNGUS, Symbol.GRASS}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR6(DataType.RESOURCE, new Symbol[] {Symbol.POTION, Symbol.FUNGUS, Symbol.WOLF, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR7(DataType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.BUTTERFLY, Symbol.EMPTY, Symbol.SCROLL}, new Symbol[] {Symbol.FUNGUS}, 0),
    CR8(DataType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.FUNGUS}, new Symbol[] {Symbol.FUNGUS}, 1),
    CR9(DataType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.EMPTY}, new Symbol[] {Symbol.FUNGUS}, 1),
    CR10(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.EMPTY, Symbol.FUNGUS}, new Symbol[] {Symbol.FUNGUS}, 1),
    CR11(DataType.RESOURCE, new Symbol[] {Symbol.GRASS, Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.GRASS}, new Symbol[] {Symbol.GRASS}, 0),
    CR12(DataType.RESOURCE, new Symbol[] {Symbol.GRASS, Symbol.GRASS, Symbol.EMPTY, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.GRASS}, 0),
    CR13(DataType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.GRASS, Symbol.GRASS}, new Symbol[] {Symbol.GRASS}, 0),
    CR14(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.GRASS, Symbol.GRASS, Symbol.EMPTY}, new Symbol[] {Symbol.GRASS}, 0),
    CR15(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.BUTTERFLY, Symbol.GRASS, Symbol.FEATHER}, new Symbol[] {Symbol.GRASS}, 0),
    CR16(DataType.RESOURCE, new Symbol[] {Symbol.FUNGUS, Symbol.GRASS, Symbol.POTION, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.GRASS}, 0),
    CR17(DataType.RESOURCE, new Symbol[] {Symbol.SCROLL, Symbol.NOTAVAILABLE, Symbol.WOLF, Symbol.GRASS}, new Symbol[] {Symbol.GRASS}, 0),
    CR18(DataType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.GRASS}, new Symbol[] {Symbol.GRASS}, 1),
    CR19(DataType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.EMPTY, Symbol.GRASS, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.GRASS}, 1),
    CR20(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.GRASS, Symbol.EMPTY, Symbol.EMPTY}, new Symbol[] {Symbol.GRASS}, 1),
    CR21(DataType.RESOURCE, new Symbol[] {Symbol.WOLF, Symbol.WOLF, Symbol.NOTAVAILABLE, Symbol.EMPTY}, new Symbol[] {Symbol.WOLF}, 0),
    CR22(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.WOLF, Symbol.WOLF}, new Symbol[] {Symbol.WOLF}, 0),
    CR23(DataType.RESOURCE, new Symbol[] {Symbol.WOLF, Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.WOLF}, new Symbol[] {Symbol.WOLF}, 0),
    CR24(DataType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.WOLF, Symbol.WOLF, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.WOLF}, 0),
    CR25(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.BUTTERFLY, Symbol.WOLF, Symbol.POTION}, new Symbol[] {Symbol.WOLF}, 0),
    CR26(DataType.RESOURCE, new Symbol[] {Symbol.GRASS, Symbol.WOLF, Symbol.POTION, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.WOLF}, 0),
    CR27(DataType.RESOURCE, new Symbol[] {Symbol.FEATHER, Symbol.NOTAVAILABLE, Symbol.FUNGUS, Symbol.WOLF}, new Symbol[] {Symbol.WOLF}, 0),
    CR28(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.EMPTY, Symbol.WOLF}, new Symbol[] {Symbol.WOLF}, 1),
    CR29(DataType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.NOTAVAILABLE, Symbol.WOLF, Symbol.EMPTY}, new Symbol[] {Symbol.WOLF}, 1),
    CR30(DataType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.WOLF, Symbol.NOTAVAILABLE, Symbol.EMPTY}, new Symbol[] {Symbol.WOLF}, 1),
    CR31(DataType.RESOURCE, new Symbol[] {Symbol.BUTTERFLY, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR32(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.BUTTERFLY, Symbol.BUTTERFLY}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR33(DataType.RESOURCE, new Symbol[] {Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.BUTTERFLY}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR34(DataType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.BUTTERFLY, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR35(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.FEATHER, Symbol.BUTTERFLY, Symbol.WOLF}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR36(DataType.RESOURCE, new Symbol[] {Symbol.SCROLL, Symbol.BUTTERFLY, Symbol.FUNGUS, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR37(DataType.RESOURCE, new Symbol[] {Symbol.BUTTERFLY, Symbol.GRASS, Symbol.NOTAVAILABLE, Symbol.POTION}, new Symbol[] {Symbol.BUTTERFLY}, 0),
    CR38(DataType.RESOURCE, new Symbol[] {Symbol.BUTTERFLY, Symbol.NOTAVAILABLE, Symbol.EMPTY, Symbol.EMPTY}, new Symbol[] {Symbol.BUTTERFLY}, 1),
    CR39(DataType.RESOURCE, new Symbol[] {Symbol.EMPTY, Symbol.EMPTY, Symbol.BUTTERFLY, Symbol.NOTAVAILABLE}, new Symbol[] {Symbol.BUTTERFLY}, 1),
    CR40(DataType.RESOURCE, new Symbol[] {Symbol.NOTAVAILABLE, Symbol.BUTTERFLY, Symbol.EMPTY, Symbol.EMPTY}, new Symbol[] {Symbol.BUTTERFLY}, 1),



    private DataType type;
    private Symbol[] frontCorners;
    private Symbol[] center;
    private int cardPoints;
    private Symbol[] requirements;
    private Symbol goldSymbol;

    public DataType getType() {
        return type;
    }

    public Symbol[] getFrontCorners() {
        return frontCorners.clone();
    }

    public Symbol[] getCenter() {
        return center.clone();
    }

    public int getCardPoints() {
        return cardPoints;
    }

    public Symbol[] getRequirements() {
        return requirements.clone();
    }

    public Symbol getGoldSymbol() {
        return goldSymbol;
    }

    private CardData(DataType type, Symbol[] frontCorners, Symbol[] center, int cardPoints) {
        this.type = type;
        this.frontCorners = frontCorners;
        this.center = center;
        this.cardPoints = cardPoints;
    }

    private CardData(DataType type, Symbol[] frontCorners, Symbol[] center, Symbol[] requirements, int cardPoints) {
        this(type, frontCorners, center, cardPoints);
        this.requirements = requirements;
    }

    private CardData(DataType type, Symbol[] frontCorners, Symbol[] center, Symbol[] requirements, Symbol goldSymbol, int cardPoints) {
        this(type, frontCorners, center, requirements, cardPoints);
        this.goldSymbol = goldSymbol;
    }
}