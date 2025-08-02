package engine.maps;

import engine.AABB;
import org.joml.Vector2f;

import java.util.List;

public class Level_1 {
    public List<AABB> LoadMap() {
        List<AABB> platform = new java.util.ArrayList<>();

        platform.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));
        platform.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));
        platform.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));
        platform.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));
        platform.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));

        return platform;
    }
}