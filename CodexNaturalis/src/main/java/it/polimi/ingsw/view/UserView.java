package it.polimi.ingsw.view;

import it.polimi.ingsw.view.coreview.CardView;

import java.util.List;

/**
 * A class linking the controller and the view. Is intended to be inherited to be spcified.
 */
public abstract class UserView {
  public abstract String getInput();
  public abstract void show(String s);

  public void showCards(CardView... cards){}
  public void showCards(List<CardView> cards){}

}
