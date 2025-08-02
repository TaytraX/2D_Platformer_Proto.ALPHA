package engine;

import entity.AnimationState;
import entity.Camera;
import entity.Player;
import entity.PlayerState;
import laucher.Main;
import render.DisplayManager;
import render.Window;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class Engine {
    public Window window;
    public boolean isRunning;
    public Renderer Renderer;
    public Camera camera;
    public Player player;
    private Physics physics;
    private long lastTime = System.currentTimeMillis();
    private float deltaTime = 0.000016f;
    public static int level;
    private static PlayerState initialState;
    public static ConcurrentHashMap<Integer, List<AABB>> platforms = new ConcurrentHashMap<>();

    public void start() {
        init();
        run();
    }

    private void init() {
        GameLogger.info("Initialisation du moteur...");

        try {
            window = Main.getWindow();
            if(window == null) {
                GameLogger.error("Window est null !", null);
                throw new Exception("Window not initialized");
            }
            GameLogger.info("Window OK");


            // Initialiser un PlayerState de base
            initialState = new PlayerState(
                    new Vector2f(-30, 3),
                    new Vector2f(0, 0),
                    new Vector2f(0, 0),
                    false,
                    false,
                    0.0f,
                    AnimationState.IDLE,
                    true,
                    false,
                    false,
                    false,
                    12.0f,
                    System.currentTimeMillis()
            );
            ThreadManager.playerState.set(initialState);
            GameLogger.info("PlayerState initialisé");

            loadInitialMap();

            // Vérification que l'état est bien défini
            PlayerState test = ThreadManager.playerState.get();
            if (test == null) {
                GameLogger.error("PlayerState est null après initialisation !", null);
                throw new Exception("PlayerState initialization failed");
            }

            window.init();
            DisplayManager display = window.getDisplayManager();
            display.setScaleMode(DisplayManager.ScaleMode.STRETCH); // ou autre mode

            Renderer = new Renderer();
            Renderer.initialize();
            ThreadManager.initializer();
            camera = new Camera();
            player = new Player(window.getWindowID());
            physics = new Physics();

            GameLogger.info("Composants créés");

        } catch (Exception e) {
            GameLogger.error("Échec d'initialisation du moteur", e);
            System.exit(1); // Arrêt propre au lieu de crash
        }
    }

    private void loadInitialMap() {
        loadLevel(getMapToLoad());
    }

    public void unloadChunk(int chunkX) {
        if (platforms.remove(chunkX) != null) {
            GameLogger.debug("Chunk " + chunkX + " déchargé");
        }
    }

    // Décharger les chunks lointains pour économiser la mémoire
    public void manageMap() {
        PlayerState playerState = ThreadManager.playerState.get();
        if (playerState == null) return;

        int currentMap = getMapToLoad();

        List<Integer> chunksToUnload = new ArrayList<>();

        for (Integer mapId  : platforms.keySet()) {
            if (mapId != currentMap) {
                chunksToUnload.add(mapId);
            }
        }

        for (Integer chunkX : chunksToUnload) {
            unloadChunk(chunkX);
        }
    }


    public void render() {
        try {
            window.clear();
            Renderer.renderFrame(camera, deltaTime);
            window.update();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void run() {
        isRunning = true;
            while (isRunning && !window.windowShouldClose()) {
                long currentTime = System.currentTimeMillis();
                deltaTime = (currentTime - lastTime) / 1000f;
                lastTime = currentTime;

                deltaTime = Math.min(deltaTime, 0.016f);

                update();
                render();
            }
        cleanup();
    }

    public void update() {
        // 1. Récupérer l'état actuel
        PlayerState currentState = ThreadManager.playerState.get();

        if (currentState.position().y <= -50.0f) {
            currentState = initialState;
        }

        // 2. Traiter les inputs et mouvements
        PlayerState afterInputs  = player.update(currentState, deltaTime);

        camera.followPlayer(deltaTime);
        // 2. Physics applique velocity à position
        PlayerState afterPhysics = physics.update(afterInputs, getLevelNearPlayer(), deltaTime);

        // 4. Sauvegarder le nouvel état
        ThreadManager.playerState.set(afterPhysics);
    }

    // Optionnel : méthode pour changer le mode en jeu
    public void toggleScaleMode() {
        DisplayManager display = window.getDisplayManager();
        DisplayManager.ScaleMode current = display.getScaleMode();

        switch (current) {
            case LETTERBOX -> display.setScaleMode(DisplayManager.ScaleMode.STRETCH);
            case STRETCH -> display.setScaleMode(DisplayManager.ScaleMode.INTEGER);
            case INTEGER -> display.setScaleMode(DisplayManager.ScaleMode.LETTERBOX);
        }
    }

    public static int getMapToLoad() {

        return switch (level) {
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 5;
            default -> 1;
        };

    }

    public void loadLevel(int level) {
        if (!platforms.containsKey(level)) {

            // Demander génération asynchrone
            MapLoadRequest request = new MapLoadRequest(level);
            ThreadManager.MapLoadQueue.offer(request);
            GameLogger.debug("Demande génération chunk " + level);
        }
    }

    public List<AABB> getLevelNearPlayer() {
        PlayerState state = ThreadManager.playerState.get();
        if (state == null) return new ArrayList<>();

        int mapToLoad  = getMapToLoad();

        List<AABB> currentMap  = platforms.get(mapToLoad);
        return currentMap != null ? currentMap : new ArrayList<>();
    }

    public void transitionToNextLevel() {
        if (level < 4) { // Maximum niveau 4 -> map 5
            level++;
            GameLogger.info("Transition vers niveau " + level + " (map " + getMapToLoad() + ")");

            // Charger la nouvelle map
            int newMapId = getMapToLoad();
            loadLevel(newMapId);

            // Nettoyer les anciennes maps
            manageMap();
        } else {
            GameLogger.info("Niveau maximum atteint !");
        }
    }

    public void cleanup() {
        Renderer.cleanUp();
        ThreadManager.shutdown();
        window.cleanup();
        glfwTerminate();
    }
}