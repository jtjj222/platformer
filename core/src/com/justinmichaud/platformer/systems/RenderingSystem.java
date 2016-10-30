package com.justinmichaud.platformer.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.justinmichaud.platformer.components.TextureComponent;
import com.justinmichaud.platformer.components.TransformComponent;

// From
// https://github.com/RoaringCatGames/libgdx-ashley-box2d-example/blob/master/core/src/com/roaringcatgames/testgame/systems/RenderingSystem.java
public class RenderingSystem extends SortedIteratingSystem {

    static final float PPM = 16.0f;
    static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth()/PPM;//37.5f;
    static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight()/PPM;//.0f;

    public static final float PIXELS_TO_METRES = 1.0f / PPM;

    private static Vector2 meterDimensions = new Vector2();

    public static Vector2 getScreenSizeInMeters(){
        meterDimensions.set(Gdx.graphics.getWidth()*PIXELS_TO_METRES,
                Gdx.graphics.getHeight()*PIXELS_TO_METRES);
        return meterDimensions;
    }

    private SpriteBatch batch;
    private OrthographicCamera cam;
    private final Box2DDebugRenderer debugRenderer;


    private ComponentMapper<TextureComponent> textureM;
    private ComponentMapper<TransformComponent> transformM;

    public RenderingSystem(SpriteBatch batch) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        textureM = ComponentMapper.getFor(TextureComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);

        this.batch = batch;

        cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        cam.zoom = 1f/3;
        cam.update();

        if (getEngine().getSystem(InputSystem.class).isDebug()) {
            debugRenderer.render(getEngine().getSystem(PhysicsSystem.class).getWorld(),
                    cam.combined);
            return;
        }

        batch.setProjectionMatrix(cam.combined);
        batch.enableBlending();
        batch.begin();

        for (Entity entity : getEntities()) {
            TextureComponent tex = textureM.get(entity);
            TransformComponent t = transformM.get(entity);

            if (tex.region == null || t.isHidden) {
                continue;
            }
            batch.draw(tex.region,
                    t.position.x - t.width/2f, t.position.y - t.height/2f,
                    t.width, t.height);
        }
        batch.end();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {

    }

    public OrthographicCamera getCamera() {
        return cam;
    }
}