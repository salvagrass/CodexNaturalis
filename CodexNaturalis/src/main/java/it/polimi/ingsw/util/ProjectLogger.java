package it.polimi.ingsw.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Class used to log exceptions on a log file.
 */
public class ProjectLogger {
    private final Logger logger;
    private static final Object lock = new Object();

    public ProjectLogger(String name) {
        this.logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
    }

    /**
     * Record the exception event writing the message with the level importance in the path.
     *
     * @param path Log file's path.
     * @param level Importance of the exception.
     * @param message Printable comment.
     * @param throwable The thrown exception.
     */
    public void log(String path,Level level, String message, Throwable throwable) {
        synchronized (lock) {
            FileHandler fh = setFileHandler(path);
            logger.log(level, message, throwable);
            fh.close();
        }
    }

    /**
     * Creates a file handler of the log file your going to write on.
     *
     * @param path The path of the file.
     * @return A file handler of the log file.
     */
    private FileHandler setFileHandler(String path) {

        try{
            Path filePath = Paths.get(path);
            Path dirPath = filePath.getParent();

            if (dirPath != null && !Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            FileHandler fh = new FileHandler(path,true);
            fh.setLevel(Level.ALL);
            this.logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            return fh;
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Clear the log file specified.
     *
     * @param path Log file's path.
     */
    public void clearFile(String path){
        try{
            Path filePath = Paths.get(path);
            Path dirPath = filePath.getParent();

            if (dirPath != null && !Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }else{
                FileWriter fw = new FileWriter(path);
                fw.close();
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
