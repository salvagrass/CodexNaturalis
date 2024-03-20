public class ResourceCard extends DrawableCard {
    public ResourceCard(Symbol[] syms, Symbol[] center, int points) {
        this.frontCorners = syms;
        this.center = center;
        this.showingFace = false;
        this.cardPoints = points;
    }

    public ResourceCard(ResourceCardData e) {
        this.frontCorners = e.getFrontCorners();
        this.center = e.getCenter();
        this.showingFace = e.getShowingFace();
        this.cardPoints = e.getCardPoints();
    }
}