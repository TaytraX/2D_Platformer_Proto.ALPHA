package engine.maps;

import engine.AABB;
import engine.LevelManager;
import engine.Portal;
import org.joml.Vector2f;
import java.util.List;

public record Level_2() {
    public LevelManager load() {
        return new LevelManager(
                List.of(
                    new AABB(new Vector2f(-30.0f, 0.0f), new Vector2f(7.0f, 1.0f))
                ),
            List.of(
                new Portal(new Vector2f(-26.0f, 1.0f), new Vector2f(0.9f, 1.7f))
            )
        );
    }
}