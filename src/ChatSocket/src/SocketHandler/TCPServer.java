package SocketHandler;

import Common.AppConstanst;
import Model.PJ.Message;
import Util.AppUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer {
    
    public interface TCPServerCallback{
        void onStartedServer();
        void onStoppedServer();
        void updateClientCount(int count);
        void onError(String e);
    }
    
    private TCPServerCallback callback;
    private ServerSocket serverSocket;
    private boolean isStop = false;
    private ArrayList<Socket> socketGenerated = new ArrayList<>();
	
    public TCPServer(TCPServerCallback callback){
        this.callback = callback;
    }
    
    
    public void start(){
        try {
            this.serverSocket = new ServerSocket(AppConstanst.SERVER_PORT);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    do {            
                        System.out.println("Wating for client!!!");
                        callback.onStartedServer();
                        Socket socket = null;
                        try {
                            socket = serverSocket.accept();
                        } catch (IOException ex) {
                            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        handleClient(socket);
                    } while (!isStop);
                }
            }).start();
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            callback.onError(ex.getMessage());
        }
        
    }
    
    public void stop(){
        isStop = true;
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            callback.onError(ex.getMessage());
        }
        callback.onStoppedServer();
    }
    
    private void handleClient(Socket socket){
        System.out.println("call this");
        socketGenerated.add(socket);
        callback.updateClientCount(socketGenerated.size());
        System.out.println("Generated a socket: " + socket.getPort());
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {                    
                    InputStream is;
                    BufferedReader receiver;
                    try {
                        is = socket.getInputStream();
                        receiver = new BufferedReader(new InputStreamReader(is));
                        Message msg = AppUtils.objectFromJSon(receiver.readLine(), Message.class);
                        sendMessage(msg.getPort(), msg.getMessage());
                    } catch (IOException ex) {
                        Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } while (true);
                
            }
        }).start();
    }
    
    private void sendMessage(int port, String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                socket = socketGenerated.stream().filter((sc) -> (sc.getPort() == port)).findFirst().get();
                if (socket != null){
                    OutputStream os;
                    BufferedWriter sender;
                    try {
                        os = socket.getOutputStream();
                        sender = new BufferedWriter(new OutputStreamWriter(os));
                        sender.write(message);
                        sender.newLine();
                        sender.flush();
                        System.out.println("Sent a message: " + message);
                    } catch (IOException ex) {
                        Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }
    
}

