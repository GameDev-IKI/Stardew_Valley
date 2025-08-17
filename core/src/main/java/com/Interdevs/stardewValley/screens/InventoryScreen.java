package com.Interdevs.stardewValley.screens;

import com.Interdevs.stardewValley.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;
public class InventoryScreen {


    private Texture inventoryBackground;
    private boolean visible;
    private Player player;
    private BitmapFont font;
    private TextureAtlas cropAtlas;

    private static final float BOX_WIDTH = 881f;
    private static final float INVENTORY_HEIGHT = 656f;
    private static final float HOTBAR_HEIGHT = 656f;

    private static final int SLOTS_PER_ROW = 9;
    private static final int TOTAL_ROWS = 3;
    private static final int TOTAL_SLOTS = SLOTS_PER_ROW * TOTAL_ROWS;

    private static final float SLOT_WIDTH = 32f;
    private static final float SLOT_HEIGHT = 32f;
    private static final float SLOT_PADDING_X = 14.4f;
    private static final float SLOT_PADDING_Y = 16f;

    private static final float SLOT_SPACING_X = SLOT_WIDTH + SLOT_PADDING_X;
    private static final float SLOT_SPACING_Y = SLOT_HEIGHT + SLOT_PADDING_Y;

    private static final float SLOT_OFFSET_X = 18f;
    private static final float SLOT_OFFSET_Y = 48f;

    private static final float SCALE = 2f;

    private BitmapFont stardewFont;
    private TextureAtlas uiAtlas;
    private TextureRegion exitButtonRegion;
    private Texture goldCoinTexture;
    private float exitButtonX, exitButtonY;
    private static final float EXIT_BUTTON_WIDTH = 64f;
    private static final float EXIT_BUTTON_HEIGHT = 42f;

    private int draggedSlotIndex = -1;
    private float dragOffsetX, dragOffsetY;
    private boolean wasDragging = false;
    private StardewValley game;
    private PlayerData playerData;
    private HashMap<String, TextureRegion[]> cropRegistry;
    private FarmScreen farmScreen;

