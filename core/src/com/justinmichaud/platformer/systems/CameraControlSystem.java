package com.justinmichaud.platformer.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.justinmichaud.platformer.components.PlayerComponent;
import com.justinmichaud.platformer.components.TransformComponent;

public class CameraControlSystem extends IteratingSystem {

    private final ComponentMapper<TransformComponent> transform
            = ComponentMapper.getFor(TransformComponent.class);

    public CameraControlSystem() {
        super(Family.all(PlayerComponent.class, TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {
        OrthographicCamera camera = getEngine().getSystem(RenderingSystem.class).getCamera();
        camera.position.set(transform.get(player).position);
        camera.position.y += RenderingSystem.getScreenSizeInMeters().y/2f*camera.zoom;
        camera.position.y -= transform.get(player).height/2f;
        camera.position.y -= 1;
    }
}
