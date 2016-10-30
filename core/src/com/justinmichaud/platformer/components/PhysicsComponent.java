package com.justinmichaud.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsComponent implements Component {

    public Body body;

    public PhysicsComponent(Body body) {
        this.body = body;
    }
}
