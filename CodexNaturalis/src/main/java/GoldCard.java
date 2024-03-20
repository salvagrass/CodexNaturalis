import java.util.HashMap;
import java.util.Map.Entry;

public class GoldCard extends DrawableCard {
    final HashMap<Symbol, Integer> requirements;

    public GoldCard(GoldCardData e) {
        this.frontCorners = e.getFrontCorners();
        this.center = e.getCenter();
        this.showingFace = e.getShowingFace();
        this.cardPoints = e.getCardPoints();
        this.requirements= e.getRequirements();
    }

    public boolean checkRequirements(GameBoard board) {
        HashMap<Symbol, Integer> map = board.getCountResources();
        for (Entry<Symbol, Integer> set : requirements.entrySet()) {
            if (map.get(set.getKey()) < set.getValue()) {
                return false;
            }
        }
        return true;
    }
}