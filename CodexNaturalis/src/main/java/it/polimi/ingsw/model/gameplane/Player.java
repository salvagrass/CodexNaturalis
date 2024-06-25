package it.polimi.ingsw.model.gameplane;

import it.polimi.ingsw.util.PawnColor;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.util.exception.InvalidDrawException;
import it.polimi.ingsw.util.exception.InvalidPlacementException;
import it.polimi.ingsw.util.exception.InvalidSelectionException;

import java.util.Map;

/**
 * This class represents a Player in the game.
 */
public class Player {
    private int points;
    private final GameBoard playingField;
    private final DrawableCard[] hand;
    private final Objective secretObjective;
    private final PawnColor pawnColor;
    private final String nickname;
    private int completedObjs;

    /**
     * Constructs a new Player object.
     *
     * @param nickname The player's nickname.
     * @param secretObjective The player's secret objective.
     * @param pawnColor The color of the player's pawn.
     */
    public Player(String nickname, Objective secretObjective, PawnColor pawnColor){
        this.playingField = new GameBoard();
        this.nickname = nickname;
        this.secretObjective = secretObjective;
        this.pawnColor = pawnColor;
        this.points = 0;
        this.hand = new DrawableCard[3];
    }

    /**
     * Returns the current points of the player.
     *
     * @return the current points of the player.
     */
    public int getPoints() {
        return this.points;
    }

    public DrawableCard[] getHand() {
        return this.hand;
    }

    /**
     * Returns a map that associates a symbol to how many times is present on the board
     * @return the map associating symbols and integer
     */
    public Map<Symbol,Integer> getInventory(){
        return this.playingField.getCountResources();
    }

    public String getNickname(){
        return this.nickname;
    }

    public PawnColor getPawnColor(){
        return this.pawnColor;
    }

    public Objective getSecretObjective(){
        return this.secretObjective;
    }

    /**
     * Places the given starting card on the player's game board.
     *
     * @param start the starting card to be placed
     */
    public void placeStartingCard(StartingCard start) {
        playingField.addStartingCard(start);
    }

    /**
     * Places a card on the game board at the specified position.
     *
     * @param idx The index of the card in the player's hand.
     * @param x The x-coordinate of the position on the board.
     * @param y The y-coordinate of the position on the board.
     * @param face Whether the card should be placed face up or face down.
     * @throws InvalidPlacementException If the card cannot be placed at the specified position.
     */
    public DrawableCard placeCard(int idx, int x, int y, boolean face) throws InvalidPlacementException {
        boolean prevFace = hand[idx].getShowingFace();
        hand[idx].setShowingFace(face);
        try{
            updPoints(playingField.addCard(hand[idx], x, y));
            DrawableCard c = hand[idx];
            hand[idx] = null;
            return c;
        }catch(InvalidPlacementException e){
            hand[idx].setShowingFace(prevFace);
            throw new InvalidPlacementException();
        }
    }

    //TODO javadoc
    public void addObjectivesScore(CommonGround ground) {
        int points;
        int count = 0;
        updPoints(points = this.playingField.countObjPoints(secretObjective));
        count += points / this.secretObjective.getPoints();
        for(Objective obj : ground.getCommonObjectives()){
            updPoints(points = this.playingField.countObjPoints(obj));
            count += points / obj.getPoints();
        }
        this.completedObjs = count;
    }

    /**
     * Draws a card from the common ground and adds it to the player's hand.
     *
     * @param ground The common ground from which to draw the card.
     * @param sel The index of the selected deck to draw from.
     */
    public DrawableCard draw(CommonGround ground, int sel) throws InvalidSelectionException, InvalidDrawException {
        int i;
        i = 0;
        while (i<hand.length && hand[i]!=null) {
            i++;
        }
        if(i< hand.length) {
            hand[i] = ground.draw(sel);
            return hand[i];
        }
        return null;
    }

    /**
     * Draws the first three cards from the common ground (2 resources and 1 gold) and adds them to the player's hand.
     *
     * @param ground The common ground from which to draw the cards.
     */
    public DrawableCard[] drawFirstThree(CommonGround ground){
        try{
            hand[0] = ground.draw(0);
            hand[1] = ground.draw(0);
            hand[2] = ground.draw(3);
            return new DrawableCard[] {hand[0], hand[1], hand[2]};
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Updates the player's points.
     * This method adds a specified number of points to the player's current score.
     * The number of points added is determined by the card played by the player.
     *
     * @param p the number of points to be added to the player's score
     */
    private void updPoints(int p) {
        this.points += p;
    }

    public int getCompletedObjs(){
        return this.completedObjs;
    }
}
