package engine;

import engine.maps.*;
import entity.PlayerState;

import static engine.Engine.platforms;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadManager {

    public static final AtomicReference<PlayerState> playerState  = new AtomicReference<>();

    public static final BlockingDeque<MapLoadRequest> MapLoadQueue = new LinkedBlockingDeque<>();

    public static ExecutorService loadMapExecutor;

    public static void initializer() {
        loadMapExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "MapLoadThread");
            thread.setDaemon(true);
            return thread;
        });

        loadMapExecutor.submit(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    MapLoadRequest request = MapLoadQueue.take();

                    LevelManager generatedLevel = switch(request.level()) {
                        case 2 -> new Level_2().load();
                        case 3 -> new Level_3().load();
                        case 4 -> new Level_4().load();
                        case 5 -> new Level_5().load();
                        default -> new Level_1().load();
                    };

                    platforms.put(request.level(), generatedLevel);
                    System.out.println("Level " + request.level() + " chargé");

                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public static void shutdown() {
        if (loadMapExecutor != null) {
            loadMapExecutor.shutdown();
        }
    }

}