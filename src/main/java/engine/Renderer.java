package engine;

import entity.Camera;
import render.*;

public class Renderer {

    private final BackgroundRenderer BackgroundRenderer;
    private final RenderMap RenderMap;
    private final RenderPlayer RenderPlayer;

    public Renderer() {
        BackgroundRenderer = new BackgroundRenderer();
        RenderMap = new RenderMap();
        RenderPlayer = new RenderPlayer();
    }

    public void initialize() {
        BackgroundRenderer.initialize();
        RenderMap.initialize();
        RenderPlayer.initialize();
    }

    public void renderFrame(Camera camera, float deltaTime){

        BackgroundRenderer.render(camera, deltaTime);
        RenderMap.render(camera, deltaTime);

        RenderPlayer.render(camera, deltaTime);
    }

    public void cleanUp() {
        BackgroundRenderer.cleanup();
        RenderMap.cleanup();
        RenderPlayer.cleanup();
    }
}
