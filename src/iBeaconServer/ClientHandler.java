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
                switch(status) {
                    case JSON.STATE_LOGOUT:
                        logoutHandler();
                        break;
                    case JSON.STATE_SEND_IBEACON:
                        String location = receiveJSON.getString(JSON.KEY_LOCATION);
                        client.setUserLocation(location);
                        System.out.println(client.getUserAccount() + " in " + location);
                    case JSON.STATE_LOGIN:
                        break;
                    case JSON.STATE_FIND_FRIEND:
                        JSONObject friendLocationJSONObject = new JSONObject();
                        JSONArray friendLocationJSONArray = new JSONArray();
                        friendLocationJSONObject.put(JSON.KEY_STATE, JSON.STATE_FIND_FRIEND);
                        for(User u : clientList) {
                            if (!u.getUserAccount().equals(client.getUserAccount())) {
                                JSONObject JSONObject = new JSONObject();
                                JSONObject.put(JSON.KEY_USER_NAME, u.getUserAccount());
                                JSONObject.put(JSON.KEY_LOCATION, u.getUserLocation());
                                friendLocationJSONArray.put(JSONObject);
                            }
                        }
                        friendLocationJSONObject.put(JSON.KEY_USER_LIST, friendLocationJSONArray);
                        System.out.println(friendLocationJSONObject);
                        client.send(friendLocationJSONObject.toString());
                        break;
                    default:
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