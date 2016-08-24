/*
Program:  PolicyKitManager
Creator:  Ameer Reza Jalal
Date:     Started on August 19, 2016
Info:     This application will manage PolicyKit policies for the end user. gksudo is no longer
          included with Ubuntu, with the common argument being to use pkexec instead. However,
          using GUI applications with pkexec can be time-consuming if one has to write policies
          for them manually. Additonally, this tempts novice users to just use sudo, which can be a
          security concern. For security reasons, this app only manages policies it creates.
*/
package policykitmanager;

import java.io.*;           //need to read and write to policy files
// javac -classpath $CLASSPATH:/usr/share/java/gtk-4.1.jar policykitmanager/PolicyKitManager.java
// jar cfe PolicyKitManager.jar policykitmanager.PolicyKitManager policykitmanager/ LICENSE org.policykitmanager.policykit.policy README.md
public class PolicyKitManager{
  public static void main(String[] args){
    BufferedReader policyReader = null;   //create BufferedReader for reading policy file
    BufferedWriter writeTemp = null;
    String newActionPath = null;          //path to new action
    try{
      if (args.length==0){                //if no arg given, will create policy file if not there
        System.out.println("No argument given. Will check policy file and perform setup.");
      }
      //First, detect if PolicyKitManager is being run with root. This is necessary.
      if (!isRoot()) //check if user is root
        throw new IOException("Please run with sudo to use.");
      //Check if policy file exists, if not, create it
      System.out.println("check if policy exists, if not, creating it...");
      File policy = new File("/usr/share/polkit-1/actions/org.policykitmanager.policykit.policy");
      File template = new File("org.policykitmanager.policykit.policy");  //use to make new policy
      File tempFile = new File("/usr/share/polkit-1/actions/org.policykitmanager.policykit.policy.tmp");
      if (!policy.isFile())
        if (!copyFile(template,policy,true))
          throw new IOException("Failed to create policy file in /usr/share/polkit-1/actions/.");
      //read current policy file
      System.out.println("create policyReader and writeTemp, read from it");
      policyReader = new BufferedReader(new InputStreamReader(new FileInputStream(policy)));
      writeTemp = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)));
      String tempS = null;
      if(args.length>0){
        if(args[0].equals("add")){
          System.out.println("making temporary file for adding...");
          if(!copyFile(policy,tempFile,false))
            throw new IOException("Failed to create temporary file, exiting...");
          writeTemp.write("\n<action id=\"org.policykitmanager.policykit.polkitman"+numActions+"\">");

        }else if (args[0].equals("rm")) {

        }
      }
      System.out.println("Made it to end of main try.");
    }catch(IOException io){
      System.out.println(io.getMessage()+" IOException from main catch");     //print Exception message to screen
    }catch(Exception e){
      System.out.println(e.getMessage()+" Other Exception from main catch");
    }
    finally{
      try{
        policyReader.close();                 //cleanup by closing open BufferedReader
        writeTemp.close();                    //cleanup by closing open BufferedWriter
      }catch(Exception e){
        System.out.println(e.getMessage()+"from main finally");
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
      System.out.println(io.getMessage()+"from isRoot()");     //print Exception message to screen
      return false;   //if there's a problem, report that user isn't root
    }finally{
      br.close();
    }
  }

  private static int numActions = 0;          //number of actions counted
  private static boolean copyFile(File copyFrom, File copyTo, boolean copyFull) throws IOException{
    //Copy file to new location; doesn't use Files built-in copy because it is useful to not copy
    //the end of a policy file for purposes of adding to it.
    BufferedWriter bw = null;
    BufferedReader br = null;
    System.out.println("right before try for copyFile");
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
      int numActions = 0;           //set count of actions to 0
      System.out.println("right before while(tempS!=copyUntil)");
      while(tempS!=copyUntil){      //exit loop at end of file
        if (tempS.startsWith("<action id="))
          numActions++;             //add to count of actions
        bw.write(tempS+"\n");       //write currently read line to output file
        bw.flush();                 //flush out rest of stream just in case
        tempS = br.readLine();      //load next line for loop

      }
      System.out.println("Did copy Successfully");
      return true;
    }catch(Exception x){
      System.out.println(x.getMessage()+"from copyFile catch");     //print Exception message to screen
      return false; //if there's a problem, report that policy wasn't made successfully
    }finally{
      br.close();
      bw.close();
    }
  }
}
