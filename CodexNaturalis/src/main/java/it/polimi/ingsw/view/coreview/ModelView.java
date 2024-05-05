package it.polimi.ingsw.view.coreview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.model.item.card.Card;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.util.Symbol;

/**
 * A class containing a view of every item in the model.
 */
public class ModelView {
  private final GroundView ground;
  private final List<BoardView> boards;
  private final List<CardView> handView;
  private String secrObj;

  /**
   * Constructor of the class.
   */
  public ModelView() {
    this.ground = new GroundView();
    this.boards = new ArrayList<>(4);
    this.handView = new LinkedList<>();
  }

  public void setObj(String obj){
    this.secrObj = obj;
  }

  public void setHand(List<DrawableCard> cards) {
    for (DrawableCard card : cards) {
      this.handView.add(new CardView(card));
    }
  }

  /**
   * Update a card of the hand.
   * 
   * @param card The new card.
   * @param idx The index of the hand to be updated.
   */
  public void updHand(Card card,int idx){
    handView.add(idx,new CardView(card));
  }

  public void updGround(int idx, String updString) {
    this.ground.updGround(idx, updString);
  }
  
  public List<CardView> getHand() {
    return this.handView;
  }
  
  public String getSecrObj(){
    return this.secrObj;
  }

  public GroundView getGround(){
    return this.ground;
  }

  public int getHandSize(){
    return this.handView.size();
  }

  public BoardView getBoard(int idx){
    //TODO REVISE
    if(this.boards.size()<=idx)
      this.boards.add(idx,new BoardView());
    return this.boards.get(idx);
  }

  public CardView getHandCard(int idx){
    return this.handView.get(idx);
  }

  public void removeHandCard(int idx){
    this.handView.remove(idx);
  }
}
