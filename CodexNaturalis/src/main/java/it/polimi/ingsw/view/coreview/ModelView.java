package it.polimi.ingsw.view.coreview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A class containing a view of every item in the model.
 */
public class ModelView {
  private GroundView ground;
  private final List<BoardView> boards;
  private final List<CardView> handView;
  private ObjectiveView secrObj;

  /**
   * Constructor of the class.
   */
  public ModelView() {
    this.boards = new ArrayList<>(4);
    this.handView = new LinkedList<>();
  }


  public void setGround(GroundView view){
    this.ground = view;
  }

  /**
   * Sets the view of the secrete objective of the player.
   *
   * @param obj The secret objective to be set.
   */
  public void setObj(ObjectiveView obj){
    this.secrObj = obj;
  }

  /**
   * Sets the view of the hand of cards of the player.
   *
   * @param handView The list of cards to be set as the hand.
   */
  public void setHand(List<CardView> handView) {
      this.handView.addAll(handView);
  }

  /**
   * Updates a card in the hand.
   *
   * @param card The new card to be updated in the hand.
   * @param idx  The index of the hand where the card is updated.
   */
  public void updHand(CardView card,int idx){
    handView.add(idx,card);
  }

  /**
   * Updates the ground view with a new card or objective.
   *
   * @param idx The index representing the type of item being updated.
   */
  public void updGround(int idx, CardView card) {
    this.ground.updGround(idx,card);
  }

  /**
   * Retrieves the hand of cards in the model view.
   *
   * @return The list of cards in the hand.
   */
  public List<CardView> getHand() {
    return this.handView;
  }

  /**
   * Retrieves the secret objective in the model view.
   *
   * @return The secret objective.
   */
  public ObjectiveView getSecrObj(){
    return this.secrObj;
  }

  /**
   * Retrieves the ground view in the model.
   *
   * @return The ground view.
   */
  public GroundView getGround(){
    return this.ground;
  }

  /**
   * Retrieves the number of cards currently in the player's hand.
   *
   * @return The number of cards in the hand.
   */
  public int getHandSize(){
    return this.handView.size();
  }

  /**
   * Retrieves a specific board view from the model view.
   *
   * @param idx The index of the board view to retrieve.
   * @return The board view at the specified index.
   */
  public BoardView getBoard(int idx){
    return this.boards.get(idx);
  }

  /**
   * Sets the number of boards for the model view.
   *
   * @param numPlayers The number of players in the game.
   */
  public void allocateBoards(int numPlayers){
    for(int i=0;i<numPlayers;i++)
      this.boards.add(i,new BoardView());
  }


  public void setBoard(int idx,BoardView boardView){
    this.boards.remove(idx);
    this.boards.add(idx,boardView);
  }

  /**
   * Retrieves a card from the hand at a specific index.
   *
   * @param idx The index of the card to retrieve from the hand.
   * @return The card view at the specified index.
   */
  public CardView getHandCard(int idx){
    return this.handView.get(idx);
  }

  /**
   * Removes a card from the hand at a specific index.
   *
   * @param idx The index of the card to remove from the hand.
   */
  public void removeHandCard(int idx){
    this.handView.remove(idx);
  }
}
