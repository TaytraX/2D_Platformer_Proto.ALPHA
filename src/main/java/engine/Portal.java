package engine;

import org.joml.Vector2f;

public record Portal(Vector2f position, Vector2f halfSize) {

    public boolean transit(AABB other) {
        return getMaxX() >= other.getMinX() &&
                getMinX() <= other.getMaxX() &&
                getMaxY() >= other.getMinY() &&
                getMinY() <= other.getMaxY();
    }

    public float getMinX() { return position.x - halfSize.x; }
    public float getMaxX() { return position.x + halfSize.x; }
    public float getMinY() { return position.y - halfSize.y; }
    public float getMaxY() { return position.y + halfSize.y; }

}