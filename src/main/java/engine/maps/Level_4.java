package engine.maps;

import engine.AABB;
import engine.LevelManager;
import engine.Portal;
import org.joml.Vector2f;
import java.util.List;

public record Level_4() {
    public LevelManager load() {
        return new LevelManager(
                List.of(
                        new AABB(new Vector2f(0.0f, -70.0f), new Vector2f(100.0f, 50f)),
                        new AABB(new Vector2f(10.f, 20.0f), new Vector2f(20.0f, 50f)),
                        new AABB(new Vector2f(-80.0f, 0.0f), new Vector2f(30.0f, 80.0f)),
                        new AABB(new Vector2f(-30.0f, -20.0f), new Vector2f(2.0f, 5f)),
                        new AABB(new Vector2f(-12.0f, -8.0f), new Vector2f(2.0f, 1f)),
                        new AABB(new Vector2f(-35.0f, 0.0f), new Vector2f(2.0f, 0.5f)),
                        new AABB(new Vector2f(-25.0f, 8.0f), new Vector2f(2.0f, 0.5f)),
                        new AABB(new Vector2f(-35.0f, 16.0f), new Vector2f(2.0f, 0.5f)),
                        new AABB(new Vector2f(-25.0f, 24.0f), new Vector2f(2.0f, 0.5f)),
                        new AABB(new Vector2f(-35.0f, 32.0f), new Vector2f(2.0f, 0.5f)),
                        new AABB(new Vector2f(-25.0f, 40.0f), new Vector2f(2.0f, 0.5f)),
                        new AABB(new Vector2f(-35.0f, 48.0f), new Vector2f(2.0f, 0.5f)),
                        new AABB(new Vector2f(-25.0f, 56.0f), new Vector2f(2.0f, 0.5f)),
                        new AABB(new Vector2f(-35.0f, 64.0f), new Vector2f(2.0f, 0.5f)),
                        new AABB(new Vector2f(-13.0f, 69.0f), new Vector2f(3.0f, 1.0f)),
                        new AABB(new Vector2f(210.0f, -70.0f), new Vector2f(70.0f, 50f)),
                        new AABB(new Vector2f(132.0f, -51.5f), new Vector2f(8.0f, 1f))
                ),
                List.of(
                        new Portal(new Vector2f(125.1f, -49.0f), new Vector2f(1.1f, 1.6f))
                )
        );
    }
}