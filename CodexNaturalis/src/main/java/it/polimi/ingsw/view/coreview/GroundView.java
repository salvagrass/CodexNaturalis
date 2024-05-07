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
  private final Gson gson;
  private final CardView[] resGroundView; // 2-3 RESOURCE CARDS
  private final CardView[] goldGroundView; // 4-5 GOLD CARDS
  private final ObjectiveView[] objViews;

  /**
   * Constructor for the GroundView class.
   * Initializes the Gson instance and arrays for resource cards, gold cards, and objectives.
   */
  public GroundView() {
    this.gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Card.class, new CardSerializeLogic()).
            registerTypeHierarchyAdapter(Objective.class,new ObjectiveSerializeLogic()).
            create();
    this.resGroundView = new CardView[3];
    this.goldGroundView = new CardView[3];
    this.objViews = new ObjectiveView[2];
  }

  /**
   * Updates the ground view with a new card or objective.
   *
   * @param idx     The index representing the type of item being updated.
   * @param updated The JSON string representing the updated card or objective.
   */
  public void updGround(int idx, String updated) {
    if (idx>=0 && idx<=2) {
      if(idx==0){
        Symbol center = this.gson.fromJson(updated,Symbol.class);
        this.resGroundView[idx] = new CardView(new ResourceCard(new Symbol[]{Symbol.NOTAVAILABLE,Symbol.NOTAVAILABLE,Symbol.NOTAVAILABLE,Symbol.NOTAVAILABLE},new Symbol[]{center}, 0, "backView"));
      }else{
        this.resGroundView[idx] = new CardView(this.gson.fromJson(updated, ResourceCard.class));
      }
    } else if (idx>=3 && idx<=5) {
      if(idx==3){
        Symbol center = this.gson.fromJson(updated,Symbol.class);
        this.goldGroundView[idx-3] = new CardView(new GoldCard(new Symbol[]{Symbol.NOTAVAILABLE,Symbol.NOTAVAILABLE,Symbol.NOTAVAILABLE,Symbol.NOTAVAILABLE},new Symbol[]{center},new HashMap<>() ,0, "backView"));
      }else{
        this.goldGroundView[idx-3] = new CardView(this.gson.fromJson(updated, GoldCard.class));
      }
    } else {
      this.objViews[idx - 6] = new ObjectiveView(this.gson.fromJson(updated,Objective.class));
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
