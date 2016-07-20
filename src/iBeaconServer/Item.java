package iBeaconServer;

/**
 * Created by ccccurly on 2016/7/15.
 */
public class Item {
    private String itemName;
    private String Owner;
    private int Minor;
    private int Rssi;
    private String location;


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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRssi(int rssi) {
        Rssi = rssi;
    }

    public int getRssi() {
        return Rssi;
    }
}
