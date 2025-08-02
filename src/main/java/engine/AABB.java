package engine;

import org.joml.Vector2f;

public record AABB(Vector2f position, Vector2f halfSize) {

    public boolean collidesWith(AABB other) {
        return getMaxX() >= other.getMinX() &&
               getMinX() <= other.getMaxX() &&
               getMaxY() >= other.getMinY() &&
               getMinY() <= other.getMaxY();
    }

    // Calcule la profondeur de pénétration sur l'axe X
    public float getOverlapX(AABB other) {
        float leftOverlap = getMaxX() - other.getMinX();
        float rightOverlap = other.getMaxX() - getMinX();
        return Math.min(leftOverlap, rightOverlap);
    }

    // Calcule la profondeur de pénétration sur l'axe Y
    public float getOverlapY(AABB other) {
        float topOverlap = getMaxY() - other.getMinY();
        float bottomOverlap = other.getMaxY() - getMinY();
        return Math.min(topOverlap, bottomOverlap);
    }

    public float getMinX() { return position.x - halfSize.x; }
    public float getMaxX() { return position.x + halfSize.x; }
    public float getMinY() { return position.y - halfSize.y; }
    public float getMaxY() { return position.y + halfSize.y; }

}