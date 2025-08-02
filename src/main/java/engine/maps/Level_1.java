package engine.maps;

import engine.AABB;
import engine.Portal;
import kotlin.Pair;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;

public class Level_1 {
    public List<Map<AABB, Portal>> LoadMap() {
        List<Map<AABB, Portal>> Level = new java.util.ArrayList<>();

        Level.add(new AABB(new Vector2f(-30.0f, 0.0f), new Vector2f(7.0f, 1.0f)));
        Level.add(new AABB(new Vector2f(0.0f, 5.0f), new Vector2f(5.0f, 1.0f)));
        Level.add(new AABB(new Vector2f(13.0f, -20.0f), new Vector2f(10.0f, 1.0f)));
        Level.add(new AABB(new Vector2f(50.0f, -12.0f), new Vector2f(10.0f, 1.0f)));
        Level.add(new AABB(new Vector2f(90.0f, -8.0f), new Vector2f(6.0f, 1.0f)));
        Level.add(new AABB(new Vector2f(103.0f, 0.0f), new Vector2f(5.0f, 1.0f)));

        Level.add(new Portal(new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 1.0f)));

        return Level;
    }
}