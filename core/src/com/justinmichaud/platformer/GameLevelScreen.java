package com.justinmichaud.platformer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.justinmichaud.platformer.components.PhysicsComponent;
import com.justinmichaud.platformer.components.PlayerComponent;
import com.justinmichaud.platformer.components.TextureComponent;
import com.justinmichaud.platformer.components.TransformComponent;
import com.justinmichaud.platformer.systems.CameraControlSystem;
import com.justinmichaud.platformer.systems.InputSystem;
import com.justinmichaud.platformer.systems.PhysicsSystem;
import com.justinmichaud.platformer.systems.RenderingSystem;

public class GameLevelScreen extends ScreenAdapter {

    private final Game game;
    private final Engine engine;

    public GameLevelScreen(Game game) {
        this.game = game;
        engine = new Engine();

        engine.addSystem(new InputSystem());
        engine.addSystem(new CameraControlSystem());
        engine.addSystem(new PhysicsSystem());
        engine.addSystem(new RenderingSystem(game.spriteBatch));

        buildPlayer();
        buildGround(0,-4.5f, 20f,0.5f);
        buildGround(5,-2.5f, 2,1);
    }

    private void buildPlayer() {
        Entity e = new Entity();
        e.add(new PlayerComponent());
        e.add(new TransformComponent(1, 1.5f));
        addPhysicsBox(e, 0, 0, BodyDef.BodyType.DynamicBody);
        e.getComponent(PhysicsComponent.class).body.setBullet(true);
        e.getComponent(PhysicsComponent.class).body.setFixedRotation(true);
        e.add(new TextureComponent(new TextureRegion(game.getOrLoadTexture("badlogic.jpg"))));
        engine.addEntity(e);
    }

    private void addPhysicsBox(Entity e, float x, float y, BodyDef.BodyType type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(x,y);
        Body body = engine.getSystem(PhysicsSystem.class).getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(e.getComponent(TransformComponent.class).width/2f,
                e.getComponent(TransformComponent.class).height/2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction=0.1f;

        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
        e.add(new PhysicsComponent(body));
    }

    private void buildGround(float x, float y, float width, float height) {
        Entity e = new Entity();
        e.add(new TransformComponent(width, height));
        addPhysicsBox(e, x, y, BodyDef.BodyType.StaticBody);
        e.add(new TextureComponent(new TextureRegion(game.getOrLoadTexture("badlogic.jpg"))));
        engine.addEntity(e);
    }

    @Override
    public void render (float delta) {
        engine.update(delta);
    }

}
