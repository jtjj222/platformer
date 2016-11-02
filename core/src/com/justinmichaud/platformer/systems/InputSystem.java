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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.justinmichaud.platformer.components.PhysicsComponent;
import com.justinmichaud.platformer.components.PlayerComponent;

public class InputSystem extends IteratingSystem {

    private static final int AXIS_X = 0, AXIS_Y=1, BUTTON_RUN=3, BUTTON_JUMP=1,
            BUTTON_DEBUG=5;

    final static float MAX_VELOCITY = 7f;

    private final ComponentMapper<PhysicsComponent> physics;
    private final ComponentMapper<PlayerComponent> playerComponent;

    private boolean debug = false;
    private float desiredAngle = 0;

    private long jumpStarted = 0;

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

        boolean run = controller.getButton(BUTTON_RUN);
        boolean jump = controller.getButton(BUTTON_JUMP);
        debug = controller.getButton(BUTTON_DEBUG);

        Body body = physics.get(player).body;
        PlayerComponent fixtures = playerComponent.get(player);

        boolean onGround = isPlayerGrounded(body, fixtures.groundSensor);
        if (onGround && jumpStarted == 0) {
            jumpStarted = System.nanoTime();
        }
        if (!onGround) {
            if (System.nanoTime() - jumpStarted < 0.5f * 1E9)
                onGround = true;
            else
                jumpStarted = 0;
        }

        // Max x velocity clamp
        if(Math.abs(body.getLinearVelocity().x) > MAX_VELOCITY) {
            body.setLinearVelocity(Math.signum(body.getLinearVelocity().x) * MAX_VELOCITY,
                    body.getLinearVelocity().y);
        }

        // Apply forces
        if ((int) controller.getAxis(AXIS_X) < 0 && body.getLinearVelocity().x > -MAX_VELOCITY) {
            body.applyLinearImpulse(-2f*(run?1.5f:1f), 0,
                    body.getLocalCenter().x, body.getLocalCenter().y, true);
        }
        else if ((int) controller.getAxis(AXIS_X) > 0 && body.getLinearVelocity().x < MAX_VELOCITY) {
            body.applyLinearImpulse(2f*(run?1.5f:1f), 0,
                    body.getLocalCenter().x, body.getLocalCenter().y, true);
        }
        else {
            // Dampen motion
            body.setLinearVelocity(body.getLinearVelocity().scl(0.9f));
        }

        if (jump && onGround) {
            body.applyLinearImpulse(0, 10,
                    body.getLocalCenter().x, body.getLocalCenter().y, true);
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
