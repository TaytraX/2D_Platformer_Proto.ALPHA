package engine;

import engine.maps.*;
import entity.PlayerState;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadManager {

    public static final AtomicReference<PlayerState> playerState  = new AtomicReference<>();

    public static final BlockingDeque<MapLoadRequest> MapLoadQueue = new LinkedBlockingDeque<>();

    public static ExecutorService loadMapExecutor;

    public static void initializer() {
        loadMapExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "GroundGenThread");
            thread.setDaemon(true);
            return thread;
        });

        loadMapExecutor.submit(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    MapLoadRequest request = MapLoadQueue.take();

                    switch(request.level()) {
                        case 2 -> new Level_2().LoadMap();
                        case 3 -> new Level_3().LoadMap();
                        case 4 -> new Level_4().LoadMap();
                        case 5 -> new Level_5().LoadMap();
                        default -> new Level_1().LoadMap();
                    };

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