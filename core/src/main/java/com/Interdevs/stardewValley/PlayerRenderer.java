package com.Interdevs.stardewValley;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class PlayerRenderer {
    private Matrix4 cameraCombined;
    private Player player;
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    private float stateTime;
    private SpriteBatch batch;
    private TextureAtlas atlas;
    private String lastDirection = "down";

    public PlayerRenderer(Player player) {
        this.player = player;
        this.batch = new SpriteBatch();
        this.atlas = new TextureAtlas(Gdx.files.internal("sprites/male_walk.atlas"));

        walkDown = new Animation<>(0.15f,
            atlas.findRegion("wForward1"),
            atlas.findRegion("wForward2"),
            atlas.findRegion("wForward3")
        );

        walkUp = new Animation<>(0.15f,
            atlas.findRegion("wBack1"),
            atlas.findRegion("wBack2"),
            atlas.findRegion("wBack3")
        );

        walkRight = new Animation<>(0.15f,
            atlas.findRegion("wRight1"),
            atlas.findRegion("wRight2"),
            atlas.findRegion("wRight3")
        );

        // For walkLeft: use walkRight frames, but flipped horizontally
        TextureRegion wLeft1 = new TextureRegion(atlas.findRegion("wRight1"));
        TextureRegion wLeft2 = new TextureRegion(atlas.findRegion("wRight2"));
        TextureRegion wLeft3 = new TextureRegion(atlas.findRegion("wRight3"));
        wLeft1.flip(true, false);
        wLeft2.flip(true, false);
        wLeft3.flip(true, false);

        walkLeft = new Animation<>(0.15f, wLeft1, wLeft2, wLeft3);


        walkDown.setPlayMode(Animation.PlayMode.LOOP);
        walkUp.setPlayMode(Animation.PlayMode.LOOP);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);
        walkRight.setPlayMode(Animation.PlayMode.LOOP);

        stateTime = 0f;
    }

    public void setCameraCombined(Matrix4 cameraCombined) {
        this.cameraCombined = cameraCombined;
    }

    public void render(float delta) {
        stateTime += delta;

        batch.setProjectionMatrix(cameraCombined);
        batch.begin();

        Vector2 pos = player.getPosition();
        TextureRegion frame = getCurrentFrame();
        batch.draw(frame, pos.x, pos.y, frame.getRegionWidth() * 1f, frame.getRegionHeight() * 1f); // 2x scale
        batch.end();
    }

    private TextureRegion getCurrentFrame() {
        if (!isMoving()) {
            switch (lastDirection) {
                case "left": return walkLeft.getKeyFrame(0);
                case "right": return walkRight.getKeyFrame(0);
                case "up": return walkUp.getKeyFrame(0);
                case "down": return walkDown.getKeyFrame(0);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            lastDirection = "left";
            return walkLeft.getKeyFrame(stateTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            lastDirection = "right";
            return walkRight.getKeyFrame(stateTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            lastDirection = "up";
            return walkUp.getKeyFrame(stateTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            lastDirection = "down";
            return walkDown.getKeyFrame(stateTime);
        }

        return walkDown.getKeyFrame(0);
    }

    private boolean isMoving() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
            Gdx.input.isKeyPressed(Input.Keys.UP) ||
            Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    public void dispose() {
        batch.dispose();
        atlas.dispose();
    }
}
