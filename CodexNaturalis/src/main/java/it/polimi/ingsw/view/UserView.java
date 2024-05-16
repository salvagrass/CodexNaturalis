package it.polimi.ingsw.view;

import it.polimi.ingsw.view.coreview.CardView;

import java.util.List;
import java.util.Map;

/**
 * A class linking the controller and the view. Is intended to be inherited to be spcified.
 */
public interface UserView {
  void outcome(GameScene scene);
  String getInput();
  void show(String s);

  void showCards(CardView... cards);
  void showCards(List<CardView> cards);

  void showWinner(Map<String, Integer> positions, Map<String, Integer> points, String name);
}
