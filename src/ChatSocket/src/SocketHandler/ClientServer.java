package SocketHandler;

import Controller.ChatForm;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import SocketHandler.tags.Decode;
import SocketHandler.tags.Tags;
import Model.Client;

public class ClientServer {

	private String username = "";
	private ServerSocket serverPeer;
	private int port;
	private boolean isStop = false;
        private ClientServerCallback callback;

	public ClientServer(String name, ClientServerCallback callback) throws Exception {
            username = name;
            port = Client.getPort();
            serverPeer = new ServerSocket(port);
            this.callback = callback;
            (new WaitPeerConnect()).start();
	}
	
	public void exit() throws IOException {
            isStop = true;
            serverPeer.close();
	}

	class WaitPeerConnect extends Thread {
            Socket connection;
            ObjectInputStream getRequest;

            @Override
            public void run() {
                super.run();
                while (!isStop) {
                    try {
                        connection = serverPeer.accept();
                        getRequest = new ObjectInputStream(connection.getInputStream());
                        String msg = (String) getRequest.readObject();
                        String name = Decode.getNameRequestChat(msg);
                        int res = callback.request("Account: " + name + " want to connect with you !", true);
                        ObjectOutputStream send = new ObjectOutputStream(connection.getOutputStream());
                        if (res == 1) {
                            send.writeObject(Tags.CHAT_DENY_TAG);
                        } else if (res == 0) {
                            send.writeObject(Tags.CHAT_ACCEPT_TAG);
                            new ChatForm(username, name, connection, port);
                        }
                        send.flush();
                    } catch (Exception e) {
                        break;
                    }
                }
                try {
                    serverPeer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}
        
        public interface ClientServerCallback{
            public int request(String msg, boolean type);
        }
}
