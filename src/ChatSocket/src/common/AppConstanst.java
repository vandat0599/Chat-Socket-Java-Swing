/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 *
 * @author user
 */
public class AppConstanst {
    public static int SERVER_PORT = 8080;
    public static int CLIENT_BASE_PORT = 1000;
    public static String ACCOUNT_FILE_NAME = "./account.txt";
    public static String getServerIP() throws UnknownHostException{
        return InetAddress.getLocalHost().getHostAddress();
    }
}
