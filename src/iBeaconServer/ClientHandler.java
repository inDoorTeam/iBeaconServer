package iBeaconServer;

import DB.GuideDB;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bing on 2016/6/5.
 */
public class ClientHandler implements Runnable {
    private GuideDB guideDB;

    private User client;
    private User isCar = null;
    private ArrayList<User> clientList;
    private ArrayList<Item> itemList = new ArrayList<>();

    public ClientHandler(ArrayList<User> userList, User user) throws IOException {
        client = user;
        clientList = userList;
        guideDB = GuideDB.getInstance();
    }

    @Override
    public void run() {
        if(!clientList.contains(client))
            clientList.add(client);

        try {
            JSONObject receiveJSON = null;
            String receiveMessage;
            int status;
            while(client.isConnected() && !client.isClose()) {
                if(((receiveMessage = client.receive()) == null)) {
                    clientList.remove(client);
                    client.close();
                    break;
                }

                try {
                    receiveJSON = new JSONObject(receiveMessage);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                status = receiveJSON.getInt(JSON.KEY_STATE);
                System.out.println(receiveMessage);
                switch(status) {
                    case JSON.STATE_LOGOUT:
                        logoutHandler();
                        break;
                    case JSON.STATE_SEND_IBEACON:
                        String location = receiveJSON.getString(JSON.KEY_LOCATION);
                        client.setUserLocation(location);
                        System.out.println(client.getUserAccount() + " in " + location);
                        JSONObject locationChangeJSONObject = new JSONObject();

                        for(User u : clientList){
                            if( u.getBindingState() && !u.equals(client)) {
                                System.out.println(u.getUserAccount() + " is Binding");
                                locationChangeJSONObject.put(JSON.KEY_STATE, JSON.STATE_USER_MOVE);
                                locationChangeJSONObject.put(JSON.KEY_TARGET_LOCATION, location);
                                locationChangeJSONObject.put(JSON.KEY_RESULT, JSON.KEY_RESULT_MESSAGE);
                                u.send(locationChangeJSONObject.toString());
                                //System.out.println("send : " + locationChangeJSONObject.toString());
                                isCar = u;
                                break;
                            }
                        }
                        break;
                    case JSON.STATE_SEND_ITEM_IBEACON:
                        System.out.println(receiveJSON.toString());
                        int itemRssi = receiveJSON.getInt(JSON.KEY_RSSI);
                        int itemMinor = receiveJSON.getInt(JSON.KEY_MINOR);
                        String itemLocation = receiveJSON.getString(JSON.KEY_LOCATION);
                        boolean hasItem = false;
                        for(Item item : itemList) {
                            if(itemMinor == item.getMinor()){
                                if(itemRssi > item.getRssi()) {
                                    System.out.println("itemRssi:" + itemRssi);
                                    item.setRssi(itemRssi);
                                    item.setLocation(itemLocation);
                                }
                                hasItem = true ;
                            }
                        }
                        if(hasItem == false){
                            Item item =  GuideDB.getInstance().getItem(itemMinor) ;
                            item.setRssi(itemRssi) ;
                            item.setMinor(itemMinor) ;
                            item.setLocation(itemLocation) ;
                            itemList.add(item);
                        }

                        break;
                    case JSON.STATE_GET_ITEM_LOCATION:
                        String itemName = receiveJSON.getString(JSON.KEY_ITEM_NAME);
                        JSONObject sendItemLocaionJSONObject = new JSONObject();
                        sendItemLocaionJSONObject.put(JSON.KEY_STATE, JSON.STATE_GET_ITEM_LOCATION);
                        boolean hasItemLocation = false;
                        for( Item item : itemList ) {
                            if( itemName.equals(item.getItemName()) ) {
                                sendItemLocaionJSONObject.put(JSON.KEY_ITEM_LOCATION, item.getLocation());
                                hasItemLocation = true ;
                            }
                        }
                        if( hasItemLocation == false ){
                            sendItemLocaionJSONObject.put(JSON.KEY_ITEM_LOCATION, JSON.MESSAGE_NOLOATION);
                        }
                        client.send(sendItemLocaionJSONObject.toString());
                        break;
                    case JSON.STATE_LOGIN:
                        break;
                    case JSON.STATE_FIND_FRIEND:
                        JSONObject friendLocationJSONObject = new JSONObject();
                        JSONArray friendLocationJSONArray = new JSONArray();
                        JSONArray otherUserJSONArray = new JSONArray();
                        friendLocationJSONObject.put(JSON.KEY_STATE, JSON.STATE_FIND_FRIEND);
                        for(User u : clientList) { // 好友清單
                            if (!u.getUserAccount().equals(client.getUserAccount()) && GuideDB.getInstance().isFriend(client, u)) {
                                JSONObject JSONObject = new JSONObject();
                                JSONObject.put(JSON.KEY_USER_NAME, u.getUserAccount());
                                if(null == u.getUserLocation())
                                    JSONObject.put(JSON.KEY_LOCATION, JSON.MESSAGE_NOLOATION);
                                else
                                    JSONObject.put(JSON.KEY_LOCATION, u.getUserLocation());
                                friendLocationJSONArray.put(JSONObject);
                            }
                            else if(!u.getUserAccount().equals(client.getUserAccount())){ // 其他用戶
                                JSONObject JSONObject = new JSONObject();
                                JSONObject.put(JSON.KEY_USER_NAME, u.getUserAccount());
                                otherUserJSONArray.put(JSONObject);
                            }
                        }
                        friendLocationJSONObject.put(JSON.KEY_FRIEND_LIST, friendLocationJSONArray);
                        friendLocationJSONObject.put(JSON.KEY_OTHERUSER_LIST, otherUserJSONArray);
                        System.out.println(friendLocationJSONObject);
                        client.send(friendLocationJSONObject.toString());
                        break;
                    case JSON.STATE_CAR_BINDING:
                        boolean binding = receiveJSON.getBoolean(JSON.KEY_BINDING);
                        client.setBindingState(binding);
                        System.out.println("User : " + client.getUserAccount() + " BINDING : " + binding);
                        break;
                    case JSON.STATE_FIND_TARGET_LOCATION:
                        String targetLocation = isCar.getUserLocation();
                        JSONObject sendToClientJSONObject = new JSONObject();
                        sendToClientJSONObject.put(JSON.KEY_STATE, JSON.STATE_FIND_TARGET_LOCATION);
                        sendToClientJSONObject.put(JSON.KEY_TARGET_LOCATION, targetLocation);
                        client.send(sendToClientJSONObject.toString());
                        break;
                    case JSON.STATE_FIND_ITEM_LIST:
                        ArrayList<Item> userItemList = GuideDB.getInstance().getItemListByuser(client);
                        System.out.println(itemList);

                        JSONObject itemListJSONObject = new JSONObject();
                        JSONArray itemListJSONArray = new JSONArray();
                        itemListJSONObject.put(JSON.KEY_STATE, JSON.STATE_FIND_ITEM_LIST);
                        for(Item item : userItemList) {
                            JSONObject JSONObject = new JSONObject();
                            JSONObject.put(JSON.KEY_ITEM_NAME, item.getItemName());
                            JSONObject.put(JSON.KEY_USER_NAME, item.getOwner());
                            JSONObject.put(JSON.KEY_MINOR, item.getMinor());
                            JSONObject.put(JSON.KEY_LOCATION, item.getLocation());
                            itemListJSONArray.put(JSONObject);
                        }
                        itemListJSONObject.put(JSON.KEY_ITEM_LIST, itemListJSONArray);
                        System.out.println(itemListJSONObject);
                        client.send(itemListJSONObject.toString());
                        break;
                    case JSON.STATE_ASK_LOCATION_PERMISSION :

                        JSONObject JSONObject = new JSONObject();
                        JSONObject.put(JSON.KEY_STATE, JSON.STATE_ASK_LOCATION_PERMISSION);
                        JSONObject.put(JSON.KEY_USER_NAME, client.getUserAccount());
                        String otherUser = receiveJSON.getString(JSON.KEY_OTHER_USER);
                        for (User user: clientList){
                            if(user.getUserAccount().equals(otherUser)) {
                                user.send(JSONObject.toString());
                                System.out.println(otherUser);
                            }
                        }
                        break;
                    case JSON.STATE_RETURN_ASK_LOCATION_PERMISSION:
                        boolean isPermission = receiveJSON.getBoolean(JSON.KEY_OTHER_USER_PERMISION);
                        JSONObject sendOSONObject = new JSONObject();
                        sendOSONObject.put(JSON.KEY_OTHER_USER_PERMISION, isPermission);
                        sendOSONObject.put(JSON.KEY_STATE, JSON.STATE_RETURN_ASK_LOCATION_PERMISSION);
                        String username = receiveJSON.getString(JSON.KEY_USER_NAME);
                        if(isPermission){
                            sendOSONObject.put(JSON.KEY_TARGET_LOCATION, client.getUserLocation());
                        }
                        for (User user : clientList) {
                            if (user.getUserAccount().equals(username)) {
                                user.send(sendOSONObject.toString());
                            }
                        }
                        break;
                    case JSON.STATE_POST_ITEM:
                        String LostLocation = receiveJSON.getString(JSON.KEY_LOCATION);
                        String LostTime = receiveJSON.getString(JSON.KEY_LOST_TIME);
                        String LostCost = receiveJSON.getString(JSON.KEY_LOST_COST);
                        String LostDescription = receiveJSON.getString(JSON.KEY_LOST_DESCRIPTION);

                        System.out.println("Lost item Location: " + LostLocation + "time: " + LostTime
                            + "cost: " + LostCost + "description: " + LostDescription);

                        break;
                    case JSON.STATE_IS_MY_ITEM_OR_NOT:
                        int isItMyItemMinor = receiveJSON.getInt(JSON.KEY_MINOR);
                        System.out.println("Item minor :' " + isItMyItemMinor + " '\t");

                        boolean isornot = GuideDB.getInstance().isMyItem(client, isItMyItemMinor);

                        //JSONObject isornotJSONObject = new JSONObject();
                        //isornotJSONObject.put(JSON.KEY_STATE, JSON.STATE_RETURN_IS_OR_NOT_MY_ITEM);
                        //isornotJSONObject.put(JSON.KEY_OTHER_USER_PERMISION, isPermission);

                        break;
                    default:
                        System.out.println("");
                        //do nothing..
                        break;
                }

            }

        }
        catch(Exception e) {
            e.printStackTrace();

            if(clientList.contains(client)) {
                clientList.remove(client);
            }
            client.close();
        }
    }

    private void logoutHandler() throws JSONException {
        if(guideDB.setOnlineTag(0, client.getUserAccount()) > 0) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON.KEY_STATE, JSON.STATE_LOGOUT);
            jsonObject.put(JSON.KEY_RESULT, true);
            jsonObject.put(JSON.KEY_RESULT_MESSAGE, JSON.KEY_LOGOUT_SUCCESS);
            client.send(jsonObject.toString());
            clientList.remove(client);
            client.close();
            System.out.println("Logout Sueecss!");
        }
        else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON.KEY_RESULT, false);
            jsonObject.put(JSON.KEY_RESULT_MESSAGE, JSON.KEY_LOGOUT_FAIL);
            client.send(jsonObject.toString());
        }
    }
}