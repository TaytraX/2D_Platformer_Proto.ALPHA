package engine;

import engine.maps.*;

import static engine.Engine.platforms;
import java.util.concurrent.*;

public class ThreadManager {
    public static void loadLevelAsync(int level) {
        CompletableFuture<LevelManager> future = CompletableFuture.supplyAsync(() -> switch(level) {
                case 2 -> new Level_2().load();
                case 3 -> new Level_3().load();
                case 4 -> new Level_4().load();
                case 5 -> new Level_5().load();
                default -> new Level_1().load();
            }
        );

        future.thenAccept(levelManager -> {
            platforms.put(level, levelManager);
            GameLogger.info("Level " + level + " chargé");
        }).exceptionally(throwable -> {
            GameLogger.error("Échec du chargement niveau " + level, throwable);
            return null;
        });
    }
}