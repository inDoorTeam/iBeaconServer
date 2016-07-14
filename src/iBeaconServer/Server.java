package iBeaconServer;

import DB.GuideDB;

import java.io.IOException;

public class Server{

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        int SOCKET_PORT = 8766;
        new Thread(new NetworkService(SOCKET_PORT, 100)).run();
//        GuideDB.getInstance().selectFromItemList();
    }
    
}