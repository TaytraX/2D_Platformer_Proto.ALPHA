package engine.maps;

import engine.AABB;
import org.joml.Vector2f;

import java.util.List;

public class Level_1 {
    public static List<AABB> platforms;

    public void LoadMap(List<AABB> Map) {
        platforms.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));
        platforms.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));
        platforms.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));
        platforms.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));
        platforms.add(new AABB(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));;
    }
}