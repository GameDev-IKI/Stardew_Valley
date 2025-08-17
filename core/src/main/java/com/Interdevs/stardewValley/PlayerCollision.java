package com.Interdevs.stardewValley;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerCollision {

    private static TiledMap map;
    private static float playerWidth = 14f;
    private static float playerHeight = 29f;

    public static void setMap(TiledMap tiledMap) {
        map = tiledMap;
    }

    public static boolean canMoveTo(Vector2 position) {
        if (map == null) return true;

        Rectangle playerRect = new Rectangle(position.x, position.y, playerWidth, playerHeight);

        for (MapObject object : map.getLayers().get("Collisions").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (playerRect.overlaps(rect)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isTouchingAction(Vector2 position) {
        if (map == null) return false;

        Rectangle playerRect = new Rectangle(position.x, position.y, playerWidth, playerHeight);
        MapObjects actionObjects = map.getLayers().get("Action") != null ? map.getLayers().get("Action").getObjects() : null;

        if (actionObjects == null) {
            System.err.println("Error: 'Action' layer not found in the map.");
            return false;
        }

        for (MapObject object : actionObjects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (playerRect.overlaps(rect)) {
                    return true;
                }
            }
        }

        return false;
    }

}
