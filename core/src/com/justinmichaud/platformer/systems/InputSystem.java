package com.justinmichaud.platformer.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.justinmichaud.platformer.components.PhysicsComponent;
import com.justinmichaud.platformer.components.PlayerComponent;

public class InputSystem extends IteratingSystem {

    private static final int AXIS_X = 0, AXIS_Y=1, BUTTON_RUN=3, BUTTON_JUMP=1,
            BUTTON_DEBUG=5;

    private final ComponentMapper<PhysicsComponent> physics;
    private final ComponentMapper<PlayerComponent> playerComponent;

    private boolean debug = false;
    private float desiredAngle = 0;

    private float jumpBoostFrames = -1;
    private boolean allowJumping = true;

    private final ControllerAdapter debugAdapter = new ControllerAdapter() {
        @Override
        public boolean buttonDown(Controller controller, int buttonCode) {
            if (Gdx.input.isKeyPressed(Input.Keys.D))
                System.out.println("Button down: " + buttonCode);
            return false;
        }

        @Override
        public boolean buttonUp(Controller controller, int buttonCode) {
            if (Gdx.input.isKeyPressed(Input.Keys.D))
                System.out.println("Button up: " + buttonCode);
            return false;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisCode, float value) {
            if (Gdx.input.isKeyPressed(Input.Keys.D))
                System.out.println("Axis moved: " + axisCode + " -> " + value);
            return false;
        }
    };

    public InputSystem() {
        super(Family.all(PlayerComponent.class, PhysicsComponent.class).get());
        physics = ComponentMapper.getFor(PhysicsComponent.class);
        playerComponent = ComponentMapper.getFor(PlayerComponent.class);
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {
        if (Controllers.getControllers().size == 0) return;
        Controller controller = Controllers.getControllers().first();

        if (Gdx.input.isKeyPressed(Input.Keys.D)) controller.addListener(debugAdapter);
        else controller.removeListener(debugAdapter);

        boolean jump = controller.getButton(BUTTON_JUMP);
        debug = controller.getButton(BUTTON_DEBUG);

        Body body = physics.get(player).body;
        PlayerComponent fixtures = playerComponent.get(player);

        boolean onGround = isPlayerGrounded(body, fixtures.groundSensor);
        float maxJumpFrames = 12*0.016f;

        // Allow them to jump again once they return to the ground and stop pressing jump
        if (!jump && onGround && jumpBoostFrames < 0) allowJumping = true;

        if (jump && allowJumping && onGround && jumpBoostFrames < 0) {
            // Start new jump
            body.applyLinearImpulse(0, 10,
                    body.getLocalCenter().x, body.getLocalCenter().y, true);
            jumpBoostFrames = 0;
            allowJumping = false;
        }
        else if (jumpBoostFrames > 0 && jumpBoostFrames < maxJumpFrames && !allowJumping) {
            // Boost existing jump
            if (jump) body.applyLinearImpulse(0, 10 * (1-jumpBoostFrames/maxJumpFrames),
                    body.getLocalCenter().x, body.getLocalCenter().y, true);
            // Otherwise, they are done being boosted
            else jumpBoostFrames = -1;
        }
        if (jumpBoostFrames >= 0) jumpBoostFrames += deltaTime;
        if (jumpBoostFrames > maxJumpFrames) jumpBoostFrames = -1;

        float maxVelocity = 7f * (controller.getButton(BUTTON_RUN)? 1.5f : 1);
        float impulseX = (controller.getButton(BUTTON_RUN)? 1f : 0.75f)
                * (onGround? 1 : 1.5f);

        int xInput = (int) controller.getAxis(AXIS_X);
        float xVel = body.getLinearVelocity().x;
        if (xInput != 0 && Math.signum(xInput) != Math.signum(xVel)) {
            impulseX *= 2; // Make turning around happen faster
        }

        // Apply forces to move player
        if (xInput != 0 && Math.signum(xInput)*body.getLinearVelocity().x < maxVelocity) {
            body.applyLinearImpulse(Math.signum(xInput)*impulseX, 0,
                    body.getLocalCenter().x, body.getLocalCenter().y, true);
        }
        else {
            // Dampen motion
            body.setLinearVelocity(body.getLinearVelocity().scl(0.9f, 1));
        }

        // Gravity
        body.applyLinearImpulse(0, -2.5f,
                body.getLocalCenter().x, body.getLocalCenter().y, true);

        body.setTransform(body.getPosition(), desiredAngle);
    }

    private boolean isPlayerGrounded(Body playerBody, Fixture sensor) {
        Array<Contact> contactList = playerBody.getWorld().getContactList();

        for (Contact c : contactList) {
            if (c.isTouching() && (c.getFixtureA() == sensor
                    || c.getFixtureB() == sensor)) {
                return true;
            }
        }

        return false;
    }

    public boolean isDebug() {
        return debug;
    }
}
