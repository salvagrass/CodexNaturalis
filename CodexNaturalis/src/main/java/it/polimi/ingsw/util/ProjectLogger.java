package it.polimi.ingsw.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ProjectLogger {
    private final Logger logger;
    private static final Object lock = new Object();

    public ProjectLogger(String name) {

        this.logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
    }

    public void log(String path,Level level, String message) {
        synchronized (lock) {
            FileHandler fh = setFileHandler(path);
            logger.log(level, message);
            fh.close();
        }
    }

    public void log(String path,Level level, String message, Throwable throwable) {
        synchronized (lock) {
            FileHandler fh = setFileHandler(path);
            logger.log(level, message, throwable);
            fh.close();
        }
    }

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
}
