import java.io.*;
import java.util.*;


public class Jsh {

  private String command;
  private String workingDirectory; // Current working directory
  private String homeDirectory; // Home Directory

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    boolean isContinue = true;

    while(isContinue){
      System.out.print("Jsh>");
      String userInput;
      if (sc.hasNextLine()) {      // not equal to EOF
        userInput = sc.nextLine();
        if (!userInput.equals("")) {
          Jsh pbe = new Jsh(userInput);
          pbe.runProcess();
        }
      } else {
        isContinue = false;
      }
      
    }
  }
  
  public Jsh(String s) {
    command = s;
    homeDirectory = System.getProperty("user.dir");
    workingDirectory = homeDirectory;
  }
  
  /**
   *  runProcess()
   *  
   */ 
  public void runProcess() {
    
    command = command.trim();
    
    // Split the command into an array of strings
    String[] tokens = command.split("[ \t\n]+");
    
    // ProcessBuilder is an object that knows how to create
    // external processes like "ls -la"
    ProcessBuilder pb = null;
    Process p = null;
    
    // Pass ProcessBuilder the command as an array of strings
    pb = new ProcessBuilder(tokens);
    pb.directory(new File(workingDirectory));
    // We set the process's initial working directory

    if (tokens[0].equals("cd")) {
      if (tokens.length == 1) {
        toDirectory(homeDirectory);
      } else if (tokens.length == 2) {
        toDirectory(tokens[1]);
      }
      
    } else {
      
      // System.err.println("Starting process " + tokens[0] + "...");
      
      try {
        p = pb.start();
      }
      catch (IOException e) {
        System.err.println("Uknown command: '" + tokens[0] + "'");
        return;
      }
      
      // Input/Output - from the perspective of the java program
      //    getErrorStream() gives an InputStream because the current
      //        process reads input from the created process's stderr
      //    getInputStream() gives an InputStream because the current
      //        process reads input from the created process's stdout
      //    getOutputStream() gives an OutputStream because the current
      //        process writes output into the created process's stdin
      // System.err.println("Establishing streams to communicate with process...");
      InputStream  sstderr = p.getErrorStream();
      InputStream  sstdout = p.getInputStream();
      OutputStream sstdin  = p.getOutputStream();
      
      // Typical to create BufferReader objects associated to all above streams
      BufferedReader stderr = new BufferedReader(new InputStreamReader(sstderr));
      BufferedReader stdout = new BufferedReader(new InputStreamReader(sstdout));
      BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(sstdin));
      
      // Communication can now happen when necessary, e.g., reading one line
      // from the process' stdout, and printing it out to jvm stdout
      try {
        String line;
        while((line = stdout.readLine()) != null) {
          System.out.println(line);
        }
        String line2;
        while((line2 = stderr.readLine()) != null) {
          System.err.println(line2);
        }
      }
      catch (IOException e) {
        System.err.println("IO Error");
      }
      
      // Wait for the process to terminate
      // System.err.println("Waiting for the process to complete");
      try {
        int returnValue = p.waitFor();
        // System.err.println("Process completed with return value" + returnValue);
      }
      catch (InterruptedException e) {
        // Do nothing for now...this is about threads and will
        // be explained later.
      }
      
      // close streams
      try {
        sstderr.close();
        sstdout.close();
        sstdin.close();
      }
      catch (IOException e) {
        System.err.println("IO Error");
      }
      
      // Destroy the process is necessary
      try {
        Thread.sleep(100);
      }
      catch (InterruptedException e) {
        // Do nothing for now...this is about threads and will
        // be explained later
      }
      
      p.destroy();
      
      return;
    }
  }

  /**
   *  toDirectory()
   *  @param String literal of directory name to change to.
   *  @return exit code 0 = pass, 1 = fail
   */
  public int toDirectory(String directory) {
    String directoryName; 

    if (directory.charAt(0) == '/') {
      directoryName = directory;
    } else {
      directoryName = workingDirectory + "/" + directory;
    }

    // We check if the directory to change to, actually exists
    File tempDirectory = new File(directoryName);
    if (!tempDirectory.isDirectory()) {
      System.err.println(directory + " is not a directory.");
      return 1;
    } else if (!tempDirectory.exists()) {
      System.err.println(directory + " doesn't exist.");
      return 1;
    } else if (!tempDirectory.canRead() || !tempDirectory.canExecute()) {
      System.err.println("permission denied.");
      return 1;
    }

    try {
      workingDirectory = tempDirectory.getCanonicalPath();
    } catch (IOException e) {
      System.err.println("I/O error.");
      return 1;
    }
    return 0;
  }
  
}