package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApp {
    public static void main(String[] args) {
      // Get the hostname
      String hostname;
      try {
        hostname = System.getenv().get("HOSTNAME");
        if(hostname.substring(0, 6).equals("worker")) // Fix: Use substring method to extract first 6 characters and use equals method for string comparison
          hostname = "worker";
      } catch (Exception e) {
        hostname = "worker";
      }

      // Set the profile based on the hostname
      String[] newArgs = new String[args.length + 1];
      System.arraycopy(args, 0, newArgs, 0, args.length);
      newArgs[args.length] = "--spring.profiles.active=" + hostname;

      SpringApplication.run(DemoApp.class, newArgs);
  }
}