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
package policykitmanager;

import java.io.*;           //need to read and write to policy files
import org.gnome.gdk.*;     //needed for GUI
import org.gnome.gtk.*;     //needed for GUI
// javac -classpath $CLASSPATH:/usr/share/java/gtk-4.1.jar PolicyKitManager.java

public class PolicyKitManager{
  public static void main(String[] args){
    BufferedReader policyReader = null;
    try{
      //First, detect if PolicyKitManager is being run with root. This is necessary.
      System.out.println("Checking if privileged user...");
      //String output = (p.getOutputStream());
      //String error = (p.getErrorStream());
      if (!isRoot()) //check if user is root
        throw new Exception("\nPlease run with sudo to use. Otherwise, try polkit-explorer.");
      System.out.println("Great!");
      //Check if policy file exists, if not, create it
      File policy = new File("/usr/share/polkit-1/actions/org.policykitmanager.policykit.policy");
      File template = new File("org.policykitmanager.policykit.policy");  //use to make new policy
      if (!policy.isFile())
        if (!copyFile(template,policy,true))
          throw new Exception("\nFailed to create policy file in /usr/share/polkit-1/actions/.");
      //read current policy file
      policyReader = new BufferedReader(new InputStreamReader(new FileInputStream(policy)));

    }catch(Exception e){
      System.out.println(e.getMessage());
    }finally{
      try{
        policyReader.close();
      }catch(Exception e){
        System.out.println(e.getMessage());
      }
    }
  }

  private static boolean isRoot() throws IOException{
    //Check if current user is root, return true if so, return false otherwsie
    Process p = Runtime.getRuntime().exec("id -u"); //should output 0 if true
    BufferedReader br = null;
    try{
      br = new BufferedReader ( new InputStreamReader( p.getInputStream()));
      boolean iAmRoot = Integer.parseInt(br.readLine())==0; //checks if process returns 0 to stream
      br.close(); // Release system resources connected to file
      if(iAmRoot)
        return true;
      else return false;
    }catch(IOException io){
      System.out.println(io.getMessage());
      return false;   //if there's a problem, report that user isn't root
    }finally{
      br.close();
    }
  }

  private static boolean copyFile(File copyFrom, File copyTo, boolean copyFull) throws IOException{
    //Copy file to new location; doesn't use Files built-in copy because it is useful to not copy
    //the end of a policy file for purposes of adding to it.
    BufferedWriter bw = null;
    BufferedReader br = null;
    try{
      if(!copyFrom.isFile())  //if there's no file to copy, the installation is broken
        throw new Exception("\nProblem: No File to copy.");
      if(copyTo.isFile())     //check if a file exists at the destination
        if(copyTo.delete())   //if a file does exist at the destination, delete it
          System.out.println("Overwriting file...");
      bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(copyTo)));
      br = new BufferedReader(new InputStreamReader(new FileInputStream(copyFrom)));
      String tempS = br.readLine(); //temporary string to hold current line
      String copyUntil = null;      //copy until end of file
      if (!copyFull)                //if copyFull is false, don't close policyconfig xml bracket
        copyUntil = "</policyconfig>";
      while(tempS!=copyUntil){      //exit loop at end of file
        bw.write(tempS+"\n");       //write currently read line to output file
        bw.flush();                 //flush out rest of stream just in case
        tempS = br.readLine();      //load next line for loop
      }
      return true;
    }catch(Exception x){
      System.out.println(x.getMessage());
      return false; //if there's a problem, report that policy wasn't made successfully
    }finally{
      br.close();
      bw.close();
    }
  }
}
