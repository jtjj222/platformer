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
import com.justinmichaud.platformer.components.PhysicsComponent;
import com.justinmichaud.platformer.components.PlayerComponent;

public class InputSystem extends IteratingSystem {

    private static final int AXIS_X = 0, AXIS_Y=1, BUTTON_RUN=3, BUTTON_JUMP=1,
            BUTTON_DEBUG=5;

    private Controller controller;
    private ComponentMapper<PhysicsComponent> physics;

    private boolean debug = false;

    private float desiredAngle = 0;

    public InputSystem() {
        super(Family.all(PlayerComponent.class, PhysicsComponent.class).get());
        physics = ComponentMapper.getFor(PhysicsComponent.class);
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {
        if (Controllers.getControllers().size == 0) return;
        controller = Controllers.getControllers().first();

        controller.addListener(new ControllerAdapter() {
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
        });

        boolean run = controller.getButton(BUTTON_RUN);
        boolean jump = controller.getButton(BUTTON_JUMP);
        debug = controller.getButton(BUTTON_DEBUG);

        Body body = physics.get(player).body;

        body.applyLinearImpulse(
                new Vector2((int)controller.getAxis(AXIS_X)*(run?0.5f:0.1f), 0),
                body.getLocalCenter(), true);
        if (jump)
            body.applyLinearImpulse(new Vector2(0, 0.5f), body.getLocalCenter(), true);

        body.setTransform(body.getPosition(), desiredAngle);
    }

    public boolean isDebug() {
        return debug;
    }
}
