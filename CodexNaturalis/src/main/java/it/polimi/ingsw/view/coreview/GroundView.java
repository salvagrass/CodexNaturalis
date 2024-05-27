package it.polimi.ingsw.view.coreview;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.GoldCard;
import it.polimi.ingsw.model.item.card.ResourceCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.util.json.CardSerializeLogic;
import it.polimi.ingsw.util.ObjectiveSerializeLogic;
import it.polimi.ingsw.util.Symbol;

import java.util.HashMap;

/**
 * A class representing a view of the common ground.
 */
public class GroundView {
  private final CardView[] resGroundView; // 0-1-2 RESOURCE CARDS
  private final CardView[] goldGroundView; // 3-4-5 GOLD CARDS
  private final ObjectiveView[] objViews;

  /**
   * Constructor for the GroundView class.
   * Initializes the Gson instance and arrays for resource cards, gold cards, and objectives.
   */
  public GroundView(CardView[] resCards,CardView[] goldCards,ObjectiveView[] objs) {
    this.resGroundView = resCards;
    this.goldGroundView = goldCards;
    this.objViews = objs;
  }

  /**
   * Updates the ground view with a new card or objective.
   *
   * @param idx     The index representing the type of item being updated.
   * @param card    The card placed on the ground
   */
  public void updGround(int idx,CardView card) {
    if (idx>=0 && idx<=2) {
        this.resGroundView[idx] = card;
    }else if(idx>=3 && idx<=5){
        this.goldGroundView[idx-3] = card;
    }
  }

  /**
   * Generates an ASCII representation of the common ground.
   *
   * @return The ASCII representation of the common ground.
   */
  public String asciiGround(){
      return CardView.showAsciiArt(resGroundView) + '\n' +
            CardView.showAsciiArt(goldGroundView) + '\n' +
            ObjectiveView.showAsciiArt(objViews) + '\n';
  }
}