    public InventoryScreen(StardewValley game, Player player, PlayerData playerData, FarmScreen farmScreen) {
        this.game = game;
        this.player = player;
        this.playerData = playerData;
        this.farmScreen = farmScreen; // store reference

        this.visible = false;
        this.goldCoinTexture = new Texture(Gdx.files.internal("UI/gold_coin.png")); // or your correct path

        this.inventoryBackground = new Texture(Gdx.files.internal("UI/InventoryBox.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/SDV Fonts/Stardew_Valley.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.BROWN;
        font = generator.generateFont(parameter);
        generator.dispose();

        this.stardewFont = font;
        this.cropAtlas = new TextureAtlas(Gdx.files.internal("crops/crops.atlas"));
        this.uiAtlas = new TextureAtlas(Gdx.files.internal("StartScreen/textureButtons.atlas"));
        this.exitButtonRegion = uiAtlas.findRegion("exit_button_up");
    }

    public InventoryScreen(StardewValley game, Player player, HashMap<String, TextureRegion[]> cropRegistry) {
        this.game = game;
        this.player = player;
        this.cropRegistry = cropRegistry != null ? cropRegistry : new HashMap<>();  // Null check

        this.visible = false;
        this.goldCoinTexture = new Texture(Gdx.files.internal("UI/gold_coin.png")); // or your correct path

        this.inventoryBackground = new Texture(Gdx.files.internal("UI/InventoryBox.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/SDV Fonts/Stardew_Valley.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.BROWN;
        font = generator.generateFont(parameter);
        generator.dispose();

        this.stardewFont = font;
        this.cropAtlas = new TextureAtlas(Gdx.files.internal("crops/crops.atlas"));
        this.uiAtlas = new TextureAtlas(Gdx.files.internal("StartScreen/textureButtons.atlas"));
        this.exitButtonRegion = uiAtlas.findRegion("exit_button_up");
    }





//    public InventoryScreen(StardewValley game, Player player, PlayerData playerData) {
//        this.game = game;
//        this.player = player;
//        this.playerData = playerData;
//
//    }

    public void toggle() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }

    private TextureRegion getCropSprite(String cropName, int stage) {
        if (cropRegistry != null && cropRegistry.containsKey(cropName)) {
            TextureRegion[] frames = cropRegistry.get(cropName);
            if (frames != null && frames.length > 0) {
                return frames[Math.min(stage - 1, frames.length - 1)];
            }
        }
        return null;
    }


    public void render(Batch batch) {
        // Set the dimensions and position of the inventory box
        float boxWidth = BOX_WIDTH;
        float boxHeight = visible ? INVENTORY_HEIGHT : HOTBAR_HEIGHT;
        float x = (Gdx.graphics.getWidth() - boxWidth) / 2f;
        float y = visible ? (Gdx.graphics.getHeight() - boxHeight) / 2f : -520f;


        // Render the inventory background
        batch.draw(inventoryBackground, x, y, boxWidth, boxHeight);

        // Render the gold coin texture and the amount of gold
        if (goldCoinTexture != null) {
            batch.draw(goldCoinTexture, 10, Gdx.graphics.getHeight() - 50);
        }
        font.draw(batch, String.valueOf(player.getGold()), 50, Gdx.graphics.getHeight() - 20);

        // Handle drop logic
        if (wasDragging && !Gdx.input.isButtonPressed(Input.Buttons.LEFT) && draggedSlotIndex != -1) {
            for (int i = 0; i < player.getInventory().size; i++) {
                float slotX = x + SLOT_OFFSET_X * SCALE + (i % SLOTS_PER_ROW) * SLOT_SPACING_X * SCALE;
                float slotY = visible
                    ? y + boxHeight - SLOT_OFFSET_Y * SCALE - (i / SLOTS_PER_ROW) * SLOT_SPACING_Y * SCALE
                    : y + 8f;
                if (isSlotClicked(slotX, slotY, SCALE)) {
                    swapItems(i);
                    break;
                }
            }
            draggedSlotIndex = -1;
        }

        // Draw inventory slots
        for (int i = 0; i < player.getInventory().size; i++) {
            float slotX = x + SLOT_OFFSET_X * SCALE + (i % SLOTS_PER_ROW) * SLOT_SPACING_X * SCALE;
            float slotY = visible
                ? y + boxHeight - SLOT_OFFSET_Y * SCALE - (i / SLOTS_PER_ROW) * SLOT_SPACING_Y * SCALE
                : y + 8f;
            drawInventorySlot(batch, i, slotX, slotY);
        }

        if (visible) {
            // Display player name and farm name
            stardewFont.draw(batch, "Name: " + player.getName(), 560, Gdx.graphics.getHeight() - 620);
            stardewFont.draw(batch, "Farm: " + player.getFarmName(), 560, Gdx.graphics.getHeight() - 680);

            // Draw exit button
            exitButtonX = x + BOX_WIDTH - EXIT_BUTTON_WIDTH - 180f;
            exitButtonY = y + 90f;
            batch.draw(exitButtonRegion, exitButtonX, exitButtonY, EXIT_BUTTON_WIDTH * 3, EXIT_BUTTON_HEIGHT * 3);
        }

        // Draw the dragged item (if any)
        if (draggedSlotIndex != -1) {
            String itemName = player.getInventory().get(draggedSlotIndex).getName();
            if (itemName != null && !itemName.isEmpty()) {
                String cropName = itemName.replaceAll("\\d", "");
                String stageStr = itemName.replaceAll("\\D", "");
                int stage;
                InventoryItem draggedItem = player.getInventory().get(draggedSlotIndex);
                if (draggedItem.getType().equals("crop")) {
                    TextureRegion[] frames = cropRegistry.get(cropName);
                    stage = frames != null ? frames.length : 1;
                } else {
                    stage = stageStr.isEmpty() ? 1 : Integer.parseInt(stageStr);
                }
                TextureRegion cropSprite = getCropSprite(cropName, stage);

                if (cropSprite != null) {
                    float drawX = Gdx.input.getX() - dragOffsetX;
                    float drawY = Gdx.graphics.getHeight() - Gdx.input.getY() - dragOffsetY;
                    batch.draw(cropSprite, drawX, drawY, SLOT_WIDTH * SCALE, SLOT_HEIGHT * SCALE);
                }
            }
        }

        wasDragging = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        if (visible && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (mouseX >= exitButtonX && mouseX <= exitButtonX + EXIT_BUTTON_WIDTH * 3 &&
                mouseY >= exitButtonY && mouseY <= exitButtonY + EXIT_BUTTON_HEIGHT * 3) {
                game.setScreen(new StartScreen(game));
            }
        }
 // End batch after all rendering
    }


    private void drawInventorySlot(Batch batch, int index, float slotX, float slotY) {
        InventoryItem item = player.getInventory().get(index);
        if (item == null) return; // Null item check

        String itemName = item.getName();
        if (itemName == null || itemName.isEmpty()) return;

        String cropName = itemName.replaceAll("\\d", "");
        String stageStr = itemName.replaceAll("\\D", "");
        int stage;

        TextureRegion cropSprite = null;

        if (item.getType().equals("crop")) {
            if (cropRegistry != null && cropRegistry.containsKey(cropName)) {
                TextureRegion[] frames = cropRegistry.get(cropName);
                stage = frames != null ? frames.length : 1;
                if (frames != null && frames.length > 0) {
                    int safeStage = Math.min(stage - 1, frames.length - 1);
                    cropSprite = frames[safeStage];
                }

            } else {
                stage = 1; // fallback
            }
        } else {
            stage = stageStr.isEmpty() ? 1 : Integer.parseInt(stageStr);
            cropSprite = getCropSprite(cropName, stage); // Assume this is safe
        }

        if (cropSprite != null) {
            Texture tex = cropSprite.getTexture();
            if (tex != null) {
                batch.draw(cropSprite, slotX, slotY, SLOT_WIDTH * SCALE, SLOT_HEIGHT * SCALE);
            }
        }



        int quantity = 0;
        if (item.getType().equals("seed")) {
            quantity = item.getNumberOfSeads();
        } else if (item.getType().equals("crop")) {
            quantity = item.getQuantityOfCrop();
        }

        if (quantity > 1) {
            String quantityStr = String.valueOf(quantity);
            stardewFont.draw(batch, quantityStr,
                slotX + SLOT_WIDTH * SCALE - 10 * SCALE,
                slotY + 12 * SCALE);
        }

        font.draw(batch, itemName, slotX + 8 * SCALE, slotY + SLOT_HEIGHT * SCALE, SLOT_WIDTH * SCALE, Align.center, false);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && draggedSlotIndex == -1) {
            if (isSlotClicked(slotX, slotY, SCALE)) {
                draggedSlotIndex = index;
                dragOffsetX = Gdx.input.getX() - slotX;
                dragOffsetY = Gdx.graphics.getHeight() - Gdx.input.getY() - slotY;

                if (item.getType().equals("seed")) {
                    farmScreen.setSelectedSeedType(item.getName());
                }
            }
        }
    }


    private boolean isSlotClicked(float slotX, float slotY, float scale) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mouseX >= slotX && mouseX <= slotX + SLOT_WIDTH * scale &&
            mouseY >= slotY && mouseY <= slotY + SLOT_HEIGHT * scale;
    }

    private void swapItems(int targetIndex) {
        if (targetIndex == draggedSlotIndex) return;
        InventoryItem temp = player.getInventory().get(targetIndex);
        player.getInventory().set(targetIndex, player.getInventory().get(draggedSlotIndex));
        player.getInventory().set(draggedSlotIndex, temp);
    }

    public void dispose() {
        inventoryBackground.dispose();
        font.dispose();
        cropAtlas.dispose();
    }

    public void setCropRegistry(HashMap<String, TextureRegion[]> cropRegistry) {
        this.cropRegistry = cropRegistry;
    }
}
