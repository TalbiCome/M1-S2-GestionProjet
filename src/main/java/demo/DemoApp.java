package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
  public class DemoApp 
  {
  public static void main(String[] args) 
  {
    String hostname = getHostNameFromEnv(); 

    String[] newArgs = appendSpringProfileFromHostNameToArgs(args, hostname);

    SpringApplication.run(DemoApp.class, newArgs);
  }

  private static String[] appendSpringProfileFromHostNameToArgs(String[] args, String hostname)
  {
    String[] newArgs = new String[args.length + 1];
    System.arraycopy(args, 0, newArgs, 0, args.length);
    newArgs[args.length] = "--spring.profiles.active=" + hostname;
    return newArgs;
  }

  private static String getHostNameFromEnv()
  {
    String hostname;
    try {
      hostname = System.getenv().get("HOSTNAME");
      if(isHostNameOfAWorker(hostname)) // Fix: Use substring method to extract first 6 characters and use equals method for string comparison
        hostname = "worker";
    } catch (Exception e) {
      hostname = "worker";
    }
    return hostname;
  }

  private static boolean isHostNameOfAWorker(String hostname) 
  {
    return hostname.substring(0, 6).equals("worker");
  }
}