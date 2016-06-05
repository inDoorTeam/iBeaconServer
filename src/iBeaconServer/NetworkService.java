package iBeaconServer;

import org.json.JSONException;
import org.json.JSONObject;

import DB.GuideDB;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkService implements Runnable {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    private static ArrayList<User> userList = new ArrayList<>();
    private User user;

    public NetworkService(int port, int poolSize) throws IOException{

        serverSocket = new ServerSocket(port);
        System.out.println("Server is created.");
        pool = Executors.newFixedThreadPool(poolSize);
    }
    public void run() {
        for (;;) {
            try {
                System.out.println("Waiting for client....");
                user = new User(serverSocket.accept());
               
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                JSONObject receiveObject;
                String receiveMessage = user.receive();
                if(receiveMessage == null)
                    continue;
                System.out.println(receiveMessage);
                receiveObject = new JSONObject(receiveMessage);
                String userName = receiveObject.getString(JSON.KEY_USER_NAME);
                String userPasswd = receiveObject.getString(JSON.KEY_USER_PWD);
                System.out.println(userName);
                System.out.println(userPasswd);

                GuideDB guideDB = GuideDB.getInstance();
                System.out.println(guideDB);
                Member member = guideDB.getMemberByUserAccountAndPsd(userName, userPasswd);

                if(member != null) {
                    user.setUserAccount(userName);
                    System.out.print(member.toString());
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put(JSON.KEY_USER_NAME, member.getAccount());
                    jsonObject.put(JSON.KEY_USER_PWD, member.getPwd());
                    System.out.println("Login Success");
                   // user.send(jsonObject.toString());

                    if(user.isConnected()) {
                        guideDB.setOnlineTag(1, userName);

                        pool.execute(new ClientHandler(userList, user));
                    }

                }
                else {

//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put(JSON.KEY_RESULT, false);
//                    jsonObject.put(JSON.KEY_RESULT_MESSAGE, JSON.KEY_LOGIN_FAIL);
//                    user.send(jsonObject.toString());

                    user.close();
                }
            } 
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                pool.shutdown();
                user.close();
            }
        }
    }
    
}
