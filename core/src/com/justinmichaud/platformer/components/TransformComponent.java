package com.justinmichaud.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
	public final Vector3 position = new Vector3();
	public final float width;
	public final float height;
	public float rotation = 0.0f;
	public boolean isHidden = false;

	public TransformComponent(float width, float height) {
		this.width = width;
		this.height = height;
	}
}