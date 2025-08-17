package com.Interdevs.stardewValley;

import java.util.ArrayList;

public class InventoryItem {
    private String name;
    private String type;// e.g. "seed", "tool", "crop"
    private int numberOfSeads;
    private int quantityOfCrop;

    public InventoryItem(String name, String type) {
        this.name = name;
        this.type = type;
        if (type.equals("seed")) {
            this.numberOfSeads = 10;
        } else if (type.equals("crop")) {
            this.quantityOfCrop = 0;
        }
    }

    public int getQuantityOfCrop() {
        return quantityOfCrop;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getNumberOfSeads(){
        return numberOfSeads;
    }

    public void incrementQuantity(){
        quantityOfCrop++;
    }


    public void setNumberOfSeads(int i) {
        numberOfSeads = i;
    }
}
