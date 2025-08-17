package com.Interdevs.stardewValley;

import com.Interdevs.stardewValley.screens.FarmScreen;
import com.Interdevs.stardewValley.screens.InventoryScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private String gender;
    private String farmName;
    private String favoriteThing;
    private String name;
    private int gold;
    private static Array<InventoryItem> inventory;
    private int currentHeldItemIndex;
    private Vector2 position;
    private OrthographicCamera camera;
    private boolean movementEnabled = true;
    private StardewValley game;
    private InventoryScreen inventoryScreen;
    private FarmScreen farmScreen;

    public Player(String name) {
        this.name = name;
        this.gold = 500;
        inventory = new Array<>();
        this.position = new Vector2(1024, 770);
        this.currentHeldItemIndex = 0;
        this.position = new Vector2();
    }

    public Player(PlayerData data) {
        this.name = data.getName();
        this.gender = data.getGender();
        this.farmName = data.getFarmName();
        this.favoriteThing = data.getFavoriteThing();
        this.position = new Vector2();
    }

    public Player(String name, PlayerData playerData, StardewValley game) {
        this.name = name;
        this.gold = 500;
        this.inventory = new Array<>();
        this.position = new Vector2(1024, 770);
        this.currentHeldItemIndex = 0;
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 1280, 720);
        this.camera.position.set(position.x, position.y, 0);
        this.inventoryScreen = new InventoryScreen(game, this, playerData,new FarmScreen(game));
    }

    public void move(float dx, float dy) {
        position.add(dx, dy);
        updateCamera();
    }

    public void updateCamera() {
        camera.position.set(position.x, position.y, 0);
        camera.update();
    }

    public void addItem(String itemName, String type) {
        inventory.add(new InventoryItem(itemName, type));
    }

    public InventoryItem getHeldItem() {
        if (inventory.size > 0) return inventory.get(currentHeldItemIndex);
        else return null;
    }

    public void nextItem() {
        if (inventory.size > 0) {
            currentHeldItemIndex = (currentHeldItemIndex + 1) % inventory.size;
        }
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            selectItemByKey(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            selectItemByKey(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            selectItemByKey(3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            selectItemByKey(4);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            selectItemByKey(5);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            selectItemByKey(6);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            selectItemByKey(7);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            selectItemByKey(8);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            selectItemByKey(9);
        }
    }

    public void selectItemByKey(int keyIndex) {
        if (keyIndex >= 1 && keyIndex <= 9 && keyIndex - 1 < inventory.size) {
            currentHeldItemIndex = keyIndex - 1;
        }
    }

    public boolean hasSeed(String seedType){
        for (InventoryItem item:Player.getInventory()){
            if (item.getType().equals("seed") && item.getName().equals(seedType)){
                if (item.getNumberOfSeads() >= 1){
                    return true;
                }
            }
        }
        return false;
    }

    // In Player class:
    public boolean useSeed(String seedType) {
        for (InventoryItem item : inventory) {
            if (item.getType().equals("seed") && item.getName().equals(seedType)) {
                if (item.getNumberOfSeads() > 0) {
                    item.setNumberOfSeads(item.getNumberOfSeads()-1);
                    if (item.getNumberOfSeads() <= 0) {
                        inventory.removeValue(item, true);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public List<InventoryItem> getCrops() {
        List<InventoryItem> crops = new ArrayList<>();
        for (InventoryItem item : inventory) {
            if (item != null && item.getType() != null && item.getType().equals("crop")) {
                crops.add(item);
            }
        }
        return crops;
    }

    public boolean removeItem(InventoryItem itemToRemove) {
        Iterator<InventoryItem> iterator = inventory.iterator();
        while (iterator.hasNext()) {
            InventoryItem item = iterator.next();
            if (item != null && item.equals(itemToRemove)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public void incrementCrop(String cropName) {
        boolean found = false;
            for (InventoryItem item : Player.getInventory()){
                if (item != null && item.getType() != null && item.getName() != null) {
                    if (item.getType().equals("crop") && item.getName().equals(cropName)){
                        item.incrementQuantity();
                        found = true;
                        break;
                    }
                }
            }

        if (!found) {
            InventoryItem newCrop = new InventoryItem(cropName, "crop");
            newCrop.incrementQuantity(); // first harvested crop
            inventory.add(newCrop);
        }
    }


    // Getters and Setters
    public String getName() {
        return name;
    }

    public int getGold() {
        return gold;
    }

    public Vector2 getPosition() {
        return position;
    }

    public static Array<InventoryItem> getInventory() {
        return inventory;
    }

    public int getCurrentHeldItemIndex() {
        return currentHeldItemIndex;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
    public void addGold(int amount) {
        gold += amount;
    }

    public boolean deductGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public boolean isMovementEnabled() {
        return movementEnabled;
    }

    public void setMovementEnabled(boolean enabled) {
        this.movementEnabled = enabled;
    }

    public InventoryScreen getInventoryScreen() {
        return inventoryScreen;
    }

    public void setInventory(Array<InventoryItem> inventory) {
        this.inventory = inventory;
    }

    public String getFarmName() {
        return farmName;
    }
}
