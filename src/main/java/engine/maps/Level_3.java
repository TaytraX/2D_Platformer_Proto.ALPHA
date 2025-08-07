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
                        //spawn platforms
                        new AABB(new Vector2f(-30.0f, 50.0f), new Vector2f(5.0f, 0.6f)),

                            // stair platforms
                            new AABB(new Vector2f(-42.0f, 38.0f), new Vector2f(2.0f, 0.2f)),
                            new AABB(new Vector2f(-28.0f, 26.0f), new Vector2f(2.0f, 0.2f)),
                            new AABB(new Vector2f(-42.0f, 14.0f), new Vector2f(2.0f, 0.2f)),
                            new AABB(new Vector2f(-28.0f, 2.0f), new Vector2f(2.0f, 0.2f)),

                                new AABB(new Vector2f(-62.0f, 42.0f), new Vector2f(10.0f, 36.0f)),
                                new AABB(new Vector2f(-142.0f, -34.0f), new Vector2f(90.0f, 36.0f)),
                                new AABB(new Vector2f(-200.0f, 42.0f), new Vector2f(40.0f, 90.0f)),

                                    new AABB(new Vector2f(-144.0f, 6.0f), new Vector2f(16.0f, 4.5f)),

                                        new AABB(new Vector2f(-100.0f, 18.0f), new Vector2f(5.0f, 0.5f)),
                                        new AABB(new Vector2f(-110.0f, 26.0f), new Vector2f(2.6f, 0.2f)),
                                        new AABB(new Vector2f(-90.0f, 34.0f), new Vector2f(3.0f, 0.2f)),
                                        new AABB(new Vector2f(-118.0f, 42.0f), new Vector2f(2.6f, 0.2f)),
                                        new AABB(new Vector2f(-94.0f, 50.0f), new Vector2f(3.0f, 0.5f)),
                                        new AABB(new Vector2f(-73.5f, 58.0f), new Vector2f(1.7f, 1.0f)),
                                        new AABB(new Vector2f(-97.0f, 66.0f), new Vector2f(5.0f, 0.5f)),
                                        new AABB(new Vector2f(-90.0f, 74.0f), new Vector2f(2.6f, 0.2f))
                ),
                List.of(
                        new Portal(new Vector2f(-20.0f, 86.0f), new Vector2f(1.0f, 1.5f))
                )
        );
    }
}