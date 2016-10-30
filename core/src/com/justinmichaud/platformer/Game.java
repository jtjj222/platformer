package com.justinmichaud.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game extends com.badlogic.gdx.Game {

	public SpriteBatch spriteBatch;
    public AssetManager assetManager;

    @Override
	public void create () {
        spriteBatch = new SpriteBatch();
        assetManager = new AssetManager();

        setScreen(new GameLevelScreen(this));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (assetManager.update()) super.render();
        else {
            // Show loading screen, since we are sill loading assets
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
	}

    public Texture getOrLoadTexture(String s) {
        if (!assetManager.isLoaded(s))
            assetManager.load(s, Texture.class);

        assetManager.finishLoading();

        return assetManager.get(s);
    }
}
