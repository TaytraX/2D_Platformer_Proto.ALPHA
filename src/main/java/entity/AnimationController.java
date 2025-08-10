package entity;

import java.util.EnumMap;
import java.util.Map;

public class AnimationController {

    private final Map<AnimationState, Long> animationStartTimes = new EnumMap<>(AnimationState.class);

    public AnimationState processTransiteAnimation(PlayerState currentState, AnimationState previousAnimation) {

        // Vérifier d'abord si l'animation précédente doit continuer (durée fixe)
        if (previousAnimation.hasFixedDuration()) {
            if (shouldContinueFixedAnimation(previousAnimation)) {
                return previousAnimation;
            }
        }

        // Évaluer les nouvelles animations par priorité
        for (int priority = 1; priority <= 4; priority++) {
            AnimationState newAnimation = checkAnimationsByPriority(currentState, priority);
            if (newAnimation != null) {
                // Démarrer le timing si c'est une animation temporelle
                if (newAnimation.hasFixedDuration() && newAnimation != previousAnimation) {
                    animationStartTimes.put(newAnimation, System.currentTimeMillis());
                }
                return newAnimation;
            }
        }

        return AnimationState.IDLE;
    }

    private boolean shouldContinueFixedAnimation(AnimationState animation) {
        Long startTime = animationStartTimes.get(animation);
        if (startTime == null) return false;

        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed < animation.getDuration();
    }

    private AnimationState checkAnimationsByPriority(PlayerState currentState, int priority) {
        return switch (priority) {
            case 1 -> checkCriticalAnimations(currentState);
            case 2 -> checkPhysicsAnimations(currentState);
            case 3 -> checkMovementAnimations(currentState);
            case 4 -> AnimationState.IDLE;
            default -> null;
        };
    }

    private AnimationState checkCriticalAnimations(PlayerState currentState) {
        // LANDING : vient d'atterrir avec vitesse élevée
        if (currentState.isGrounded() && !currentState.wasGrounded() && Math.abs(currentState.velocity().x()) > 0.2f) {
            return AnimationState.LANDING;
        }
        return null;
    }

    private AnimationState checkPhysicsAnimations(PlayerState currentState) {
        if (!currentState.isGrounded()) {
            if (currentState.velocity().y > 0.5f) {
                if (currentState.moveRight()) return AnimationState.JUMPING_RIGHT;
                if (currentState.moveLeft()) return AnimationState.JUMPING_LEFT;
                return AnimationState.JUMPING;
            }
            if (currentState.velocity().y < -0.5f) return AnimationState.FALLING;
        }
        return null;
    }

    private AnimationState checkMovementAnimations(PlayerState currentState) {
        // SKIDDING : a de l'élan, mais pas d'input
        if (currentState.isGrounded() &&
                Math.abs(currentState.velocity().x) > 0.5f &&
                !currentState.moveLeft() && !currentState.moveRight()) {
            return AnimationState.SKIDDING;
        }

        // Mouvement normal
        if (currentState.jump() && currentState.moveRight()) return AnimationState.JUMPING_RIGHT;
        if (currentState.jump() && currentState.moveLeft()) return AnimationState.JUMPING_LEFT;
        if (currentState.jump()) return AnimationState.JUMPING;

        if (currentState.moveLeft()) return AnimationState.WALKING_LEFT;
        if (currentState.moveRight()) return AnimationState.WALKING_RIGHT;

        return null;
    }
}