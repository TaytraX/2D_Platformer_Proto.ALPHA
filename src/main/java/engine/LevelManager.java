package engine;

import java.util.List;

public record LevelManager(List<AABB> platforms, List<Portal> portals) {}