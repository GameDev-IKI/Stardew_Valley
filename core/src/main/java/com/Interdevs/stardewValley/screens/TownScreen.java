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

public class TownScreen extends ScreenAdapter {

    private StardewValley game;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Player player;
    private PlayerRenderer playerRenderer;
    private InventoryScreen inventoryScreen;
    private String entryPointName;

//    public TownScreen(StardewValley game) {
//        this(game, null);
//    }

    public TownScreen(StardewValley game, String entryPointName, Player player) {
        this.game = game;
        this.entryPointName = entryPointName;
        this.player = player;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.zoom = 0.5f;
        viewport = new FitViewport(1280, 720, camera);
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);
        map = new TmxMapLoader().load("maps/Town.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
//        PlayerData playerData = PlayerData.loadFromFile();
//        if (playerData == null) {
//            throw new RuntimeException("Failed to load valid player data.");
//        }
        Vector2 spawn = Actions.getSpawnPosition(map, entryPointName);
        if (spawn == null) spawn = new Vector2(100, 100); // fallback

        //player = new Player(playerData.getName(), playerData, game);
        player.setPosition(spawn.x, spawn.y);

        playerRenderer = new PlayerRenderer(player);

        camera.position.set(spawn.x, spawn.y, 0);
        camera.update();
        this.inventoryScreen = game.getInventoryScreen();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        if (Actions.isTouching("ActionStore", player.getPosition(), map)) {
            game.setScreen(new GeneralStoreScreen(game, "FromTown",player));
            return;
        }

        if (Actions.isTouching("ActionStop", player.getPosition(), map)) {
            game.setScreen(new BusStopScreen(game, "FromTown",player));
            return;
        }


        updateCamera();

        mapRenderer.setView(camera);
        mapRenderer.getBatch().begin();
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            String name = map.getLayers().get(i).getName();
            if (name.equals("Front")) break;
            if (map.getLayers().get(i).isVisible() && map.getLayers().get(i) instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
                mapRenderer.renderTileLayer((com.badlogic.gdx.maps.tiled.TiledMapTileLayer) map.getLayers().get(i));
            }
        }

// Render player
        playerRenderer.setCameraCombined(camera.combined);
        playerRenderer.render(delta);

// Render front and AlwaysFront layers
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            String name = map.getLayers().get(i).getName();
            if (name.equals("Front") || name.startsWith("AlwaysFront")) {
                if (map.getLayers().get(i).isVisible() && map.getLayers().get(i) instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
                    mapRenderer.renderTileLayer((com.badlogic.gdx.maps.tiled.TiledMapTileLayer) map.getLayers().get(i));
                }
            }
        }
        mapRenderer.getBatch().end();
        mapRenderer.getBatch().setProjectionMatrix(uiCamera.combined);
        mapRenderer.getBatch().begin();
        inventoryScreen.render(mapRenderer.getBatch());
        mapRenderer.getBatch().end();
    }

    private void updateCamera() {
        float mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

        float halfW = camera.viewportWidth * camera.zoom / 2;
        float halfH = camera.viewportHeight * camera.zoom / 2;

        Vector2 pos = player.getPosition();
        camera.position.set(
            Math.max(halfW, Math.min(pos.x, mapWidth - halfW)),
            Math.max(halfH, Math.min(pos.y, mapHeight - halfH)),
            0
        );
        camera.update();
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            inventoryScreen.toggle();
            player.setMovementEnabled(!inventoryScreen.isVisible());
        }

        if (!player.isMovementEnabled()) return;
        float speed = 100f;
        Vector2 pos = player.getPosition();
        Vector2 newPos = new Vector2(pos);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) newPos.x -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) newPos.x += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) newPos.y += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) newPos.y -= speed * delta;

        if (!Actions.isTouching("Collisions", newPos, map)) {
            player.setPosition(newPos.x, newPos.y);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiCamera.setToOrtho(false, width, height); // Sync UI camera
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        playerRenderer.dispose();
    }
}
