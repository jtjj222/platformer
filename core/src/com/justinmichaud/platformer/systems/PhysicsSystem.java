package com.justinmichaud.platformer.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.justinmichaud.platformer.components.PhysicsComponent;
import com.justinmichaud.platformer.components.PlayerComponent;
import com.justinmichaud.platformer.components.TransformComponent;

public class PhysicsSystem extends IteratingSystem {

    private World world;
    private ComponentMapper<TransformComponent> transform;
    private ComponentMapper<PhysicsComponent> physics;

    // Used to step the physics system
    // http://saltares.com/blog/games/fixing-your-timestep-in-libgdx-and-box2d/
    private float accumulator;
    private float step = 1.0f / 60.0f;

    public PhysicsSystem() {
        super(Family.all(PhysicsComponent.class, TransformComponent.class).get());

        world = new World(new Vector2(0,0), true);

        transform = ComponentMapper.getFor(TransformComponent.class);
        physics = ComponentMapper.getFor(PhysicsComponent.class);
    }

    @Override
    public void update(float delta) {
        accumulator+=delta;

        while (accumulator >= step) {
            world.step(step, 8, 2);
            accumulator -= step;
            updateTransformationsFromPhysics();
        }

        super.update(delta);
    }

    // Make big changes after the world has been stepped
    private void updateTransformationsFromPhysics() {
        for (Entity e : getEntities()) {
            TransformComponent t = transform.get(e);
            PhysicsComponent p = physics.get(e);

            t.position.set(p.body.getPosition(), 0);
            t.rotation = p.body.getAngle() * MathUtils.radiansToDegrees;
        }
    }

    // Interpolate into next frame
    @Override
    protected void processEntity(Entity e, float delta) {
        TransformComponent t = transform.get(e);
        PhysicsComponent p = physics.get(e);

        float alpha = accumulator / step;

        t.position.x = p.body.getPosition().x * alpha + t.position.x * (1.0f - alpha);
        t.position.y = p.body.getPosition().y * alpha + t.position.y * (1.0f - alpha);
        t.rotation = p.body.getAngle()*MathUtils.radiansToDegrees*alpha
                + t.rotation * (1.0f - alpha);

        t.rotation = p.body.getAngle() * MathUtils.radiansToDegrees;
    }

    public World getWorld() {
        return world;
    }
}
