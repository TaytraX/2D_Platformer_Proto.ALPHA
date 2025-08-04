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
                    new AABB(new Vector2f(-30.0f, -70.0f), new Vector2f(75.0f, 50f)),
                    new AABB(new Vector2f(-65.0f, 0.0f), new Vector2f(20.0f, 80.0f)),
                    new AABB(new Vector2f(45.0f, -20.0f), new Vector2f(2.0f, 18.0f)),
                    new AABB(new Vector2f(45.0f, 50.0f), new Vector2f(2.0f, 50.0f)),
                    new AABB(new Vector2f(42.0f, -12.0f), new Vector2f(1.0f, 0.8f)),
                    new AABB(new Vector2f(26.0f, -3.0f), new Vector2f(4.0f, 0.5f))
                ),
            List.of(
                new Portal(new Vector2f(-26.0f, 1.0f), new Vector2f(0.9f, 1.7f))
            )
        );
    }
}