public class GoldCardCorner extends GoldCard {

    public GoldCardCorner(GoldCardData e){
        this.frontCorners = e.getFrontCorners();
        this.center = e.getCenter();
        this.showingFace = e.getShowingFace();
        this.cardPoints = e.getCardPoints();
        this.requirements = e.getRequirements();
    }

    public String getPoints() {
        return this.cardPoints + " EACH CORNER";
    }

    public int givePoints(GameBoard g, int x, int y) {
        int points = 0;
        int i, j;
        int l1, l2;
        DrawableCard[][] board = g.getBoard();
        if (!getShowingFace()) {
            return 0;
        }
        l1 = board.length;
        l2 = board[0].length;
        for (i = x - 1 >= 0 ? x - 1 : x + 1; i <= x + 1 && i < l1; i += 2) {
            for (j = y - 1 >= 0 ? y - 1 : y + 1; j <= y + 1 && j < l2; j += 2) {
                points += board[i][j] != null ? 1 : 0;
            }
        }
        return points * this.cardPoints;
    }
}
