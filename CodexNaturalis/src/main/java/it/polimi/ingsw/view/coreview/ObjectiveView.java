package it.polimi.ingsw.view.coreview;

import java.util.HashMap;
import java.util.Map;

import it.polimi.ingsw.model.item.objective.DiagonalType;
import it.polimi.ingsw.model.item.objective.DispObjective;
import it.polimi.ingsw.model.item.objective.Disposition;
import it.polimi.ingsw.model.item.objective.LType;
import it.polimi.ingsw.model.item.objective.Objective;
import it.polimi.ingsw.model.item.objective.ResourceObjective;
import it.polimi.ingsw.util.Symbol;

/**
 * A class representing a view of the objective.
 */
public class ObjectiveView {

    /**
     * A record representing coordinates for ASCII art representation.
     */
    private record Coord(int x,int y){

    }

    private final Map<Coord,Character> asciiObj;
    private final String id;
    private static final int asciiXdim = 7;
    private static final int asciiYdim = 13;

    /**
     * Constructor for the ObjectiveView class.
     *
     * @param obj The objective to be represented.
     */
    public ObjectiveView(Objective obj){
        this.id = obj.getId();
        asciiObj = new HashMap<>();
        createHeader(obj);
    }


    /**
     * Retrieves the id of the objective
     * @return the id of the objective
     */
    public String getId(){
        return id;
    }

    /**
     * Creates the header of the objective's ASCII art representation.
     *
     * @param obj The objective for which the header is created.
     */
    public void createHeader(Objective obj) {
        this.asciiObj.put(new Coord(1, (asciiYdim - 1) / 2), (char) (obj.getPoints() + '0'));
        switch(obj.getClass().getSimpleName()){
            case "ResourceObjective" -> {
                ResourceObjective resObj = (ResourceObjective) obj;
                createBody(resObj);
            }
            case "DispObjective" ->{
                DispObjective dispObj = (DispObjective) obj;
                Disposition disposition = dispObj.getDisposition();
                switch(disposition.getClass().getSimpleName()){
                    case "LType" ->{
                        LType lType = (LType) disposition;
                        createBody(lType);
                    }
                    case "DiagonalType" ->{
                        DiagonalType diagonalType = (DiagonalType) disposition;
                        createBody(diagonalType);
                    }
                }
            }
        }
    }

    /**
     * Creates the body of the objective's ASCII art representation for a ResourceObjective.
     *
     * @param resObj The ResourceObjective for which the body is created.
     */
    public void createBody(ResourceObjective resObj){
        Map<Symbol,Integer> mapping = resObj.getResources();
        int x = 3;
        int yMiddle = (asciiYdim-1)/2;
        for(Symbol s:mapping.keySet()){
            for(int i = 0; i < mapping.get(s); i++){
                this.asciiObj.put(new Coord(x, yMiddle), s.INITIAL);
                x++;
            }
        }
    }

    /**
     * Creates the body of the objective's ASCII art representation for an LType disposition.
     *
     * @param lType The LType disposition for which the body is created.
     */
    public void createBody(LType lType){
        int yMiddle = (asciiYdim-1)/2;
        int xLast;
        Coord coord;
        switch(lType.getFirsCardPos()){
            case 0 -> {
                coord = new Coord(3,yMiddle-1);
                xLast = 5;
            }
            case 1 -> {
                coord = new Coord(3,yMiddle+1);
                xLast = 5;
            }
            case 2 -> {
                coord = new Coord(5, yMiddle+1);
                xLast = 3;
            }
            case 3 -> {
                coord = new Coord(5, yMiddle-1);
                xLast = 3;
            }
            default -> throw new RuntimeException();
        }
        asciiObj.put(coord,lType.getSecondaryColor().INITIAL);
        asciiObj.put(new Coord(4,yMiddle),lType.getMainColor().INITIAL);
        asciiObj.put(new Coord(xLast,yMiddle),lType.getMainColor().INITIAL);
    }

    /**
     * Creates the body of the objective's ASCII art representation for a DiagonalType disposition.
     *
     * @param diagonalType The DiagonalType disposition for which the body is created.
     */
    public void createBody(DiagonalType diagonalType) {
        if (diagonalType.getDirection() == 1) {
            for (int i = 3; i < 6; i++) {
                this.asciiObj.put(new Coord(i, 2 + i), diagonalType.getColor().INITIAL);
            }
        } else {
            for (int i = 5, j = 5; i >= 3; i--, j++) {
                this.asciiObj.put(new Coord(i, j), diagonalType.getColor().INITIAL);
            }
        }
    }

    /**
     * Generates the ASCII art representation of one or more objectives.
     *
     * @param objs The objectives for which the ASCII art representation will be generated.
     * @return The ASCII art representation of the objectives.
     */
    public static String showAsciiArt(ObjectiveView... objs) {
        StringBuilder sb = new StringBuilder();
        int j,counter,pos;
        ObjectiveView view;
        for (int i = 0; i < asciiXdim; i++) {
            for (j = 0, counter=0,pos=0,view = objs[0]; j < asciiYdim * objs.length + objs.length; j++,counter++) {
                if (counter == asciiYdim) {
                    pos++;
                    if(pos<objs.length) {
                        view = objs[pos];
                    }
                    counter = -1;
                    sb.append('\t');
                } else if ((counter == 0 || counter == asciiYdim-1) && i != 0 && i != asciiXdim - 1) {
                    sb.append('|');
                } else if (i == 0 || i == asciiXdim - 1 || i == 2) {
                    sb.append('-');
                } else {
                    char c = view.asciiObj.getOrDefault(new Coord(i, counter), ' ');
                    sb.append(Symbol.symbolFromInit(c).ANSI_COLOR).append(c).append(Symbol.ANSI_RESET);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}

/*
|------------
|     2     |
|-----------|
|      X    |
|     X     |
|    X      |
-------------
 */
