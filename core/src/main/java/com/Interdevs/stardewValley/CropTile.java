package com.Interdevs.stardewValley;

import com.badlogic.gdx.math.Vector2;

public class CropTile {
    public static final int MAX_STAGE = 5;  // stages are from 0-5 as given index
    private Vector2 position;
    private String cropType; // wheat, corn, etc.
    private int growthStage;
    private boolean isWatered;
    private long lastWateredTime; // for growth timing

    public CropTile(Vector2 position) {
        this.position = position;
        this.cropType = null;
        this.growthStage = 0;
        this.isWatered = false;
    }

    public boolean isPlanted() {
        return cropType != null;
    }

    public void plantCrop(String cropType) {
        this.cropType = cropType;
        this.growthStage = 0;
        this.lastWateredTime = System.currentTimeMillis();
        this.isWatered = true;
        System.out.println("CropTile planted: " + cropType + ", growthStage = " + growthStage);
    }

    public void waterCrop() {
        if (isPlanted() && growthStage < 6) {
            isWatered = true;
            lastWateredTime = System.currentTimeMillis();
        }
    }

    public void updateGrowth() {
        if (isPlanted() && growthStage < MAX_STAGE) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastWateredTime >= 5000) { // 1 min growth
                growthStage++;
                isWatered = false;
                lastWateredTime = currentTime;
            }
        }
    }

    public boolean isHarvestable() {
        return isPlanted() && growthStage == MAX_STAGE;
    }

    public void harvest() {
        cropType = null;
        growthStage = 0;
        isWatered = false;
    }


    // Getters
    public String getCropType() { return cropType; }
    public int getGrowthStage() { return growthStage; }
    public Vector2 getPosition() { return position; }
}
