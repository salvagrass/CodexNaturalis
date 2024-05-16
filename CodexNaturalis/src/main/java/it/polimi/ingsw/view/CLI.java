package it.polimi.ingsw.view;

import it.polimi.ingsw.view.coreview.CardView;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CLI implements UserView {
  private final Scanner input;

  public CLI() {
    input = new Scanner(System.in);
  }

  @Override
  public void outcome(GameScene scene) {

  }

  public String getInput() {
    return input.nextLine();
  }

  public void show(String s) {
    System.out.print(s);
  }

  public void showCards(List<CardView> cards){
    this.show(CardView.showAsciiArt(cards));
  }

  @Override
  public void showWinner(Map<String, Integer> positions, Map<String, Integer> points, String name) {
    if(positions.get(name) == 1){
      System.out.println("WINNER!");
    }
    List<String> names = positions.keySet().stream().sorted(Comparator.comparingInt(positions::get)).toList();
    for(String n : names){
      System.out.println(positions.get(n) + " " + n + ": " + points.get(n) + " points");
    }
  }

  public void showCards(CardView... cards){this.show(CardView.showAsciiArt(cards));}
}
