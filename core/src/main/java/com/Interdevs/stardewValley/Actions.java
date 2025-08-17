package com.Interdevs.stardewValley;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Actions {
    public static boolean isTouching(String layerName, Vector2 position, TiledMap map) {
        if (map == null) return false;

        Rectangle playerRect = new Rectangle(position.x, position.y, 16, 16); // Adjust to player size
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) return false;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (playerRect.overlaps(rect)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Vector2 getSpawnPosition(TiledMap map, String spawnName) {
        MapLayer spawnsLayer = map.getLayers().get("Spawns");
        if (spawnsLayer == null) return null;

        for (MapObject object : spawnsLayer.getObjects()) {
            if (spawnName.equals(object.getName())) {
                float x = Float.parseFloat(object.getProperties().get("x").toString());
                float y = Float.parseFloat(object.getProperties().get("y").toString());
                return new Vector2(x, y);
            }
        }

        return null;
    }
}
