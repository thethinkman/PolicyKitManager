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
/////////////////////////////////////need to fix numActions
import java.io.*;           //need to read and write to policy files
// jar cfe PolicyKitManager.jar policykitmanager.PolicyKitManager policykitmanager/ LICENSE org.policykitmanager.policykit.policy README.md
public class PolicyKitManager{
  public static void main(String[] args){
    try{
      boolean correctArgs = false;        //are the arguments correct?
      if (args.length>0){                 //there must be arguments
        if(args[0].equals("add")){        //for add
          if(args.length==3){             //must have length of 3
            if(args[1].equals("gui") || args[1].equals("nogui")){ //must specify gui or not
              if((new File(args[2])).isFile()){                   //path must lead to file
                correctArgs = true;
              }else{
                System.out.println("Problem: the filepath entered is incorrect.");
              }
            }
          }
        }
      }
      if (!correctArgs){                //if bad args, express following message
        System.out.println("No arguments given correctly.");
        System.out.println("Run as follows:");
        System.out.println("(1)To add policy, polkitman add [gui/nogui] [path to program]");
        System.out.println("Policy listing and removal is coming in future versions. ");
        throw new Exception("Please run with the correct arguments to use.");
      }
      //First, detect if PolicyKitManager is being run with root. This is necessary.
      if (!isRoot()) //check if user is root
        throw new IOException("Please run with sudo to use.");
      //Check if policy file exists, if not, create it
      File policy = new File("/usr/share/polkit-1/actions/org.policykitmanager.policykit.policy");
      File template = new File("org.policykitmanager.policykit.policy");  //use to make new policy
      File tempFile = new File("/usr/share/polkit-1/actions/org.policykitmanager.policykit.policy.tmp");
      tempFile.deleteOnExit();  //delete temporary file when JVM exits
      if (!policy.isFile())
        if (!copyFile(template,policy,true))
          throw new IOException("Failed to create policy file in /usr/share/polkit-1/actions/.");
      if(!copyFile(policy,tempFile,false))
            throw new IOException("Failed to create temporary file, exiting...");
      if(args[0].equals("add")){
        if(!writeAction(tempFile,args[1],args[2]))
          throw new IOException("Failed to write action to policy file.");
      }//else if (args[0].equals("rm")) {

        //}
      if (!copyFile(tempFile,policy,true))
        throw new IOException("Failed to create policy file from tempFile.");

    }catch(IOException io){
      System.out.println(io.getMessage()+" IOException from main catch");     //print Exception message to screen
    }catch(Exception e){
      System.out.println(e.getMessage()+" Other Exception from main catch");
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
    try{
      if(!copyFrom.isFile())  //if there's no file to copy, the installation is broken
        throw new Exception("\nProblem: No File to copy.");
      if(copyTo.isFile())     //check if a file exists at the destination
        if(!copyTo.delete())   //if a file does exist at the destination, delete it
          throw new Exception("\nProblem: Could not delete destination file.");
      bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(copyTo)));
      br = new BufferedReader(new InputStreamReader(new FileInputStream(copyFrom)));
      String tempS = br.readLine(); //temporary string to hold current line
      numActions = 0;               //set count of actions to 0
      while(tempS!=null){           //exit loop at end of file
        if (tempS.startsWith("<action id="))
          numActions++;             //add to count of actions
        bw.write(tempS+"\n");       //write currently read line to output file
        bw.flush();                 //flush out rest of stream just in case
        tempS = br.readLine();      //load next line for loop
        if (!copyFull)
          if(tempS.startsWith("</policy"))
            break;
      }
      return true;
    }catch(Exception x){
      System.out.println(x.getMessage()+"from copyFile catch");     //print Exception message to screen
      return false; //if there's a problem, report that policy wasn't made successfully
    }finally{
      br.close();
      bw.close();
    }
  }

  private static boolean writeAction(File tempFile,String gui,String path) throws IOException{
    //Add action to existing policy
    FileWriter bw = null;
    try{
      if(!tempFile.isFile())     //check if a file exists at the destination
          throw new IOException("Seems that there's no file that exists at the destination.");
      bw = new FileWriter(tempFile.getCanonicalPath(),true);

      bw.flush();
      bw.write("\n\t<action id=\"org.policykitmanager.policykit.pkexec.polkitman"+(numActions+1)+"\">");
      bw.flush();
      bw.write("\n\t\t<description>Run program with elevated permissions</description>");
      bw.flush();
      bw.write("\n\t\t<message>Authentication is required to run this program</message>");
      bw.flush();
      bw.write("\n\t\t<defaults>");
      bw.flush();
      bw.write("\n\t\t\t<allow_any>no</allow_any>");
      bw.flush();
      bw.write("\n\t\t\t<allow_inactive>no</allow_inactive>");
      bw.flush();
      bw.write("\n\t\t\t<allow_active>auth_admin_keep</allow_active>");
      bw.flush();
      bw.write("\n\t\t</defaults>");
      bw.flush();
      bw.write("\n\t\t<annotate key=\"org.freedesktop.policykit.exec.path\">"+path+"</annotate>");
      bw.flush();
      if (gui.equals("gui"))
        bw.write("\n\t\t<annotate key=\"org.freedesktop.policykit.exec.allow_gui\">TRUE</annotate>");
      bw.flush();
      bw.write("\n\t</action>\n\n</policyconfig>\n");
      bw.flush();
      return true;
    }catch(Exception x){
      System.out.println(x.getMessage()+"from writeAction catch");     //print Exception message to screen
      return false; //if there's a problem, report that policy wasn't made successfully
    }finally{
      bw.close();
    }
  }

}
