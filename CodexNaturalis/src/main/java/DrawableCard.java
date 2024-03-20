public abstract class DrawableCard extends Card {
    final int cardPoints;

    public String getPoints() {
        return this.cardPoints + "";
    }

    public boolean checkRequirements(GameBoard board) {
        return true;
    }

    public int givePoints(GameBoard board, int x, int y) {
        if (getShowingFace()) {
            return this.cardPoints;
        }
        return 0;
    }
}
