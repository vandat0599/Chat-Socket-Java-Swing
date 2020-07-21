/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class AppUtils {
    
    public static void showPopUpError(){
        
    }
    
    public static<T> String objectToJSon(T t){
        return new Gson().toJson(t);
    }
    
    public static<T> T objectFromJSon(String json, Class<T> t){
        return new Gson().fromJson(json, t);
    }
    
    public static<T> void saveObject(T t, String fileName){
        String jsonString = objectToJSon(t);
    	File file= new File(fileName);
    	try {
            file.createNewFile();
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file, true);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                bw.write(jsonString);
                bw.write("\n");
                bw.close();
                System.out.print("save successful");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public static<T> T getObject(Class<T> t, String fileName) {
            String dataString = null;
            try {
                File myObj = new File(fileName);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    dataString = myReader.nextLine();
                    System.out.println("data get: " + dataString);
                }
                    myReader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            return objectFromJSon(dataString, t);
	}
        
        public static<T> ArrayList<T> getArrayObject(Class<T> t, String fileName) {
            ArrayList<T> result = new ArrayList<>();
            try {
                String dataString = null;
                File myObj = new File(fileName);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    dataString = myReader.nextLine();
                    result.add(objectFromJSon(dataString, t));
                }
                myReader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            return result;
	}
        
        public static void showAlert(String message){
            JOptionPane.showMessageDialog(null, message);
        }
}
