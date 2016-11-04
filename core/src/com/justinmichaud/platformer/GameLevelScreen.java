package com.justinmichaud.platformer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
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

        buildPlayer(0,0);
        buildGround(0,-4.5f, 20f,0.5f);
        buildGround(5,-2.5f, 1,1);
        buildGround(-3,-1.5f, 1,1);
        buildGround(-5,0, 1,1);
    }

    private void buildPlayer(int x, int y) {
        Entity e = new Entity();
        e.add(new TransformComponent(1, 1.5f));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);
        Body body = engine.getSystem(PhysicsSystem.class).getWorld().createBody(bodyDef);

        float width = e.getComponent(TransformComponent.class).width;
        float height = e.getComponent(TransformComponent.class).height;
        float frictionStripHeight = height/10f;

        Fixture groundSensor, frictionFixture;

        {
            PolygonShape shape = new PolygonShape();

            shape.setAsBox(width/2f, (height-frictionStripHeight)/2f,
                    new Vector2(0, frictionStripHeight/2f), 0);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1f;
            fixtureDef.friction=0;
            fixtureDef.restitution = 0;

            body.createFixture(fixtureDef);
            shape.dispose();
        }
        {
            PolygonShape shape = new PolygonShape();

            shape.setAsBox(width/2f, frictionStripHeight/2f,
                    new Vector2(0,-(height-frictionStripHeight)/2f), 0);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1f;
            fixtureDef.friction=0.5f;
            fixtureDef.restitution = 0;

            frictionFixture = body.createFixture(fixtureDef);
            shape.dispose();
        }
        {
            CircleShape shape = new CircleShape();
            shape.setRadius(width/2f);
            shape.setPosition(new Vector2(0,-height/2.5f));

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1f;
            fixtureDef.isSensor = true;
            fixtureDef.friction=0.1f;
            fixtureDef.restitution = 0;

            groundSensor = body.createFixture(fixtureDef);
            shape.dispose();
        }

        body.setBullet(true);
        body.setFixedRotation(true);

        e.add(new PhysicsComponent(body));
        e.add(new PlayerComponent(groundSensor, frictionFixture));
        e.add(new TextureComponent(new TextureRegion(game.getOrLoadTexture("badlogic.jpg"))));
        engine.addEntity(e);
    }

    private void addStaticBox(Entity e, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x,y);
        Body body = engine.getSystem(PhysicsSystem.class).getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(e.getComponent(TransformComponent.class).width/2f,
                e.getComponent(TransformComponent.class).height/2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction=0.1f;
        fixtureDef.restitution = 0;

        body.createFixture(fixtureDef);
        shape.dispose();
        e.add(new PhysicsComponent(body));
    }

    private void buildGround(float x, float y, float width, float height) {
        Entity e = new Entity();
        e.add(new TransformComponent(width, height));
        addStaticBox(e, x, y);
        e.add(new TextureComponent(new TextureRegion(game.getOrLoadTexture("badlogic.jpg"))));
        engine.addEntity(e);
    }

    @Override
    public void render (float delta) {
        engine.update(delta);
    }

}
