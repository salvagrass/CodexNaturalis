package it.polimi.ingsw.model.gameplane;

import it.polimi.ingsw.model.util.PawnColor;
import it.polimi.ingsw.model.item.card.DrawableCard;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.model.item.card.StartingCard;
import it.polimi.ingsw.model.gameplane.CommonGround;
import it.polimi.ingsw.model.util.exception.InvalidDrawException;
import it.polimi.ingsw.model.util.exception.InvalidPlacementException;
import it.polimi.ingsw.model.util.exception.InvalidSelectionException;

/**
 * This class represents a Player in the game.
 */
public class Player {
    private GameBoard playingField;
    private DrawableCard[] hand;
    private Objective secretObjective;
    private int points;
    private PawnColor pawnColor;
    private String nickname;

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
    }

    /**
     * Returns the current points of the player.
     *
     * @return the current points of the player.
     */
    public int getPoints() {
        return this.points;
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
    public void placeCard(int idx, int x, int y, boolean face) throws InvalidPlacementException {
        boolean prevFace = hand[idx].getShowingFace();
        hand[idx].setShowingFace(face);
        try{
            updPoints(playingField.addCard(hand[idx], x, y));
        }catch(InvalidPlacementException e){
            hand[idx].setShowingFace(prevFace);
            throw new InvalidPlacementException();   
        }
        hand[idx] = null;
    }
    
    /**
     * Updates the score of the objectives.
     * This method updates the score of the objectives using the updPoints method.
     * Firstly it updates the score of the secret objective, then it updates the score of each shared objective.
     * 
     * @param ground 
     */
    public void addObjectivesScore(CommonGround ground) {
        updPoints(this.playingField.countObjPoints(secretObjective));
        for(Objective obj : ground.getCommonObjectives()){
            updPoints(this.playingField.countObjPoints(obj));
        }
    }

    /**
     * Draws a card from the common ground and adds it to the player's hand.
     *
     * @param ground The common ground from which to draw the card.
     * @param sel The index of the selected deck to draw from.
     */
    public void draw(CommonGround ground, int sel) throws InvalidSelectionException, InvalidDrawException {
        int i;
        for(i = 0; hand[i] != null && i < hand.length; i++);
        if(i<hand.length)
            hand[i] = ground.draw(sel);
    }

    /**
     * Draws the first three cards from the common ground (2 resources and 1 gold) and adds them to the player's hand.
     *
     * @param ground The common ground from which to draw the cards.
     */
    public void drawFirstThree(CommonGround ground){
        try{
            hand[0] = ground.draw(0);
            hand[1] = ground.draw(0);
            hand[2] = ground.draw(3);
        }catch(Exception e){}
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
}
