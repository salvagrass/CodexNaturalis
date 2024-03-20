public abstract class Card {
    Symbol[] frontCorners;
    Symbol[] center;
    boolean showingFace;

    public boolean getShowingFace() {
        return this.showingFace;
    }

    public void setShowingFace(boolean newVal) {
        this.showingFace = newVal;
    }

    public Symbol[] getShowingCorners() {
        if (getShowingFace()) {
            return this.frontCorners.clone();
        }
        return new Symbol[] { Symbol.EMPTY, Symbol.EMPTY, Symbol.EMPTY, Symbol.EMPTY };
    }

    public Symbol[] getCenter() {
        if (getShowingFace()) {
            return new Symbol[] {};
        }
        return this.center.clone();
    }
}