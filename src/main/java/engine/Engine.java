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

import static engine.ThreadManager.playerState;
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
    public int level = 4;
    public static ConcurrentHashMap<Integer, LevelManager> platforms = new ConcurrentHashMap<>();

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
            PlayerState initialState = createSpawnState();
            playerState.set(initialState);
            GameLogger.info("PlayerState initialisé");

            loadInitialMap();

            // Vérification que l'état est bien défini
            PlayerState test = playerState.get();
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

            GameLogger.info("Level 1 chargé");

        } catch (Exception e) {
            GameLogger.error("Échec d'initialisation du moteur", e);
            System.exit(1); // Arrêt propre au lieu de crash
        }
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

    private void loadInitialMap() {
        loadLevel(getMapToLoad());
    }

    public void render() {
        try {
            window.clear();
            Renderer.renderFrame(camera, deltaTime);
            window.update();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void update() {
        // 1. Récupérer l'état actuel
        PlayerState currentState = playerState.get();

        // respawn si le joueur est mort
        if (currentState.position().y <= -50.0f) {
            currentState = createSpawnState();
        }

        LevelManager currentLevel = getLevelNearPlayer();

        // 2. Traiter les inputs et mouvements
        PlayerState afterInputs  = player.update(currentState, deltaTime);

        camera.followPlayer(deltaTime);

        // 2. Physics applique velocity à position
        PlayerState afterPhysics = physics.update(afterInputs, getLevelNearPlayer().platforms(), deltaTime);

        if(checkTransition(afterPhysics, currentLevel.portals())) {
            // Créer une nouvelle position de spawn pour le nouveau niveau
            afterPhysics = createSpawnState();
        }

        // 4. Sauvegarder le nouvel état
        playerState.set(afterPhysics);
    }

    public void unloadChunk(int level) {
        if (platforms.remove(level) != null) {
            GameLogger.debug("Level " + level + " déchargé");
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

    private PlayerState createSpawnState() {
        Vector2f spawnPosition = switch(level + 1) {
            case 2 -> new Vector2f(-30, -15);
            case 3 -> new Vector2f(-30, 50);
            case 4 -> new Vector2f(-30.0f, -10.0f);
            case 5 -> new Vector2f(-4, 5);
            default -> new Vector2f(-30, 3);
        };

        return new PlayerState(
                spawnPosition,
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
                false,
                System.currentTimeMillis()
        );
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

    public int getMapToLoad() {

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
            GameLogger.debug("Demande dechargement du Level " + level);
        }
    }

    public LevelManager getLevelNearPlayer() {
        PlayerState state = playerState.get();
        if (state == null) return new LevelManager(new ArrayList<>(), new ArrayList<>());

        LevelManager currentLevel = platforms.get(getMapToLoad());

        return currentLevel != null ? currentLevel : new LevelManager(new ArrayList<>(), new ArrayList<>());
    }

    private boolean checkTransition(PlayerState playerState, List<Portal> portals) {
        AABB playerAABB = playerState.getAABB();

        for (Portal portal : portals) {
            if (portal.transit(playerAABB)) {
                transitionToNextLevel();
                return true;
            }
        }
        return false;
    }

    public void transitionToNextLevel() {
        if (level < 4) { // Maximum niveau 4
            level++;
            GameLogger.info("Transition vers niveau " + level);

            loadLevel(getMapToLoad());
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