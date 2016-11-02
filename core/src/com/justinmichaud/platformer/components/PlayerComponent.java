package com.justinmichaud.platformer.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Fixture;

public class PlayerComponent implements Component{
    public final Fixture groundSensor;
    public final Fixture frictionFixture;

    public PlayerComponent(Fixture groundSensor, Fixture frictionFixture) {
        this.groundSensor = groundSensor;
        this.frictionFixture = frictionFixture;
    }
}
