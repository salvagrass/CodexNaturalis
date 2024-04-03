package it.polimi.ingsw.model.gameplane;

import it.polimi.ingsw.model.utils.PawnColor;
import it.polimi.ingsw.model.item.DrawableCard;
import it.polimi.ingsw.model.item.Objective;
import it.polimi.ingsw.model.item.StartingCard;

/**
 * This class represents a Player in the game.
 */
public class Player {
    private GameBoard playingField;
    private CommonGround shared;
    private DrawableCard[] hand;
    private Objective secretObjective;
    private int points;
    private PawnColor pawnColor;
    private String nickname;

    /**
     * Constructs a new Player.
     * @param shared
     * @param nickname
     */
    public Player(CommonGround shared, String nickname) {
        this.playingField= new GameBoard();
        this.shared=shared;
        this.nickname=nickname;
        this.points=0;
        this.hand=drawFirstThree();
    }

    /**
     * Returns the shared common ground.
     *
     * @return Returns the shared common ground
     */
    public CommonGround getShared() {
        return this.shared;
    }

    /**
     * Returns the current points of the player.
     *
     * @return the current points of the player
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
     * Allows the player to choose between two different objectives.
     * The objectives are provided by the getRandomObj method from the CommonGround class,
     * which is called twice.
     */
    public void chooseSecretObject() {
        Object obj1 = getShared().getRandomObjective();
        Object obj2 = getShared().getRandomObjective();
        //comunicazione col server
        //this...
    }
    /**
     * Allows the player to draw the first three cards.
     * Two cards are drawn from the resource deck and one from the gold card deck.
     * The draw method from the CommonGround class is called three times.
     * Each returned value from the draw method is saved in an element of a three-element array.
     *
     * @return an array of the first three cards drawn by the player
     */
    public DrawableCard[] drawFirstThree() {
        DrawableCard[] cards = new DrawableCard[3];
        cards[0] = shared.draw(0);
        cards[1] = shared.draw(0);
        cards[2] = shared.draw(3);
        return cards;
    }

    /**
     * Attempts to place a card from the player's hand onto the game board at the specified position.
     * If the position is valid (as determined by the checkPosition method of the GameBoard class),
     * the card is placed and the player's points are updated accordingly.
     *
     * @param idx the index of the card in the player's hand
     * @param x the x-coordinate of the position on the game board
     * @param y the y-coordinate of the position on the game board
     * @return true if the card was successfully placed, false otherwise
     */
    public boolean placeCard(int idx, int x, int y) {
        if(playingField.checkPosition(x,y)){
            updPoints(playingField.addCard(hand[idx],x,y));
            return true;
        }
        return false;
    }
    /**
     * Allows the player to draw a card from a specific deck in the common ground.
     * The deck is selected based on the provided index.
     *
     * @param sel the index of the deck from which to draw a card
     */
    public void drawCard(int sel) {
            shared.draw(sel);
    }
    /**
     * Updates the score of the objectives.
     * This method updates the score of the objectives using the updPoints method.
     * First, it updates the score of the secret objective, then it updates the score of each shared objective.
     */
    public void addObjectivesScore() {
        updPoints(this.playingField.countObjPoints(secretObjective));
        for(Objective obj: this.shared.getCommonObjectives())
            updPoints(this.playingField.countObjPoints(obj));
    }

    /**
     * Updates the player's points.
     * This method adds a specified number of points to the player's current score.
     * The number of points added is determined by the card played by the player.
     *
     * @param p the number of points to be added to the player's score
     */
    private void updPoints(int p) {
        this.points = this.points + p;
    }
}
