package model;

import controller.ChatForm;
import controller.ChatForm;
import socket.ClientServer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import socket.tags.Decode;
import socket.tags.Encode;
import socket.tags.Tags;

/**
 *
 * @author user
 */
public class Client implements ClientServer.ClientServerCallback{

    public static ArrayList<Peer> clientarray = null;
    private ClientServer server;
    private InetAddress IPserver;
    private int portServer = 8080;
    private String nameUser = "";
    private boolean isStop = false;
    private static int portClient = 10000; 
    private int timeOut = 10000;  //time to each request is 10 seconds.
    private Socket socketClient;
    private ObjectInputStream serverInputStream;
    private ObjectOutputStream serverOutputStream;
    private ClientCallback callback;


    public Client(String arg, int arg1, String name, String dataUser, ClientCallback callback) throws Exception {
        IPserver = InetAddress.getByName(arg);
        nameUser = name;
        portClient = arg1;
        clientarray = Decode.getAllUser(dataUser);
        this.callback = callback;
        new Thread(new Runnable(){
                @Override
                public void run() {
                    updateFriend();
                }
        }).start();
        server = new ClientServer(nameUser, this);
        (new Request()).start();
    }

    public static int getPort() {
        return portClient;
    }

    public void request() throws Exception {
        socketClient = new Socket();
        SocketAddress addressServer = new InetSocketAddress(IPserver, portServer);
        socketClient.connect(addressServer);
        String msg = Encode.sendRequest(nameUser);
        serverOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
        serverOutputStream.writeObject(msg);
        serverOutputStream.flush();
        serverInputStream = new ObjectInputStream(socketClient.getInputStream());
        msg = (String) serverInputStream.readObject();
        serverInputStream.close();
        //		just for test
        clientarray = Decode.getAllUser(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateFriend();
            }
        }).start();
    }

    @Override
    public int request(String msg, boolean type) {
        return callback.request(msg, type);
    }

    public class Request extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isStop) {
                try {
                    Thread.sleep(timeOut);
                    request();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void intialNewChat(String IP, int host, String guest) throws Exception {
        final Socket connclient = new Socket(InetAddress.getByName(IP), host);
        ObjectOutputStream sendrequestChat = new ObjectOutputStream(connclient.getOutputStream());
        sendrequestChat.writeObject(Encode.sendRequestChat(nameUser));
        sendrequestChat.flush();
        ObjectInputStream receivedChat = new ObjectInputStream(connclient.getInputStream());
        String msg = (String) receivedChat.readObject();
        if (msg.equals(Tags.CHAT_DENY_TAG)) {
                callback.request("Your friend denied connect with you!", false);
                connclient.close();
                return;
        }
        //not if
        new ChatForm(nameUser, guest, connclient, portClient);

    }

    public void exit() throws IOException, ClassNotFoundException {
        isStop = true;
        socketClient = new Socket();
        SocketAddress addressServer = new InetSocketAddress(IPserver, portServer);
        socketClient.connect(addressServer);
        String msg = Encode.exit(nameUser);
        serverOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
        serverOutputStream.writeObject(msg);
        serverOutputStream.flush();
        serverOutputStream.close();
        server.exit();
    }

    public void updateFriend(){
        int size = clientarray.size();
        callback.resetList();
        //while loop
        int i = 0;
        while (i < size) {
                if (!clientarray.get(i).getName().equals(nameUser))
                        callback.updateFriend(clientarray.get(i).getName());
                i++;
        }
    }

    public interface ClientCallback{
        public void updateFriend(String msg);
        public void resetList();
        public int request(String msg, boolean type);
    }
}