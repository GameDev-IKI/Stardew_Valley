package com.Interdevs.stardewValley.screens;

import com.Interdevs.stardewValley.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;
public class GeneralStoreScreen extends ScreenAdapter {

    private StardewValley game;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Player player;
    private PlayerRenderer playerRenderer;
    private String entryPointName;
    private Texture dialogueTexture;
    private BitmapFont dialogueFont;
    private boolean inDialogue = false;
    private String currentDialogue = "";
    private Stage dialogueStage;
    private int selectedOption = -1;
    private SpriteBatch menuBatch;
    private InventoryScreen inventoryScreen;

    public GeneralStoreScreen(StardewValley game, String entryPointName, Player player) {
        this.game = game;
        this.entryPointName = entryPointName;
        this.player = player; // Reuse the passed player
    }


    @Override
    public void show() {
        try {
            camera = new OrthographicCamera();
            camera.zoom = 0.5f;
            uiCamera = new OrthographicCamera();
            uiCamera.setToOrtho(false, 1280, 720);
            viewport = new FitViewport(1280, 720, camera);

            map = new TmxMapLoader().load("maps/General_Store.tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

            Vector2 spawn = Actions.getSpawnPosition(map, entryPointName);
            if (spawn == null) spawn = new Vector2(100, 100); // fallback
            player.setPosition(spawn.x, spawn.y);
            playerRenderer = new PlayerRenderer(player);

            camera.position.set(spawn.x, spawn.y, 0);
            camera.update();
            //load dialogue assets
            menuBatch = new SpriteBatch();
            try {
                dialogueTexture = new Texture(Gdx.files.internal("CharacterCreationScreen/Character_Creation_Box.png"));
                dialogueFont = new BitmapFont(Gdx.files.internal("Fonts/Stardew_Valley_Font.fnt"), false);
            } catch (GdxRuntimeException e) {
                Gdx.app.error("GeneralStore", "Failed to load dialogue assets", e);
                // Fallback to default font
                dialogueFont = new BitmapFont();
            }// Setup dialogue stage
            dialogueStage = new Stage(new ScreenViewport());
            inventoryScreen = game.getInventoryScreen();
        } catch (Exception e) {
        Gdx.app.error("GeneralStore", "Initialization failed", e);
        game.setScreen(new TownScreen(game, "FromStore",player)); // Fallback
    }
    }

    @Override
    public void render(float delta) {
        try {
            // Clear screen
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            handleInput(delta);

            if (Actions.isTouching("ActionTownFromStore", player.getPosition(), map)) {
                game.setScreen(new TownScreen(game, "FromStore",player));
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
            if (Actions.isTouching("Counter", player.getPosition(), map) &&
                !inDialogue && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                inDialogue = true;
                selectedOption = -1;
                currentDialogue = "Welcome to Pierre's!\nPress E to sell crops";
            }

            if (inDialogue) {
                renderDialogue();
                handleDialogueInput();
            }
            if (inventoryScreen != null && inventoryScreen.isVisible()) {
                inventoryScreen.render(menuBatch);
            }
        } catch (Exception e) {
            System.out.println("Re rendering townScreen");
        Gdx.app.error("GeneralStore", "Rendering error", e);
        game.setScreen(new TownScreen(game, "FromStore",player)); // Fallback
    }
    }

    private int getCropPrice(String cropName) {
        // Define prices for each crop type
        switch(cropName.toLowerCase()) {
            case "wheat":
                return 25;
            case "sunflower":
                return 50;
            case "corn":
                return 75;
            default:
                return 0; // Unknown crops have no value
        }
    }

    private void renderDialogue() {
        if (menuBatch == null) return;
        // Position at bottom right
        float x = Gdx.graphics.getWidth() - dialogueTexture.getWidth() - 20;
        float y = 20;

        menuBatch.begin();
        // Draw background
        menuBatch.draw(dialogueTexture, x, y);

        // Draw text
        dialogueFont.draw(menuBatch, currentDialogue, x + 30, y + 100);

        // Draw options if in selection mode
        if (selectedOption >= 0) {
            int yOffset = 80;
            for (int i = 0; i < player.getCrops().size(); i++) {
                InventoryItem crop = player.getCrops().get(i);
                String text = String.format("%d. %s x%d - %dg",
                    i+1, crop.getName(), crop.getQuantityOfCrop(), getCropPrice(crop.getName()));

                // Highlight selected option
                if (i == selectedOption) {
                    dialogueFont.setColor(Color.YELLOW);
                } else {
                    dialogueFont.setColor(Color.WHITE);
                }

                dialogueFont.draw(menuBatch, text, x + 50, y + yOffset);
                yOffset -= 30;
            }
        }
        menuBatch.end();
    }

    private void handleDialogueInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (selectedOption == -1) {
                // First press - show crop options
                if (!player.getCrops().isEmpty()) {
                    currentDialogue = "Select crop to sell:";
                    selectedOption = 0; // Default to first option
                } else {
                    currentDialogue = "You have no crops to sell!";
                }
            } else {
                // Sell the selected crop
                sellAllCrops(player);
                selectedOption = -1;
                currentDialogue = "Thanks! Anything else?\nPress E to sell more crops";
            }
        }

        // Handle option selection with arrow keys
        if (!player.getCrops().isEmpty()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedOption = Math.min(selectedOption + 1, player.getCrops().size() - 1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedOption = Math.max(selectedOption - 1, 0);
            }

            // Or with number keys
            for (int i = 0; i < player.getCrops().size(); i++) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.valueOf(String.valueOf(i+1)))) {
                    sellAllCrops(player);
                    selectedOption = -1;
                    currentDialogue = "Thanks! Anything else?\nPress E to sell more crops";
                    break;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            inDialogue = false;
            selectedOption = -1;
        }
    }

    private void sellAllCrops(Player player) {
        int totalEarnings = 0;

        // Get all crops from inventory
        for (InventoryItem crop : player.getCrops()) {
            int quantity = crop.getQuantityOfCrop();
            int pricePerUnit = getCropPrice(crop.getName());
            totalEarnings += pricePerUnit * quantity;

            // Remove the crop from inventory
            player.removeItem(crop);
        }

        // Add gold to player
        player.addGold(totalEarnings);

        // Optional: show feedback
        System.out.println("Sold all crops! Total earned: " + totalEarnings + "g");
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
            try {
                if (map != null) map.dispose();
                if (mapRenderer != null) mapRenderer.dispose();
                if (playerRenderer != null) playerRenderer.dispose();
                if (menuBatch != null) menuBatch.dispose();
                if (dialogueTexture != null) dialogueTexture.dispose();
                if (dialogueFont != null) dialogueFont.dispose();
                if (dialogueStage != null) dialogueStage.dispose();
            } catch (Exception e) {
                Gdx.app.error("GeneralStore", "Dispose error", e);
            }
        }
    }
