package iBeaconServer;

import java.io.IOException;

public class Server{

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        int SOCKET_PORT = 8765;
        new Thread(new NetworkService(SOCKET_PORT, 100)).run();
    }
    
}