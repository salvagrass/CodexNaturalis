public enum ResourceCardData {
    //istanze da aggiungere
    //formato:
    //ISTANZA1(frontCorners, center, cardPoints), ISTANZA2(frontCorners, center, cardPoints), ...;
    //es.
    C1(new Symbol[] { Symbol.EMPTY, Symbol.EMPTY, Symbol.FUNGUS, Symbol.EMPTY},
            new Symbol[] {Symbol.BUTTERFLY},
            0);
    private Symbol[] frontCorners;
    private Symbol[] center;
    private boolean showingFace;
    private int cardPoints;

    private ResourceCardData(Symbol[] frontCorners, Symbol[] center, int cardPoints) {
        this.frontCorners = frontCorners;
        this.center = center;
        this.showingFace = false;
        this.cardPoints = cardPoints;
    }

    public Symbol[] getFrontCorners() {
        return this.frontCorners.clone();
    }

    public Symbol[] getCenter() {
        return this.center.clone();
    }

    public boolean getShowingFace() {
        return this.showingFace;
    }

    public int getCardPoints() {
        return this.cardPoints;
    }
}
