/*
Program:  PolicyKitManager
Creator:  Ameer Reza Jalal
Date:     Started on August 19, 2016
Info:     This application will manage PolicyKit policies for the end user. gksudo is no longer
          included with Ubuntu, with the common argument being to use pkexec instead. However,
          using GUI applications with pkexec can be time-consuming if one has to write policies
          for them manually. Additonally, this tempts users to just use sudo, which can be a
          security concern.
*/
package policymanager;

import java.io.*;         //need to read and write to policy files
import org.gnome.gdk.*;   //needed for GUI
import org.gnome.gtk.*;   //needed for GUI
// javac -classpath $CLASSPATH:/usr/share/java/gtk-4.1.jar PolicyKitManager.java

public class PolicyKitManager{
  public static void main(String[] args){
    String policyPath = "/usr/share/polkit-1/actions/org.mycustom.policykit.policy";
    try{
      //First, detect if PolicyKitManager is being run with root. This is necessary.
      System.out.println("Checking if privileged user...");
      //String output = (p.getOutputStream());
      //String error = (p.getErrorStream());
      if (!isRoot()) //check if user is root
        throw new Exception("\nPlease run this program with administrator permissions.");
    }catch(Exception e){
      System.out.println(e.getMessage());
    }

  }

  private static boolean isRoot() throws IOException{
    //Check if current user is root, return true if so, return false otherwsie
    Process p = Runtime.getRuntime().exec("id -u"); //should output 0 if true
    try{
      BufferedReader br = new BufferedReader ( new InputStreamReader( p.getInputStream()));
      boolean iAmRoot = Integer.parseInt(br.readLine())==0;
      br.close(); // Release system resources connected to file
      if(iAmRoot)
        return true;
      else return false;
    }catch(IOException io){
      return false;
    }
  }
}
