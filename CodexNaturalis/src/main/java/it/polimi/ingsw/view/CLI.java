package it.polimi.ingsw.view;

import it.polimi.ingsw.view.coreview.CardView;

import java.util.List;
import java.util.Scanner;

public class CLI extends UserView {
  private final Scanner input;

  public CLI() {
    input = new Scanner(System.in);
  }

  public String getInput() {
    return input.nextLine();
  }

  public void show(String s) {
    System.out.print(s);
  }

  public void showHand(List<CardView> hand){
    this.show(CardView.showAsciiArt(hand));
  }
}
