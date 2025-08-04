package engine.maps;

import engine.AABB;
import engine.LevelManager;
import engine.Portal;
import org.joml.Vector2f;
import java.util.List;

public record Level_3() {
    public LevelManager load() {
        return new LevelManager(
                List.of(
                        new AABB(new Vector2f(-30.0f, -70.0f), new Vector2f(75.0f, 50f))
                ),
                List.of(
                        new Portal(new Vector2f(108.0f, 1.0f), new Vector2f(1.0f, 1.0f))
                )
        );
    }
}