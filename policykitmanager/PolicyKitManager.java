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
      if (!policy.isFile())
        if (!makePolicy(policy))
          throw new Exception("\nFailed to create policy file in /usr/share/polkit-1/actions/.");
      System.out.println("PolicyKitManager Policy found!");
      //BufferedReader policyReader = new BufferedReader(
      //  new InputStreamReader(new FileInputStream(policy)));
      //policyReader.close();
    }catch(Exception e){
      System.out.println(e.getMessage());
    }
  }

  private static boolean isRoot() throws IOException{
    //Check if current user is root, return true if so, return false otherwsie
    Process p = Runtime.getRuntime().exec("id -u"); //should output 0 if true
    try{
      BufferedReader br = new BufferedReader ( new InputStreamReader( p.getInputStream()));
      boolean iAmRoot = Integer.parseInt(br.readLine())==0; //checks if process returns 0 to stream
      br.close(); // Release system resources connected to file
      if(iAmRoot)
        return true;
      else return false;
    }catch(IOException io){
      System.out.println(io.getMessage());
      return false;   //if there's a problem, report that user isn't root
    }
  }

  private static boolean makePolicy(File policy) throws IOException{
    //Create Policy file
    File toCopy = new File("org.policykitmanager.policykit.policy");  //copy file from install
    try{
      if(!toCopy.isFile())  //if there's no file to copy, the installation is broken
        throw new Exception("\nFiles missing from PolicyKitManager installation.");
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(policy)));
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(toCopy)));
      String tempS = br.readLine(); //temporary string to hold current line
      while(tempS!=null){           //exit loop at end of file
        bw.write(tempS+"\n");       //write currently read line to policy file
        bw.flush();                 //flush out rest of stream just in case
        tempS = br.readLine();      //load next line for loop
      }
      br.close();
      bw.close();
      System.out.println("Successfully created file in /usr/share/polkit-1/actions/");
      return true;
    }catch(Exception x){
      System.out.println(x.getMessage());
      return false; //if there's a problem, report that policy wasn't made successfully
    }
  }
}
