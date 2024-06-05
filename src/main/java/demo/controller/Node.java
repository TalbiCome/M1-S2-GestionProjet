package demo.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Profile("Node")
@Controller
public class Node {

  private String hostname;

  @GetMapping("/hello")
  public void generateWorkers(@RequestParam int nbWorkers) {
    }
  
}
