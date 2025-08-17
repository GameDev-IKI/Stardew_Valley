package com.Interdevs.stardewValley.screens;

import com.Interdevs.stardewValley.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
class BusStopScreen extends ScreenAdapter {

    private boolean processingClick = false;
    private boolean processingTransition = false;
    private float clickCooldown = 0;
    private StardewValley game;
    private OrthographicCamera camera;
    private Viewport viewport;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Player player;
    private PlayerData playerData;
    private PlayerRenderer playerRenderer;
    private String entryPointName;
    private InventoryScreen inventoryScreen;
    private OrthographicCamera uiCamera;

    public BusStopScreen(StardewValley game) {
        //this(game, null);
    }

    public BusStopScreen(StardewValley game, String entryPointName, Player player) {
        this.game = game;
        this.player = player;
        this.entryPointName = entryPointName;
    }

    public BusStopScreen(StardewValley game, String entryPointName, InventoryScreen inventoryScreen) {
        this.game = game;
        this.inventoryScreen = inventoryScreen;
        this.entryPointName = entryPointName;
    }

    @Override
    public void show() {

        if (this.inventoryScreen == null) {
            HashMap<String, TextureRegion[]> cropRegistry = FarmScreen.cropRegistry; // <- however you're loading it
            this.inventoryScreen = new InventoryScreen(game, player, cropRegistry);
        }

        camera = new OrthographicCamera();
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        uiCamera.update();
        camera.zoom = 0.5f;
        viewport = new FitViewport(1280, 720, camera);

        map = new TmxMapLoader().load("maps/BusStop.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        Vector2 spawn = Actions.getSpawnPosition(map, entryPointName);
//        this.playerData = player.getPlayerData();
//        if (this.playerData == null) {
//            this.playerData = new PlayerData();
//        }
        // safely extract data from the passed player

        if (spawn == null) spawn = new Vector2(100, 100); // fallback

        if (this.inventoryScreen == null) {
            this.inventoryScreen = new InventoryScreen(game,player,playerData,new FarmScreen(game));
        }
        player.setPosition(spawn.x, spawn.y);
        playerRenderer = new PlayerRenderer(player);

        camera.position.set(spawn.x, spawn.y, 0);
        camera.update();
        Gdx.input.setInputProcessor(null); // Clear any existing processor
        processingTransition = false;
        clickCooldown = 0;
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null); // Clear input processor when screen is hidden
    }

    @Override
    public void render(float delta) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            handleInput(delta);
            if (processingTransition){
                Gdx.input.setInputProcessor(null);
                return;
            }

            // Screen transitions
        if (Actions.isTouching("ActionFarm", player.getPosition(), map)) {
            processingTransition = true;
            game.setScreen(new FarmScreen(game, "FromStop"));
            return;
        }
        if (Actions.isTouching("ActionTown", player.getPosition(), map)) {
            processingTransition = true;
            game.setScreen(new TownScreen(game, "FromBusStop",player));
            return;
        }

        //toggle inventory
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            inventoryScreen.toggle();
            if (inventoryScreen.isVisible()){
                player.setMovementEnabled(false);
            } else {
                player.setMovementEnabled(true);
            }
        }

            updateCamera();

            mapRenderer.setView(camera);

            // 1. Render background layers (everything before "Front")
            mapRenderer.getBatch().begin();
            for (int i = 0; i < map.getLayers().getCount(); i++) {
                String layerName = map.getLayers().get(i).getName();
                if (layerName.equals("Front")) break;
                if (map.getLayers().get(i).isVisible() && map.getLayers().get(i) instanceof TiledMapTileLayer) {
                    mapRenderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(i));
                }
            }
            mapRenderer.getBatch().end();

            // 2. Render player
            playerRenderer.setCameraCombined(camera.combined);
            playerRenderer.render(delta);

            // 3. Render all front layers
            mapRenderer.getBatch().begin();
            for (int i = 0; i < map.getLayers().getCount(); i++) {
                String layerName = map.getLayers().get(i).getName();
                if (layerName.equals("Front") || layerName.startsWith("AlwaysFront")) {
                    if (map.getLayers().get(i).isVisible() && map.getLayers().get(i) instanceof TiledMapTileLayer) {
                        mapRenderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(i));
                    }
                }
            }
            mapRenderer.getBatch().setProjectionMatrix(uiCamera.combined);
            inventoryScreen.render(mapRenderer.getBatch()); //Render the shared inventory
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
        if (inventoryScreen.isVisible()) return; //disable movement

        float speed = 100f;
        Vector2 pos = player.getPosition();
        Vector2 newPos = new Vector2(pos);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) newPos.x -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) newPos.x += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) newPos.y += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) newPos.y -= speed * delta;

        if (clickCooldown <= 0 && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            clickCooldown = 0.3f;
            // Handle left click if needed
        } else {
            clickCooldown -= delta;
        }

        if (!Actions.isTouching("Collisions", newPos, map)) {
            player.setPosition(newPos.x, newPos.y);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        playerRenderer.dispose();
    }
}
