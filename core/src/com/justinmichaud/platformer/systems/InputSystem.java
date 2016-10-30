package com.justinmichaud.platformer.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.justinmichaud.platformer.components.PlayerComponent;
import com.justinmichaud.platformer.components.TransformComponent;

public class InputSystem extends IteratingSystem {

    private static final int AXIS_X = 0, AXIS_Y=1, BUTTON_RUN=3, BUTTON_JUMP=1,
            BUTTON_DEBUG=5;

    private Controller controller;
    private ComponentMapper<TransformComponent> transform;

    private boolean debug = false;

    public InputSystem() {
        super(Family.all(PlayerComponent.class, TransformComponent.class).get());
        transform = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {
        if (controller == null) controller = Controllers.getControllers().first();
        if (controller == null) return;

        controller.addListener(new ControllerAdapter() {
            @Override
            public boolean buttonDown(Controller controller, int buttonCode) {
                System.out.println("Button down: " + buttonCode);
                return false;
            }

            @Override
            public boolean buttonUp(Controller controller, int buttonCode) {
                System.out.println("Button up: " + buttonCode);
                return false;
            }

            @Override
            public boolean axisMoved(Controller controller, int axisCode, float value) {
                System.out.println("Axis moved: " + axisCode + " -> " + value);
                return false;
            }
        });

        boolean run = controller.getButton(BUTTON_RUN);
        boolean jump = controller.getButton(BUTTON_JUMP);
        debug = controller.getButton(BUTTON_DEBUG);
        transform.get(player).position.add(
                (int)controller.getAxis(AXIS_X)*(run?2:1),
                -(int)controller.getAxis(AXIS_Y)*(jump?2:1),0);
    }

    public boolean isDebug() {
        return debug;
    }
}
