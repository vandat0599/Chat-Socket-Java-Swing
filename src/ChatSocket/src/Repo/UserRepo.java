/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Repo;

import Common.AppConstanst;
import Model.User;
import Util.AppUtils;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class UserRepo {
    public static ArrayList<User> getAllAccounts(){
        return AppUtils.getArrayObject(User.class, AppConstanst.ACCOUNT_FILE_NAME);
    }
    
    public static boolean isExistUser(User user){
        return AppUtils.getArrayObject(
                User.class, 
                AppConstanst.ACCOUNT_FILE_NAME)
                .stream()
                .filter(u -> u.getUserName().equals(user.getUserName()) && u.getPassWord().equals(user.getPassWord())
                ).count() > 0;
    }
}
