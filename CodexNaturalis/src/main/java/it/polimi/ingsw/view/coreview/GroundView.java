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

/**
 * A class representing a view of the common ground.
 */
public class GroundView {
  private final Gson gson;
  private final Symbol[] decks; // 0 FOR RESOURCE DECK, 1 FOR GOLD DECK
  private final ResourceCard[] groundResCards; // 2-3 RESOURCE CARDS
  private final GoldCard[] groundGoldCards; // 4-5 GOLD CARDS
  private final String[] commonObjectives;

  /**
   * COnstructor of the class.
   */
  public GroundView() {
    this.gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Card.class, new CardSerializeLogic()).
            registerTypeHierarchyAdapter(Objective.class,new ObjectiveSerializeLogic()).
            create();
    this.decks = new Symbol[2];
    this.groundResCards = new ResourceCard[2];
    this.groundGoldCards = new GoldCard[2];
    this.commonObjectives = new String[2];
  }
  
  /**
   * Update the object value.
   * 
   * @param idx The index of the element to be updated.
   * @param updated The new object value.
   */
  public void updGround(int idx, String updated) {
    if (idx == 0 || idx == 1) {
      this.decks[idx] = this.gson.fromJson(updated, Symbol.class);
    } else if (idx == 2 || idx == 3) {
      this.groundResCards[idx - 2] = this.gson.fromJson(updated, ResourceCard.class);
    } else if (idx == 4 || idx == 5) {
      this.groundGoldCards[idx - 4] = this.gson.fromJson(updated, GoldCard.class);
    } else {
      this.commonObjectives[idx - 6] = this.gson.fromJson(updated,Objective.class).toString();
    }
  }

  @Override
  public String toString(){
    String view = "Decks: [ResDeck(0): " + decks[0].name() + ", GoldDeck(1): " + decks[1].name()
                                    + "]\n ResGroundCards:\n(2) " + groundResCards[0].toString() + "\n(3) " + groundResCards[1].toString()
                                    + "\n GoldGroundCards:\n(4) " + groundGoldCards[0].toString() + "\n(5) " + groundGoldCards[1].toString()
                                    + "\nObjectives:\n" + this.commonObjectives[0] + "\n" + this.commonObjectives[1];
    return view;
  }
}
