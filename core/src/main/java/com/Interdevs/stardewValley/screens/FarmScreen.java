package com.Interdevs.stardewValley.screens;

import com.Interdevs.stardewValley.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;

public class FarmScreen extends ScreenAdapter {

    private OrthographicCamera camera;
    private Viewport viewport;
    private OrthographicCamera uiCamera;
    private TiledMap map;
    private StardewValley game;
    private OrthogonalTiledMapRenderer mapRenderer;
    private float actionCooldown = 0f;
    private final float ACTION_COOLDOWN_TIME = 0.2f;
    private boolean processingTransition = false;
    private float clickCooldown = 0f;
    private InventoryScreen inventoryScreen;
    private Player player;
    private PlayerRenderer playerRenderer;
    private String entryPointName;
    // for growing crops
    private float growthTimer = 0f;
    private final float GROWTH_INTERVAL = 10f;// 10 seconds for testing

    private String selectedSeedType = "Sunflower";
    private FarmingManager farmingManager;
    TextureAtlas cornAtlas;
    TextureAtlas sunflowerAtlas;
    TextureAtlas wheatAtlas;
    private TextureRegion[] sunflowerFrames;
    private TextureRegion[] wheatFrames;
    private TextureRegion[] cornFrames;
    static HashMap<String, TextureRegion[]> cropRegistry = new HashMap<>();

    public void setSelectedSeedType(String selectedSeedType) {
        this.selectedSeedType = selectedSeedType;
    }

    public FarmScreen(StardewValley game) {
        this.game = game;
    }
    public FarmScreen(StardewValley game, String entryPointName){
        this.game = game;
        this.entryPointName = entryPointName;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        farmingManager = new FarmingManager();
        camera.zoom = 0.5f;
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);
        viewport = new FitViewport(1280, 720, camera);

        map = new TmxMapLoader().load("maps/Farm2.tmx");
        if (map == null) {
            System.out.println("Failed to load Farm2.tmx");
        }

        PlayerCollision.setMap(map);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        PlayerData playerData = PlayerData.loadFromFile();
        if (playerData == null) throw new RuntimeException("Failed to load player data.");
        player = new Player(playerData.getName(), playerData, game);
        player.addItem("Sunflower","seed");
        player.addItem("Wheat","seed");
        Vector2 spawn = Actions.getSpawnPosition(map, entryPointName);
        if (spawn == null) spawn = new Vector2(100, 100);
        player.setPosition(spawn.x, spawn.y);
        playerRenderer = new PlayerRenderer(player);

        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
        inventoryScreen = game.getInventoryScreen();
        inventoryScreen.setCropRegistry(cropRegistry);
        cornAtlas = new TextureAtlas(Gdx.files.internal("crops/corn.atlas"));
        sunflowerAtlas = new TextureAtlas(Gdx.files.internal("crops/sunflower.atlas"));
        wheatAtlas = new TextureAtlas(Gdx.files.internal("crops/wheat.atlas"));

        cornFrames = new TextureRegion[6];
        sunflowerFrames = new TextureRegion[6];
        wheatFrames = new TextureRegion[6];

        for (int i = 0; i < 6; i++) {
            cornFrames[i] = cornAtlas.findRegion("corn" + (i + 1));
            sunflowerFrames[i] = sunflowerAtlas.findRegion("sunflower" + (i + 1));
            wheatFrames[i] = wheatAtlas.findRegion("wheat" + (i + 1));
        }

        cropRegistry.put("Corn", cornFrames);
        cropRegistry.put("Sunflower", sunflowerFrames);
        cropRegistry.put("Wheat", wheatFrames);

