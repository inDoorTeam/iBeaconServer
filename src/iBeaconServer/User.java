package iBeaconServer;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import org.json.JSONException;
import org.json.JSONObject;

import DB.DBConnect;


public class User {
    
    
//    private String username = "";
//    private boolean LoginState = false;
//
//    DataInputStream fromClient;//宣告一個讀取Client傳送過來的字串物件
//    String fromClientString;
//    ArrayList<DataOutputStream> clientOutputStreams = new ArrayList<DataOutputStream>();
//    protected int count ;
//    protected Socket clientSocket;
//    public User(Socket serverSocket , ArrayList<DataOutputStream> clientOutputStreams ){
//        this.clientOutputStreams = clientOutputStreams;
//        this.clientSocket = serverSocket;
//        try{
//            fromClient = new DataInputStream(serverSocket.getInputStream());//宣告一個將server端資料寫出的變數
//        }
//        catch(Exception e){
//            System.out.println(e.toString());
//        }
//    }
//
//    public void run(){
//        while (true) {
//            try{
//                fromClientString = fromClient.readUTF();
//                JSONObject fromClientJSONObject = new JSONObject(fromClientString);
//                System.out.println("---------------------------------");
//                switch( fromClientJSONObject.getInt(JSON.KEY_STATE) ) {
//                    case JSON.STATE_SEND_IBEACON:
//                        System.out.println("Rssi = " + fromClientJSONObject.get(JSON.KEY_RSSI));
//                        System.out.println("Uuid = " + fromClientJSONObject.get(JSON.KEY_UUID));
//                        System.out.println("Major = " + fromClientJSONObject.get(JSON.KEY_MAJOR));
//                        System.out.println("Minor = " + fromClientJSONObject.get(JSON.KEY_MINOR));
//                        break;
//                    case JSON.STATE_LOGIN:
//                        String name = fromClientJSONObject.get(JSON.KEY_USER_NAME).toString();
//                        String pwd = fromClientJSONObject.get(JSON.KEY_USER_PWD).toString();
//                        System.out.println("User Name = " + name);
//                        System.out.println("PassWord = " + pwd);
//
//                        break;
//                }
//                System.out.println("---------------------------------");
//            }
//            catch (IOException e) {
//                // serverSocket.close();
//                break;
//            }
//            catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
//    }
    
    

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String userAccount;
    private String location;
    private boolean isClose = false;
    private boolean isBinding = false;

    public User(Socket socket) throws IOException {
        this.socket = socket;
        isClose = false;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        if(socket.isConnected()){
            try{
                System.out.println("Connect from = " + socket.getInetAddress().getHostAddress() );
                System.out.println("Client port = " + socket.getPort()) ;
            }
            catch (Exception e) {
                socket.close();
            }
        }
    }

    public void setUserAccount(String account) {
        userAccount = account;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserLocation(String location) {
        this.location = location; 
    }

    public String getUserLocation() {
        return location;
    }

    public void send(String message) {
        if(!socket.isOutputShutdown()) {
            try {
                output.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void send(Socket otherSocket, String message) {
        try {
            PrintWriter otherOutput = new PrintWriter(new OutputStreamWriter(otherSocket.getOutputStream(), "UTF-8"), true);
            otherOutput.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() throws IOException {
        if(!socket.isInputShutdown())
            return input.readUTF();
        else
            return null;
    }

    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClose() {
        return socket.isClosed();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public String getIpAddress() {
        return socket.getInetAddress().toString();
    }
    public String toString(){
        return userAccount;
    }
    public boolean getBindingState(){
        return isBinding;
    }
    public void setBindingState(boolean binding){
        isBinding = binding;
    }
}