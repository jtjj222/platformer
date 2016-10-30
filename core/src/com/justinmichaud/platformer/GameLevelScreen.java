package com.justinmichaud.platformer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.justinmichaud.platformer.components.PlayerComponent;
import com.justinmichaud.platformer.components.TextureComponent;
import com.justinmichaud.platformer.components.TransformComponent;
import com.justinmichaud.platformer.systems.InputSystem;
import com.justinmichaud.platformer.systems.RenderingSystem;

public class GameLevelScreen extends ScreenAdapter {

    private final Game game;
    private final Engine engine;

    public GameLevelScreen(Game game) {
        this.game = game;
        engine = new Engine();

        engine.addSystem(new RenderingSystem(game.spriteBatch));
        engine.addSystem(new InputSystem());

        buildPlayer();
    }

    private void buildPlayer() {
        Entity e = new Entity();
        e.add(new PlayerComponent());
        e.add(new TransformComponent());
        e.add(new TextureComponent(new TextureRegion(game.getOrLoadTexture("badlogic.jpg"))));
        engine.addEntity(e);
    }

    @Override
    public void render (float delta) {
        engine.update(delta);
    }

}
