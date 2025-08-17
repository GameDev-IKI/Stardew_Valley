package com.Interdevs.stardewValley.screens;

import com.Interdevs.stardewValley.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.concurrent.atomic.AtomicBoolean;

public class BedroomScreen extends ScreenAdapter {

    private StardewValley game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Player player;
    private PlayerRenderer playerRenderer;

    public BedroomScreen(StardewValley game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.zoom = 0.5f;
        viewport = new FitViewport(1280, 720, camera);

        map = new TmxMapLoader().load("maps/bedroom.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        player = new Player("Player");
        player.setPosition(6 * 16, 4 * 15); // center of 12x12 map

        playerRenderer = new PlayerRenderer(player);

        PlayerCollision.setMap(map); // Correct: no constructor

        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.getBatch().begin();
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            String name = map.getLayers().get(i).getName();
            if (name.equals("Front")) break;
            if (map.getLayers().get(i).isVisible() && map.getLayers().get(i) instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
                mapRenderer.renderTileLayer((com.badlogic.gdx.maps.tiled.TiledMapTileLayer) map.getLayers().get(i));
            }
        }
        mapRenderer.getBatch().end();

// Render player
        playerRenderer.setCameraCombined(camera.combined);
        playerRenderer.render(delta);

// Render front and AlwaysFront layers
        mapRenderer.getBatch().begin();
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            String name = map.getLayers().get(i).getName();
            if (name.equals("Front") || name.startsWith("AlwaysFront")) {
                if (map.getLayers().get(i).isVisible() && map.getLayers().get(i) instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
                    mapRenderer.renderTileLayer((com.badlogic.gdx.maps.tiled.TiledMapTileLayer) map.getLayers().get(i));
                }
            }
        }
        mapRenderer.getBatch().end();
    }

    private void handleInput(float delta) {
        AtomicBoolean transitioning = new AtomicBoolean(false);
        float speed = 100f;
        Vector2 nextPos = new Vector2(player.getPosition());

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            nextPos.x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            nextPos.x += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            nextPos.y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            nextPos.y -= speed * delta;
        }

        if (PlayerCollision.canMoveTo(nextPos)) {
            player.setPosition(nextPos.x, nextPos.y);
        }

        if (Actions.isTouching("Action", player.getPosition(), map)){
            game.setScreen(new FarmScreen(game, "FromBedroom"));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        playerRenderer.dispose();
    }
}
