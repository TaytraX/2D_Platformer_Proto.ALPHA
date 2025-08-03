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
                        new AABB(new Vector2f(-30.0f, 0.0f), new Vector2f(7.0f, 1.0f)),
                        new AABB(new Vector2f(0.0f, 5.0f), new Vector2f(5.0f, 1.0f)),
                        new AABB(new Vector2f(13.0f, -20.0f), new Vector2f(10.0f, 1.0f)),
                        new AABB(new Vector2f(50.0f, -12.0f), new Vector2f(10.0f, 1.0f)),
                        new AABB(new Vector2f(90.0f, -8.0f), new Vector2f(6.0f, 1.0f)),
                        new AABB(new Vector2f(103.0f, 0.0f), new Vector2f(5.0f, 1.0f))
                ),
                List.of(
                        new Portal(new Vector2f(108.0f, 1.0f), new Vector2f(1.0f, 1.0f))
                )
        );
    }
}