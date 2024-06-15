package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.serverfunction.MsgCreator;
import it.polimi.ingsw.util.Message;
import it.polimi.ingsw.util.PawnColor;
import it.polimi.ingsw.util.Phase;
import it.polimi.ingsw.view.coreview.CardView;
import it.polimi.ingsw.view.coreview.ItemView;
import it.polimi.ingsw.view.coreview.ObjectiveView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CLI extends UserView{
    private final Scanner in;
    private String command;
    private final PriorityQueue<Phase> phaseQueue;
    private ExecutorService executorService;
    private String currPlayer;
    private int capacity;


    
    public CLI() {
        super();
        this.phaseQueue = new PriorityQueue<>((firstPhase, secondPhase) -> {
            if(firstPhase == Phase.CRASHED)
                return -1;
            else if (secondPhase == Phase.CRASHED) {
                return 1;
            }
            return 0;
        });
        this.colors = new HashSet<>();
        this.colors.addAll(Arrays.stream(PawnColor.values()).toList());
        this.playersInLobby = new HashSet<>();
        this.in = new Scanner(System.in);
        this.turnOrder = new ArrayList<>(4);
        this.leaderboard = new HashMap<>();
        this.chats = new LinkedList<>();
    }

    public void tryConnection(String addr) throws RuntimeException {
        super.tryConnection(addr);
        waitChangePhase();
    }

    @Override
    public void setCurrPlayer(String currPlayer){
        synchronized (this.currPlayerLock) {
            this.currPlayer = currPlayer;
            this.currPlayerLock.notifyAll();
        }
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        super.setCapacity(capacity);
    }

    private synchronized void setCommand(String msg){
        this.command = msg;
        this.notifyAll();
    }

    private synchronized String getCommand(int timeoutMillis) throws TimeoutException{
        while(this.command==null){
            try {
                this.wait(timeoutMillis);
                if(this.command==null){
                    throw new TimeoutException();
                }
            } catch (InterruptedException e) {
                //TODO MAYBE RUNTIME EXCEPTION IS OVERKILL
                throw new RuntimeException(e);
            }
        }
        String tmp = this.command;
        this.command = null;
        return tmp;
    }

    private synchronized String getCommand(){
        while(this.command==null){
            try {
                this.wait();
            } catch (InterruptedException e) {
                //TODO MAYBE RUNTIME EXCEPTION IS OVERKILL
                throw new RuntimeException(e);
            }
        }
        String tmp = this.command;
        this.command = null;
        return tmp;
    }

    @Override
    public void playerJoined(Set<String> players){
        synchronized (this.playersInLobby) {
            this.playersInLobby.addAll(players);
            if(this.waitForPlayers.get() && this.playersInLobby.size() < this.capacity){
                System.out.println("Waiting for players\nRemaining: " + (this.capacity-this.playersInLobby.size()));
            }
        }
    }

    @Override
    public void setCurrPhase(Phase phase, ItemView... items) {
        synchronized(this.phaseQueue){
            phaseQueue.add(phase);
            this.phaseQueue.notifyAll();
            super.setCurrPhase(phase, items);
        }
    }

    private void waitChangePhase(){
        synchronized (this.phaseQueue) {
            while (this.phaseQueue.isEmpty()) {
                try {
                    this.phaseQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.currPhase = this.phaseQueue.remove();
            if(this.currPhase==Phase.CRASHED){
                this.phaseQueue.removeIf(p->!(p==Phase.MENU));
            }
        }
        changePhase(this.currPhase);
    }

    public void waitConfirm(){
        synchronized(this.phaseQueue){
            if(this.phaseQueue.peek()==Phase.CRASHED){
                synchronized(this.confirmValue) {
                    this.confirmValue.set(true);
                }
                return;
            }
        }
        synchronized(this.confirmValue){
            try {
                this.confirmValue.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendConfirm(boolean confirmValue,String confirmMsg){
        super.sendConfirm(confirmValue, confirmMsg);
        synchronized (this.confirmValue){
            this.confirmValue.notifyAll();
        }
    }

    private void showMenu(){
        System.out.println("1)Create Game\n2)Join Game\n3)Load Game");
        System.out.print("Insert choice: ");
        String choice = this.in.nextLine();
        while (!choice.matches("^(exit|[1-3])$")) {
            System.out.print("Choice not valid\nRetry: ");
            choice = in.nextLine();
        }
        scanInput(choice);
        waitChangePhase();
    }

    private void create() {
        String constraint = "ID: 6 alphanumeric characters - Capacity: 2-4 players";
        String regex = "^(exit)|([a-zA-Z0-9]{6}\\s[2-4])$";
        System.out.print("Insert Room ID (" + constraint+") : ");
        String input = in.nextLine();
        while(!input.matches(regex)){
            System.out.print("Invalid choice ("+ constraint +")\nRetry: ");
            input = in.nextLine();
        }
        sendUntilConfirm(input, constraint, (x) -> x.matches(regex));
        waitChangePhase();
    }
    
    private void join() {
        String constraint = "6 alphanumeric characters";
        String regex = "^(exit)|([a-zA-Z0-9]{6})";
        System.out.print("Insert Room ID (" + constraint + "): " );
        String input = in.nextLine();
        while(!input.matches(regex)){
            System.out.print("Invalid ID (" + constraint +")\nRetry: ");
            input = in.nextLine();
        }
        sendUntilConfirm(input,constraint,(x)->x.matches(regex));
        waitChangePhase();
    }

    private void chooseName(){
        String constraint = "1 to 8 alphabetical characters";
        String regex = "^[a-zA-Z]{1,8}$";
        System.out.print("Choose your name (" + constraint +"): ");
        String name = in.nextLine();
        while(!name.matches(regex)){
            System.out.print("Invalid name ("+ constraint +")\nRetry: ");
            name = in.nextLine();
        }
        sendUntilConfirm(name, constraint, (x)->x.matches(regex));
        waitChangePhase();
    }

    private void chooseStartingFace() {
        System.out.println(CardView.showAsciiArt(this.starts));
        System.out.print("Choose your starting card face (back, front): ");
        String choose = in.nextLine();
        while (!choose.matches("^(front|back)$")) {
            System.out.print("Invalid choice (back, front)\nRetry: ");
            choose = in.nextLine();
        }
        choose = choose.equals("back") ? "0" : "1";
        scanInput(choose);
        this.starts = null;
        waitChangePhase();
    }

    private void waitPlayers(){
        this.waitForPlayers.set(true);
        waitChangePhase();
    }

    private void chooseColor() {
        Set<String> copy;
        synchronized(this.colors){
            copy = this.colors.stream().map(Enum::name).collect(Collectors.toSet());
        }
        System.out.print(copy+"\nChoose color: ");
        String color = in.nextLine();
        while (!copy.contains(color.toUpperCase())) {
            System.out.print("Color not available\nRetry: ");
            color = in.nextLine();
        }
        sendUntilConfirm(color,"",(x)->{
            synchronized (this.colors) {
                try{
                    return this.colors.contains(PawnColor.valueOf(x.toUpperCase()));
                }catch(IllegalArgumentException e){
                    return false;
                }
            }
        });
        waitChangePhase();
    }
    
    private void chooseObjective() {
        System.out.println(ObjectiveView.showAsciiArt(this.scrtObjs));
        System.out.print("Choose your objective (1, 2): ");
        String choose = in.nextLine();
        while (!choose.matches("^[12]$")) {
            System.out.print("Invalid choice (1, 2)\nRetry: ");
            choose = in.nextLine();
        }
        choose = choose.equals("1") ? "0" : "1";
        this.modelView.setObj(this.scrtObjs[Integer.parseInt(choose)]);
        scanInput(choose);
        this.scrtObjs = null;
        waitChangePhase();
    }

    private void gameFlow() {
        AtomicBoolean stay = new AtomicBoolean(true);
        boolean inserted = true;
        String copyCurrPlayer=null;
        System.out.println("Game Started");
        synchronized(currPlayerLock){
            while(this.currPlayer == null){
                try {
                    this.currPlayerLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.executorService = Executors.newSingleThreadExecutor();
        executorService.submit(()->{
            while(stay.get()){
                String msg = in.nextLine();
                setCommand(msg);
            }
        });
        while(stay.get()){
            synchronized (this.currPlayerLock) {
                if (!this.currPlayer.equals(copyCurrPlayer)) {
                    char c = inserted ? ' ' : '\n';
                    copyCurrPlayer = this.currPlayer;
                    String showCurrPlayer = this.currPlayer.equals(this.name) ? "Your turn"
                            : "Current player: " + this.currPlayer;
                    System.out.println(c+showCurrPlayer);
                    inserted = true;
                }
            }
            if(inserted) {
                System.out.print("-> ");
            }
            try {
                String[] cmd = getCommand(15000).split(" ");
                handleCommand(cmd);
                inserted = true;
            } catch (TimeoutException e) {
                inserted = false;
            }
            synchronized(this.phaseQueue){
                if(this.phaseQueue.contains(Phase.ENDGAME) || this.phaseQueue.contains(Phase.CRASHED)){
                    stay.set(false);
                    executorService.shutdownNow();
                    inserted = true;
                }
            }
        }
        waitChangePhase();
    }

    private synchronized void handleCommand(String[] cmd){
        switch (cmd[0]) { //TODO complete switch cases
            case "show" -> {
                if(cmd.length < 2){
                    System.out.println("Command incomplete");
                    return;
                }
                switch(cmd[1]){
                    case "hand" -> {
                        synchronized (this.modelView) {
                            System.out.print(CardView.showAsciiArt(this.modelView.getHand()));
                        }
                    }
                    case "objective" -> {
                        synchronized (this.modelView) {
                            System.out.print(ObjectiveView.showAsciiArt(this.modelView.getSecrObj()));
                        }
                    }
                    case "ground" -> {
                        synchronized (this.modelView) {
                            System.out.print(this.modelView.getGround().asciiGround());
                        }
                    }
                    case "card" -> {
                        synchronized (this.modelView) {
                            try {
                                int userIdx;
                                int x,y;
                                if(cmd.length == 5){
                                    userIdx = Integer.parseInt(cmd[2]) - 1;
                                    x = Integer.parseInt(cmd[3]);
                                    y = Integer.parseInt(cmd[4]);
                                } else if(cmd.length == 4){
                                    userIdx = this.turnOrder.indexOf(this.name);
                                    x = Integer.parseInt(cmd[2]);
                                    y = Integer.parseInt(cmd[3]);
                                } else{
                                    System.out.println("Command incorrect");
                                    return;
                                }
                                System.out.println(this.modelView.getBoard(userIdx).showCard(x,y));
                            }catch(Exception e){
                                System.out.println("Not an index");
                            }
                        }
                    }
                    case "board" -> {
                        synchronized(this.modelView){
                            try {
                                if(cmd.length == 2){
                                    System.out.println(this.modelView.getBoard(this.turnOrder.indexOf(this.name)).asciiBoard());
                                }else if(cmd.length == 3){
                                    int idx = Integer.parseInt(cmd[2]) - 1;
                                    System.out.println(this.modelView.getBoard(idx).asciiBoard());
                                }else{
                                    System.out.println("Command incorrect");
                                }
                            }catch(Exception e) {
                                System.out.println("Not an index");
                            }
                        }
                    }
                    case "leaderboard" -> {
                        synchronized (this.leaderboard) {
                            List<String> tmp = this.leaderboard.keySet().stream()
                                .sorted(Comparator.comparingInt(leaderboard::get).reversed())
                                .toList();
                            for (String name : tmp) {
                                System.out.println(
                                        name + " (id: " + (this.turnOrder.indexOf(name) + 1) + "): " + leaderboard.get(name));
                            }
                        }
                    }
                }
            }
            case "flip" -> {
                if(cmd.length != 2){
                    System.out.println("Command incorrect");
                    return;
                }
                try{
                    int idx = Integer.parseInt(cmd[1]) - 1;
                    if(idx>=0 && idx<this.modelView.getHand().size()){
                        CardView view = this.modelView.getHandCard(idx);
                        view.setFace(!view.getFace());
                    }else{
                        System.out.println("Index not valid");
                    }
                }catch(NumberFormatException e){
                    System.out.println("Not a number");
                }
            }
            case "place" -> placeCard();
            case "draw" -> drawCard();
            case "chat" -> {
                if(cmd.length != 2){
                    System.out.println("Command incorrect");
                    return;
                }
                synchronized (this.chats) {
                    if(cmd[1].equals("All")){
                        printChat(this.chats.stream().filter(x -> x.receiver.equals("All")).toList());
                    }else if(!cmd[1].equals(this.name)){
                        String chat = cmd[1];
                        printChat(this.chats.stream().
                                filter(x -> (x.sender.equals(chat) && x.receiver.equals(this.name)) ||
                                        (x.sender.equals(this.name) && x.receiver.equals(chat))).toList());
                    }else{
                        System.out.println("This chat doesn't exist\n");
                    }
                }
            }
            case "message" -> {
                if(cmd.length != 2){
                    System.out.println("Command incorrect");
                    return;
                }
                if(!this.name.equals(cmd[1]) && (cmd[1].equals("All") || this.turnOrder.contains(cmd[1]))){
                    this.client.sendChatMessage(MsgCreator.chatMsg(this.name, cmd[1], getCommand()));
                }else{
                    System.out.println("Receiver not available\n");
                }
            }
            case "help" -> {
                if(cmd.length == 1){
                    printFile("help");
                }else{
                    printHelpCmd(String.join(" ", cmd).split(" ", 2)[1]);
                }
            }
            default -> System.out.println("Command not supported\n");
        }
    }

    private void printFile(String file) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/util/" + file + ".txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            //TODO log file
            e.printStackTrace();
        }
    }

    private void printHelpCmd(String cmd) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/util/help.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            String line;
            do{
                line = bufferedReader.readLine();
            }
            while (line != null && !line.matches("^<" + cmd + ".*"));
            if(line == null){
                System.out.println("Command unknown");
                return;
            }
            do{
                System.out.println(line);
                line = bufferedReader.readLine();
            }while(line != null && (!(line).contains("<") || line.matches("^<" + cmd + ".*")));
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            //TODO log file
            e.printStackTrace();
        }
    }

    private void showWinner() {
        synchronized(this.positions){
            while(this.positions.isEmpty()){
                try{
                    this.positions.wait();
                }catch(InterruptedException e){
                    throw new RuntimeException();
                }
            }
        }
        
        if (this.positions.get(this.name) == 1) {
            System.out.println("WINNER!");
        }
        List<String> names = this.positions.keySet().stream().sorted(Comparator.comparingInt(
                this.positions::get)).toList();
        for (String n : names) {
            System.out.println(this.positions.get(n) + " " + n + ": " + this.leaderboard.get(n) + " points");
        }

    
        System.out.println("Press enter to continue");
        in.nextLine();
        waitChangePhase();
    }

    private void placeCard(){
        int idx,x,y;
        System.out.print("IDX: ");
        try {
            idx = Integer.parseInt(getCommand()) - 1;
            if (idx < 0 || idx >= this.modelView.getHandSize()) {
                System.out.println("Invalid Card");
                return;
            }
            this.tmpIdx = idx;
        } catch (NumberFormatException e) {
            System.out.println("Not a number");
            return;
        }
        try {
            System.out.print("X,Y: ");
            String coordinates = getCommand();
            String regex = "(-?\\d+),(-?\\d+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher match = pattern.matcher(coordinates);
            if(!match.matches()){
                throw new NumberFormatException();
            }
            x = Integer.parseInt(match.group(1));
            y = Integer.parseInt(match.group(2));
            synchronized(this.confirmValue) {
                this.client.placeCard(idx, x, y,this.modelView.getHandCard(idx).getFace());
                waitConfirm();
                System.out.println(this.confirmMsg);
                if(this.confirmValue.get()){
                    this.idx = this.tmpIdx;
                    this.modelView.removeHandCard(idx);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid coordinates");
        }
    }

    private void drawCard(){
        System.out.print("IDX: ");
        String index = getCommand();
        if(!index.matches("^[1-6]$")){
            System.out.println("Index not valid");
            return;
        }
        int idx = Integer.parseInt(index) - 1;
        synchronized(this.confirmValue) {
            client.drawCard(idx);
            this.waitConfirm();
            System.out.println(this.confirmMsg);
        }
    }

    private void sendUntilConfirm(String input, String constraint, Predicate<String> t) {
        String constraintMsg = constraint.isEmpty() ? constraint : "(" + constraint + ")";
        synchronized (this.confirmValue) {
            scanInput(input);
            waitConfirm();
            while (!this.confirmValue.get()) {
                System.out.println(this.confirmMsg);
                do {
                    System.out.print("Retry " + constraintMsg + ": ");
                    input = in.nextLine();
                } while (!t.test(input));
                scanInput(input);
                waitConfirm();
            }
        }
    }

    @Override
    public void changePhase(Phase phase) {
        if(this.waitForPlayers.get()){
            this.waitForPlayers.set(false);
            this.playersInLobby.clear();
        }
        switch(phase){
            case Phase.MENU -> showMenu();
            case Phase.CREATE -> create();
            case Phase.JOIN -> join();
            case Phase.NAME -> chooseName();
            case Phase.STARTCARD -> chooseStartingFace();
            case Phase.WAITFORPLAYERS -> waitPlayers();
            case Phase.COLOR -> chooseColor();
            case Phase.OBJECTIVE -> chooseObjective();
            case Phase.GAME -> gameFlow();
            case Phase.ENDGAME -> showWinner();
            case Phase.CRASHED -> {
                System.out.print("A player has crashed\nPress enter and return to menu");
                if(executorService == null) {
                    in.nextLine();
                }else{
                    getCommand();
                    executorService.shutdownNow();
                }
                super.cleanView();
                waitChangePhase();
            }
            case Phase.EXIT -> System.out.println("Uscito gioco\nGame Terminato");
        }
    }

    @Override
    public void scanInput(String toScan){
        synchronized(this.phaseQueue){
            if(this.phaseQueue.peek() == Phase.CRASHED) {
                return;
            }
        }
        super.scanInput(toScan);
    }

    private void printChat(List<Message> msgs) {
        for(Message msg : msgs){
            System.out.println(msg.sender + ": " + msg.msg);
        }
    }

    @Override
    public void cleanView(){
        super.cleanView();
        this.currPlayer = null;
        this.capacity = 0;
    }
}
