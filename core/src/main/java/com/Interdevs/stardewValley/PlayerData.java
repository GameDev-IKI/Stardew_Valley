package com.Interdevs.stardewValley;

import java.io.*;

public class PlayerData {
    public String name;
    public String farmName;
    public String favoriteThing;
    public String gender;

    public PlayerData() {
    }

    public PlayerData(String name, String farmName, String favoriteThing, String gender) {
        this.name = name;
        this.farmName = farmName;
        this.favoriteThing = favoriteThing;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getFavoriteThing() {
        return favoriteThing;
    }

    public void setFavoriteThing(String favoriteThing) {
        this.favoriteThing = favoriteThing;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void saveToFile() {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("savegame.txt",true));
            writer.write(name);
            writer.write("\n"+ farmName);
            writer.write("\n"+ favoriteThing);
            writer.write("\n"+ gender+"\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static PlayerData loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("savegame.txt"))) {
            String name = null, farm = null, thing = null, gender = null;
            String line;
            while ((line = reader.readLine()) != null) {
                name = line;
                farm = reader.readLine();
                thing = reader.readLine();
                gender = reader.readLine();

                if (name != null && farm != null && thing != null && gender != null) {
                    System.out.println("[Player Data] Name: " + name);
                    System.out.println("[Player Data] Farm Name: " + farm);
                    System.out.println("[Player Data] Favorite Thing: " + thing);
                    System.out.println("[Player Data] Gender: " + gender);
                } else {
                    System.out.println("File format error: Incomplete data block.");
                }
            }

            if (name != null && farm != null && thing != null && gender != null) {
                return new PlayerData(name, farm, thing, gender);
            } else {
                throw new RuntimeException("Failed to load valid player data.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load player data", e);
        }
    }

}
