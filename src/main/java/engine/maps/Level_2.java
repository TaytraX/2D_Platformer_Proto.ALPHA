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
                    new AABB(new Vector2f(45.0f, -51.0f), new Vector2f(2.0f, 50.0f)),
                    new AABB(new Vector2f(45.0f, 53.0f), new Vector2f(2.0f, 50.0f)),
                    new AABB(new Vector2f(42.0f, -12.0f), new Vector2f(1.0f, 0.8f)),
                    new AABB(new Vector2f(26.0f, -3.0f), new Vector2f(4.0f, 0.5f)),
                    new AABB(new Vector2f(80.0f, -20.0f), new Vector2f(20.0f, 0.5f)),
                    new AABB(new Vector2f(88.0f, -14.0f), new Vector2f(20.0f, 0.5f)),
                    new AABB(new Vector2f(150.0f, -7.0f), new Vector2f(14.0f, 1.0f)),

                        new AABB(new Vector2f(170.0f, 1.0f), new Vector2f(3.0f, 0.5f)),
                        new AABB(new Vector2f(185.0f, 9.0f), new Vector2f(3.0f, 0.5f)),
                        new AABB(new Vector2f(170.0f, 17.0f), new Vector2f(3.0f, 0.5f)),
                        new AABB(new Vector2f(185.0f, 25.0f), new Vector2f(3.0f, 0.5f)),
                        new AABB(new Vector2f(170.0f, 33.0f), new Vector2f(3.0f, 0.5f)),

                            new AABB(new Vector2f(215.0f, 41.0f), new Vector2f(25.0f, 1.0f))
                ),
            List.of(
                new Portal(new Vector2f(238.6f, 43.5f), new Vector2f(1.4f, 1.8f))
            )
        );
    }
}