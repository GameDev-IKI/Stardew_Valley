package com.Interdevs.stardewValley;

import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

public class FarmingManager {
    private static HashMap<Vector2, CropTile> cropTilesMap;

    public FarmingManager() {
        cropTilesMap = new HashMap<>();
        // Initialize all farmable tile positions here
    }

    public CropTile getTile(Vector2 tilePos) {
        return cropTilesMap.get(tilePos);
    }

    public void plantAt(Vector2 tilePos, String seedType) {
        tilePos = new Vector2((int)(tilePos.x / 16) * 16, (int)(tilePos.y / 16) * 16); // snap to grid

        CropTile tile = cropTilesMap.get(tilePos);
        if (tile == null) {
            tile = new CropTile(tilePos); // Ensure CropTile stores its own position
            cropTilesMap.put(tilePos, tile);
        }

        if (!tile.isPlanted()) {
            tile.plantCrop(seedType); // sets planted = true and growthStage = 0
            System.out.println("Planted crop: " + seedType + " at " + tilePos);
        } else{
            System.out.println("Tile at " + tilePos + " already has a crop planted.");
        }
    }

    public void waterAt(Vector2 tilePos) {
        CropTile tile = getTile(tilePos);
        if (tile != null && tile.isPlanted()) {
            tile.waterCrop();
        }
    }

    public void harvestAt(Vector2 tilePos, Player player) {
        CropTile tile = getTile(tilePos);
        if (tile != null && tile.isHarvestable()) {
            String crop = tile.getCropType();
            tile.harvest();
            player.addItem(crop, "seed");
        }
    }



    public void updateGrowth() {
        for (CropTile tile : cropTilesMap.values()) {
            tile.updateGrowth();
        }
    }

    public Map<Vector2, CropTile> getCropTilesMap() {
        return cropTilesMap;
    }
}
