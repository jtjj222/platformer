package com.justinmichaud.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {
    public final TextureRegion region;

    public TextureComponent(TextureRegion region) {
        this.region = region;
    }
}