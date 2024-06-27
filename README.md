# Politecnico di Milano - Codex Naturalis AA 2023/2024 

Group Members
- Salvatore Grasso
- Gabriele Ghezzi
- Riccardo Ferri
- Nicolo Greppi

## Implemented Features
* Basic Features:
    - Feature 1: Complete ruleset
    - Feature 2: Client/Server communication using sockets.
    - Feature 3: Client/Server communication using RMI.
    - Feature 4: TUI 
    - Feature 5: GUI
* Advanced Features:
  - **Multiple games**: Allows multiple games to be played at the same time using the same server.
  - **Data Persistence**: Saves the game state and player data, enabling the game to be resumed from the last saved state.
  - **Player Chat**: Provides an in-game chat feature for players to communicate with each other during the game.

## Execution Instructions:
Running from JAR
 * Ensure you have Java installed on your system.
 * To execute the server (On windows):
   - Download the server.jar file from the GitHub repository.
   - Open a terminal and navigate to the directory where the JAR file is located.
   - Execute the following command:
   
         java -jar server.jar
   - Open a second terminal and run the following command:

         ipconfig
   - Take note of the IPV4 shown in the terminal and communicate it to the players.

 * To execute the client(GUI):
   - Download the GUI.jar file from the GitHub repository.
   - Double click on it.
   - Select the connection type and insert the address of the server.

* To execute the client(TUI):
  - Download the TUI.jar file from the GitHub repository.
  - Open a terminal and navigate to the directory where the JAR file is located.
  - Execute the following command:

        java -jar TUI.jar
  - Follow the instructions on video to connect to the server.

### Important Notes:
* For proper functioning in RMI, it may be necessary to disable all network adapters from the control panel except for the current one.
* The project has been tested only on Windows 10 and 11. The TUI.jar and server.jar should work without problems on any other OS with Java 21 installed, while proper functioning of the GUI.jar on any other OS is not guaranteed.
* Every JAR file saves files on the disk for logging purposes. However, we especially recommend putting the server.jar in its own dedicated directory to avoid clutter, as it also stores saved games.
      
