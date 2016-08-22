import java.io.*;
import org.gnome.gdk.*;
import org.gnome.gtk.*;
// javac -classpath $CLASSPATH:/usr/share/java/gtk-4.1.jar PolicyKitManager.java
// manage policies on this machine for security
public class PolicyKitManager{
  public static void main(String[] args){
    try{
      System.out.println("Checking if privileged user...");
      //String output = (p.getOutputStream());
      //String error = (p.getErrorStream());
      if (!isRoot())
        throw new Exception("\nPlease run this program with administrator permissions.");
      String policyPath = "/usr/share/polkit-1/actions/org.freedesktop.policykit.policy";
    }catch(Exception e){
      System.out.println(e.getMessage());
    }
  }
  static boolean isRoot() throws IOException{
    Process p = Runtime.getRuntime().exec("id -u");
    try{
      BufferedReader br = new BufferedReader ( new InputStreamReader( p.getInputStream()));
      if(Integer.parseInt(br.readLine())==0)
        return true;
      else return false;
    }catch(IOException io){
      return false;
    }
  }
}
