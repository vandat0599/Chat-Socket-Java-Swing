/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SocketHandler;

/**
 *
 * @author user
 */
public class TCPClient {
    public interface TCPClientCallback{
        
    }
    
    private TCPClientCallback callback;
    
    
    public TCPClient(TCPClientCallback callback){
        this.callback = callback;
    }
}
