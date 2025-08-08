package engine;

import entity.Camera;
import laucher.Main;
import render.*;

public class Renderer {

    private final BackgroundRenderer backgroundRenderer;
    private final RenderMap renderMap;
    private final RenderPlayer renderPlayer;
    private final RenderPortal renderPortal;
    private THE_END theEndScreen;

    public Renderer() {
        backgroundRenderer = new BackgroundRenderer();
        renderMap = new RenderMap();
        renderPlayer = new RenderPlayer();
        renderPortal = new RenderPortal();
    }

    public void initialize() {
        backgroundRenderer.initialize();
        renderMap.initialize();
        renderPlayer.initialize();
        renderPortal.initialize();
        theEndScreen = new THE_END();
    }

    public void renderFrame(Camera camera, float deltaTime){

        if (Main.getEngine().isGameCompleted()) {
            theEndScreen.render(null, deltaTime);
        } else {
            backgroundRenderer.render(camera, deltaTime);
            renderMap.render(camera, deltaTime);
            renderPlayer.render(camera, deltaTime);
            renderPortal.render(camera, deltaTime);

        }
    }

    public void cleanUp() {
        backgroundRenderer.cleanup();
        renderMap.cleanup();
        renderPlayer.cleanup();
    }
}