        System.out.println("Camera position: " + camera.position + " Zoom: " + camera.zoom);
        Gdx.input.setInputProcessor(null); // Clear any existing processor
        processingTransition = false;
        clickCooldown = 0;
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null); // Clear input processor when screen is hidden
    }

    private void renderCrops() {
        for (Vector2 tilePos : farmingManager.getCropTilesMap().keySet()) {
            CropTile tile = farmingManager.getCropTilesMap().get(tilePos);

            if (tile.isPlanted()) {
                String cropType = tile.getCropType();
                int stage = tile.getGrowthStage();
                TextureRegion[] frames = cropRegistry.get(cropType);
                if (frames != null) {
                    TextureRegion currentFrame = frames[stage];
                    mapRenderer.getBatch().draw(currentFrame, tilePos.x, tilePos.y, 16, 16);
                } else {
                    System.err.println("Crop type not found in registry: " + cropType);
                }
            }
        }
    }


    @Override
    public void render(float delta) {
        if (processingTransition) {
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            System.out.println("FARM SCREEN LEFT CLICK - " + Gdx.input.getX() + "," + Gdx.input.getY());
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        if (actionCooldown > 0){
            actionCooldown -= delta;
        }
        // Check for action zone trigger
        if (!justPlanted && actionCooldown <= 0) {
            if (Actions.isTouching("Action", player.getPosition(), map)) {
                processingTransition = true;
                game.setScreen(new BedroomScreen(game));
                return;
            }
            if (Actions.isTouching("ActionStop", player.getPosition(), map)) {
                processingTransition = true;
                game.setScreen(new BusStopScreen(game, "FromFarm",player));
                return;
            }
        }

        camera.position.set(player.getPosition().x, player.getPosition().y, 0);

        // Clamp camera position
        float mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
        float halfViewportWidth = camera.viewportWidth * camera.zoom / 2;
        float halfViewportHeight = camera.viewportHeight * camera.zoom / 2;

        camera.position.x = Math.max(halfViewportWidth, Math.min(camera.position.x, mapWidth - halfViewportWidth));
        camera.position.y = Math.max(halfViewportHeight, Math.min(camera.position.y, mapHeight - halfViewportHeight));

        camera.update();

        mapRenderer.setView(camera);

        // Set view and start rendering background layers
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
        renderCrops();

// Render front and AlwaysFront layers
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            String name = map.getLayers().get(i).getName();
            if (name.equals("Front") || name.startsWith("AlwaysFront")) {
                if (map.getLayers().get(i).isVisible() && map.getLayers().get(i) instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
                    mapRenderer.renderTileLayer((com.badlogic.gdx.maps.tiled.TiledMapTileLayer) map.getLayers().get(i));
                }
            }
        }
        mapRenderer.getBatch().setProjectionMatrix(uiCamera.combined);
        inventoryScreen.render(mapRenderer.getBatch());
        mapRenderer.getBatch().end();
        growthTimer += delta;
        if (growthTimer >= GROWTH_INTERVAL) {
            for (Vector2 pos : farmingManager.getCropTilesMap().keySet()) {
                CropTile tile = farmingManager.getCropTilesMap().get(pos);
                System.out.println(pos + ": " + tile.getCropType() + " stage " + tile.getGrowthStage());
            }
            farmingManager.updateGrowth();
            growthTimer = 0f;
        }
    }


    private boolean isPlayerOnFarmTile(Vector2 pos) {
        // Get center of the tile the player is standing on
        float tileCenterX = ((int)(pos.x / 16) * 16) + 8; // +8 to get center
        float tileCenterY = ((int)(pos.y / 16) * 16) + 8;

        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().equalsIgnoreCase("FarmTile")) {
                for (MapObject object : layer.getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        if (rect.contains(tileCenterX, tileCenterY)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean justPlanted = false;

    private void attemptToPlant(Vector2 pos) {
        if (actionCooldown > 0) return;

        justPlanted = true;
        actionCooldown = ACTION_COOLDOWN_TIME;
        System.out.println("Attempting to plant at: " + pos);
        if (player == null) {
            System.out.println("Player is null!");
            return;
        }

        if (isPlayerOnFarmTile(pos)) {
            System.out.println("On farm tile");
            if (player.hasSeed(selectedSeedType)) {
                System.out.println("Player has seed: " + selectedSeedType);
                farmingManager.plantAt(pos, selectedSeedType);
                player.useSeed(selectedSeedType);
                System.out.println("Planting complete");
            } else {
                System.out.println("Player doesn't have seed: " + selectedSeedType);
            }
        } else {
            System.out.println("Not on farm tile");
        }
    }

    private void attemptToHarvest(Vector2 pos){
        Vector2 tilePos = new Vector2((int)(pos.x / 16) * 16, (int)(pos.y / 16) * 16); // Snap to tile
        if (player == null) {
            System.out.println("Player is null!");
            return;
        }
        if (isPlayerOnFarmTile(pos)){
            System.out.println("Player on farm tile");
            CropTile tile = farmingManager.getCropTilesMap().get(tilePos);
            if (tile != null && tile.isHarvestable()){
                String cropType = tile.getCropType();
                player.incrementCrop(cropType);
                System.out.println("crop incremented");
                for (InventoryItem item : Player.getInventory()) {
                    if (item != null && "crop".equals(item.getType()) && cropType.equals(item.getName())) {
                        System.out.printf("Harvested %s - New Quantity: %d%n",
                            cropType, item.getQuantityOfCrop());
                        break;
                    }
                }
                tile.harvest();
            }
        }
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            inventoryScreen.toggle();
            player.setMovementEnabled(!inventoryScreen.isVisible());
        }

        if (!player.isMovementEnabled()) return;


        float speed = 100f;
        Vector2 currentPos = player.getPosition();
        Vector2 newPos = new Vector2(currentPos);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newPos.x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newPos.x += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            newPos.y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            newPos.y -= speed * delta;
        }

        if (PlayerCollision.canMoveTo(newPos)) {
            player.setPosition(newPos.x, newPos.y);
        }

        if (clickCooldown <= 0) {
            justPlanted = false;

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                clickCooldown = 0.3f; // 300ms cooldown
                if (actionCooldown <= 0) {
                    attemptToPlant(player.getPosition());
                }
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                clickCooldown = 0.3f; // Also apply to right click
                if (actionCooldown <= 0) {
                    attemptToHarvest(player.getPosition());
                }
            }
        } else {
            clickCooldown -= delta;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        //viewport.apply();
        uiCamera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        playerRenderer.dispose();
        cornAtlas.dispose();
        sunflowerAtlas.dispose();
        wheatAtlas.dispose();

    }
}
