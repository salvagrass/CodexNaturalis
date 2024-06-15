package it.polimi.ingsw;

import it.polimi.ingsw.view.UserView;
import it.polimi.ingsw.view.CLI;

import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) throws Exception{
        boolean stay;
        UserView view = new CLI();
        Scanner scanner = new Scanner(System.in);
        String connection;
        do{
            stay = false;
            do{
                System.out.println("1 Socket\n2 RMI\nSelect connection type: ");
                connection = scanner.nextLine();
            }while(!connection.equals("1") && !connection.equals("2"));
            view.connect(connection.equals("1"));
            try{
                view.tryConnection("localhost");
            }catch(RuntimeException e){
                System.out.println("can't connect\ntry again...\n\n");
                stay = true;
            }
        }while(stay);
    }
}
