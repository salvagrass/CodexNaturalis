public class StartingCard extends Card {
    private final Symbol[] backCorners;

    public StartingCard(StartingEnum e) {
        this.frontCorners = e.getFrontCorners();
        this.backCorners = e.getBackCorners();
        this.center = e.getCenter();
        this.showingFace = e.getShowingFace();
    }

    public Symbol[] getShowingCorners() {
        if (getShowingFace()) {
            return this.frontCorners.clone();
        }
        return this.backCorners.clone();
    }
}