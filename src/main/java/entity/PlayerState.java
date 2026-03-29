package entity;

import engine.AABB;
import org.joml.Vector2f;

public class PlayerState {
    public Vector2f position;
    public Vector2f velocity;
    public Vector2f jumpVelocity;
    public boolean isGrounded;
    public boolean wasGrounded; // pour capturer la vélocité d'impact
    public AnimationState animationState;
    public boolean facingRight;
    public boolean moveLeft;
    public boolean moveRight;
    public boolean jump;
    public float force;
    public boolean blockedHorizontally;
    public long timestamp;
    public AABB aabb;


    public PlayerState(
            Vector2f position,
            Vector2f velocity,
            Vector2f jumpVelocity,
            boolean isGrounded,
            AnimationState animationState,
            boolean facingRight,
            boolean moveLeft,
            boolean moveRight,
            boolean jump,
            float force,
            boolean blockedHorizontally,
            long timestamp
    ) {
        this.position = position;
        this.velocity = velocity;
        this.jumpVelocity = jumpVelocity;
        this.isGrounded = isGrounded;
        this.animationState = animationState;
        this.facingRight = facingRight;
        this.moveLeft = moveLeft;
        this.moveRight = moveRight;
        this.jump = jump;
        this.force = force;
        this.blockedHorizontally = blockedHorizontally;
        this.timestamp = timestamp;
        this.aabb = new AABB(position, PLAYER_SIZE);
    }

    public static final Vector2f PLAYER_SIZE = new Vector2f(0.4f, 0.6f);
}