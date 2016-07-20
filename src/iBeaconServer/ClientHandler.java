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
                        ArrayList<Item> itemList = GuideDB.getInstance().getItemListByuser(client);
                        System.out.println(itemList);

                        JSONObject itemListJSONObject = new JSONObject();
                        JSONArray itemListJSONArray = new JSONArray();
                        itemListJSONObject.put(JSON.KEY_STATE, JSON.STATE_FIND_ITEM_LIST);
                        for(Item item : itemList) {
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

                        client.send(JSONObject.toString());

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