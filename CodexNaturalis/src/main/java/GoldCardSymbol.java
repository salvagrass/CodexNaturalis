public class GoldCardSymbol extends GoldCard {
    final Symbol sym;

    public GoldCardSymbol(GoldCardSymbolData e) {
        this.frontCorners = e.getFrontCorners();
        this.center = e.getCenter();
        this.showingFace = e.getShowingFace();
        this.cardPoints = e.getCardPoints();
        this.requirements = e.getRequirements();
        this.sym = e.getSymbol();
    }

    public String getPoints() {
        return this.cardPoints + " " + sym;
    }

    public int givePoints(GameBoard g, int x, int y) {
        if (!getShowingFace()) {
            return 0;
        }
        return g.getCountResources().get(sym) * this.cardPoints;
    }
}