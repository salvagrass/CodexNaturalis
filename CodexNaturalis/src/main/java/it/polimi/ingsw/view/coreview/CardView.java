package it.polimi.ingsw.view.coreview;

import it.polimi.ingsw.model.item.card.*;
import it.polimi.ingsw.util.Symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class representing a view of the card
 *  ---------------------
    | GOLD    3       P |
    |-------------------|
    | F       R       W |
    |         R         |
    | M       R       B |
    |-------------------|
    | W W F             |
    ---------------------
 */
public class CardView {

    private record Coord(int x, int y, boolean side) {

    }
    private final String cardID;
    private final Symbol color;
    private boolean face;
    private final Map<Coord, Character> asciiArt;
    private static final int asciiArtXDim = 9;
    private static final int asciiArtYDim = 21;

    /**
     * Constructor of the class.
     * 
     * @param card The card to be represented.
     */
    public CardView(Card card) {
        this.cardID = card.getId();
        this.color = card.getColor();
        this.face = card.getShowingFace();
        this.asciiArt = new HashMap<>();
        this.constructHeader(card);
    }

    public void setFace(boolean face) {
        this.face = face;
    }
    
    public boolean getFace() {
        return this.face;

    }

    public Symbol getColor(){
        return this.color;
    }

    @Override
    public String toString(){
        return cardID;
    }

    private void constructHeader(Card card) {
        switch (card.getClass().getSimpleName()) {
            case "StartingCard" -> this.push("START");
            case "ResourceCard" -> {
                this.push("RES");
                ResourceCard resCard = (ResourceCard) card;
                this.push("" + resCard.getCardPoints(), 1, (asciiArtYDim - 1) / 2, true);
            }
        case "GoldCard" -> {
            this.push("GOLD");
            GoldCard goldCard = (GoldCard) card;
            this.push("" + goldCard.getCardPoints(), 1, (asciiArtYDim - 1) / 2, true);
            this.constructFooter(goldCard);
        }
        case "GoldCardCorner" -> {
            this.push("GOLD");
            GoldCardCorner goldCard = (GoldCardCorner) card;
            this.push("" + goldCard.getCardPoints(), 1, (asciiArtYDim - 1) / 2, true);
            this.push("■", 1, asciiArtYDim - 3, true);
            this.constructFooter(goldCard);
        }
        case "GoldCardSymbol" -> {
            this.push("GOLD");
            GoldCardSymbol goldCard = (GoldCardSymbol) card;
            this.push("" + goldCard.getCardPoints(), 1, (asciiArtYDim - 1) / 2, true);
            this.push(goldCard.getSym().name().substring(0,1), 1, asciiArtYDim - 3, true);
            this.constructFooter(goldCard);
        }
      }
      constructBody(card);
    }

    private void constructBody(Card card){
        card.setShowingFace(true);
        this.setCorners(card.getShowingCorners(),true);

        card.setShowingFace(false);
        this.setCorners(card.getShowingCorners(),false);

        String[] center = this.nameInitial(card.getCenter());
        for(int i=0;i<center.length;i++){
            this.push(center[i], 4 - (center.length/2)+i, (asciiArtYDim - 1) / 2, false);
        }
    }
    
    private void constructFooter(GoldCard card) {
      int i = 2; 
      for (Symbol sym : card.getRequirements().keySet()) {
        int length = card.getRequirements().get(sym);
        for(int k = 0; k < length; k++, i += 2){
          this.push(sym.name().replace("FUNGUS","M").substring(0,1), asciiArtXDim - 2, i, true);
        }
      }
    }
    
    private void push(String s, int row, int begin, boolean side) {
      int length = s.length();
      for (int i = 0; i < length; i++) {
        this.asciiArt.put(new Coord(row,begin + i,side), s.charAt(i));
      }
    }

    private void push(String s) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            this.asciiArt.put(new Coord(1, 2+i, true), s.charAt(i));
            this.asciiArt.put(new Coord(1, 2+i, false), s.charAt(i));
        }
    }

    private String[] nameInitial(Symbol[] symbols){
        String[] strings = new String[symbols.length];
        for(int i=0;i< symbols.length;i++){
            strings[i] = symbols[i].name().replace("FUNGUS","M").substring(0,1);
        }
        return strings;
    }

    private void setCorners(Symbol[] symbols,boolean side){
        int i,counter;
        int[] xPos = new int[]{3,5};
        int[] yPos = new int[]{2,asciiArtYDim-3};
        int[] values = new int[]{0,1,3,2};
        String[] names = this.nameInitial(symbols);
        for(i=0,counter=0;i<2;i++){
            for(int j=0;j<2;j++,counter++){
                this.push(names[values[counter]],xPos[i],yPos[j],side);
            }
        }
    }

    public static String showAsciiArt(CardView... cards) {
        int i,j,column,pos;
        CardView cardView;
        StringBuilder asciiArt = new StringBuilder();
        for(i=0;i<asciiArtXDim;i++){
            for(j=0,column=0,cardView=cards[0],pos=0;j<asciiArtYDim*cards.length+cards.length;j++,column++){
                if(column%asciiArtYDim==0&&column>0){
                    pos++;
                    if(pos<cards.length)
                        cardView = cards[pos];
                    column=-1;
                    asciiArt.append('\t');
                }else if(i==0||i==asciiArtXDim-1) {
                    asciiArt.append('-');
                }else if(column==0) {
                    asciiArt.append('|');
                    if(3<=i && i<=5)
                        asciiArt.append(cardView.color.ANSI_COLOR);
                }else if(column==asciiArtYDim-1){
                    if(3<=i && i<=5)
                        asciiArt.append(Symbol.ANSI_RESET);
                    asciiArt.append('|');
                }else if(i==2||i==6)
                    asciiArt.append('-');
                else{
                    char c = cardView.asciiArt.getOrDefault(new Coord(i,column,cardView.face),' ');
                    asciiArt.append(c);
                }
            }
            asciiArt.append('\n');
        }
        return asciiArt.toString();
    }


    public static String showAsciiArt(List<CardView> cards){
        CardView[] temp = new CardView[cards.size()];
        for(int i=0;i<cards.size();i++)
            temp[i] = cards.get(i);
        return showAsciiArt(temp);
    }
}
