package demo.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import demo.model.Worker;


@Profile("service")
@Controller
@RequestMapping("/service")
public class Service {
  
  @GetMapping("/hello/{name}")
  public ResponseEntity<String> helloRequest(@PathVariable("name") String name) {
    RestClient restClient = RestClient.create();
    String r = restClient.get().uri("http://loadBalancer:8081/hello/" + name)
      .retrieve().body(String.class);
    System.out.println("Service: " + name);
    System.out.println("Service Res: " + name);
    return new ResponseEntity<>(r, HttpStatus.OK);
  }
}
