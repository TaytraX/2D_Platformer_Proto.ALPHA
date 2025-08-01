package engine;

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