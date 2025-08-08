package engine.maps;

import engine.AABB;
import engine.LevelManager;
import engine.Portal;
import org.joml.Vector2f;
import java.util.List;

public record Level_5() {
    public LevelManager load() {
        return new LevelManager(
                List.of(
                        new AABB(new Vector2f(-50.0f, -50.0f), new Vector2f(70.0f, 50f)),
                        new AABB(new Vector2f(-70.0f, 40.0f), new Vector2f(30.0f, 60.0f)),
                        new AABB(new Vector2f(40.0f, -60.0f), new Vector2f(10.0f, 20.0f)),
                        new AABB(new Vector2f(30.0f, -10.0f), new Vector2f(20.0f, 10.0f)),
                        new AABB(new Vector2f(128.0f, -50.0f), new Vector2f(70.0f, 50f))
                ),
                List.of(
                        new Portal(new Vector2f(40.0f, -38.5f), new Vector2f(1.1f, 1.8f))
                )
        );
    }
}