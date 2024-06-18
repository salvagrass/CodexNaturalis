package it.polimi.ingsw.controller.userclient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.serverfunction.MsgCreator;
import it.polimi.ingsw.util.Message;
import it.polimi.ingsw.util.PawnColor;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.util.Symbol;
import it.polimi.ingsw.view.UserView;
import it.polimi.ingsw.view.coreview.BoardView;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ObjectiveView;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Client{
  private final UserView userView;
  private final Gson gson;
  int port;

  public Client(UserView userView) {
    this.userView = userView;
    this.gson = new Gson();
  }

  public void handleMsg(String message) {
    String[] commands = message.split("\\|");
    for (String command : commands) {
      String[] dictate = command.split(";");
      switch (dictate[0]) {
        case "CHOOSE" -> new Thread(() -> sendMsg(this.userView.getInput())).start();
        case "CAPACITY" -> this.userView.setCapacity(Integer.parseInt(dictate[1]));
        case "JOINED" -> {
          Set<String> players = this.gson.fromJson(dictate[1], new TypeToken<Set<String>>() {
          }.getType());
          this.userView.playerJoined(players);
        }
        case "UPDCOLORS" -> {
          Set<PawnColor> colors = this.gson.fromJson(dictate[1], new TypeToken<Set<PawnColor>>() {
          }.getType());
          this.userView.updColors(colors);
        }
        case "CREATEHANDVIEW" -> {
          List<CardView> hand = this.gson.fromJson(dictate[1], new TypeToken<List<CardView>>() {
          }.getType());
          this.userView.setHand(hand);
        }
        case "CREATEGROUNDVIEW" -> {
          CardView[] resCards = this.gson.fromJson(dictate[1], CardView[].class);
          CardView[] goldCards = this.gson.fromJson(dictate[2], CardView[].class);
          ObjectiveView[] objs = this.gson.fromJson(dictate[3], ObjectiveView[].class);
          this.userView.createGroundView(resCards, goldCards, objs);
        }
        case "LOADHIDDENOBJ" -> this.userView.loadObj(this.gson.fromJson(dictate[1],ObjectiveView.class));
        case "LOADBOARD" -> this.userView.loadBoard(Integer.parseInt(dictate[1]), this.gson.fromJson(dictate[2], BoardView.class));
        case "TURNORDER" -> {
          List<String> names = this.gson.fromJson(dictate[1], new TypeToken<List<String>>() {
          }.getType());
          this.userView.setNames(names);
        }
        case "UPDHANDVIEW" -> {
          CardView card = this.gson.fromJson(dictate[1], CardView.class);
          this.userView.updHandView(card);
        }
        case "UPDGROUNDVIEW" -> {
          int idx = Integer.parseInt(dictate[1]);
          CardView card = this.gson.fromJson(dictate[2], CardView.class);
          this.userView.updGroundView(idx, card);
        }
        case "UPDBOARDVIEW" -> {
          int turnIdx = Integer.parseInt(dictate[1]);
          int x = Integer.parseInt(dictate[2]);
          int y = Integer.parseInt(dictate[3]);
          CardView card = this.gson.fromJson(dictate[4], CardView.class);
          Map<Symbol, Integer> inventory = this.gson.fromJson(dictate[5], new TypeToken<Map<Symbol, Integer>>() {
          }.getType());
          this.userView.updBoardView(turnIdx, card, x, y, inventory);
          //TODO check set token
        }
        case "UPDPOINTS" -> {
          int turnIdx = Integer.parseInt(dictate[1]);
          int newPoints = Integer.parseInt(dictate[2]);
          this.userView.updPoints(turnIdx, newPoints);
        }
        case "LEADERBOARD" -> {
          Map<String, Integer> lead = this.gson.fromJson(dictate[1], new TypeToken<Map<String, Integer>>() {
          }.getType());
          this.userView.setPositions(lead);
        }
        case "NAME" -> this.userView.setName(dictate[1]);
        case "CURRPLAYER" -> this.userView.setCurrPlayer(dictate[1]);
        case "CONFIRM" -> this.userView.sendConfirm(true, MsgCreator.deformat(dictate[1]));
        case "DENIED" -> this.userView.sendConfirm(false, MsgCreator.deformat(dictate[1]));
        case "CHANGEPHASE" -> {
          Phase phase = Phase.valueOf(dictate[1]);
          switch (phase) {
            case Phase.STARTCARD -> this.userView.setCurrPhase(phase, this.gson.fromJson(dictate[2], CardView[].class));
            case Phase.OBJECTIVE -> this.userView.setCurrPhase(phase, this.gson.fromJson(dictate[2], ObjectiveView[].class));
            case Phase.SERVERCRASHED,EXIT -> {
              closeConnection();
              this.userView.setCurrPhase(phase);
            }
            default -> this.userView.setCurrPhase(phase);
          }
        }
        case "CHAT" -> this.userView.addChatMessage(new Message(dictate[2], dictate[1], dictate[3]));
        case "COLORCHOSEN" -> this.userView.setPlayerColor(dictate[1], PawnColor.valueOf(dictate[2]));
      }
    }
  }


  abstract void sendMsg(String msg);
  abstract void closeConnection();
  public abstract void placeCard(int idx,int x,int y,boolean face);
  public abstract void drawCard(int idx);
  public abstract void sendChatMessage(String msg);
  public abstract void connect(String address);
}
