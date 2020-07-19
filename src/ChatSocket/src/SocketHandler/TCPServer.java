package SocketHandler;

import Common.AppConstanst;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer {
    
    public interface ServerHandlerCallback{
        
    }
    
    private ServerHandlerCallback callback;
    private ServerSocket serverSocket;
	
    public TCPServer(ServerHandlerCallback callback){
        this.callback = callback;
        try {
            this.serverSocket = new ServerSocket(AppConstanst.SERVER_PORT);
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}

