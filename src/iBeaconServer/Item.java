package iBeaconServer;

import java.util.ArrayList;

/**
 * Created by ccccurly on 2016/7/15.
 */
public class Item {
    private String itemName;
    private String Owner;
    private int Minor;
    private int Rssi;
    private String location;
    private String prelocation = null;
    private boolean flag = false; //flag = false, hold.true : lose.
    private static ArrayList<Item> lostItem;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getMinor() {
        return Minor;
    }

    public void setMinor(int minor) {
        Minor = minor;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public String getPreLocation() {
        return prelocation;
    }


    public void setLocation(String location) {
        this.location = location;
    }

    public void setPreLocation(String location) {
        this.prelocation = location;
    }


    public void setRssi(int rssi) {
        Rssi = rssi;
    }

    public int getRssi() {
        return Rssi;
    }

    public void setFlag (boolean newFlag){
        flag = newFlag;
    }
    public boolean getFlag(){
        return flag;
    }

    public static void setLostItem(Item newLostItem){
        if (lostItem == null){
            lostItem = new ArrayList<>();
        }
        lostItem.add(newLostItem);
    }
    public static ArrayList<Item> getLostItem(){
        if(lostItem == null){
            lostItem = new ArrayList<>();
        }
        return lostItem;
    }

    public static void removeLostItem(Item i){
        lostItem.remove(i);
    }
}
