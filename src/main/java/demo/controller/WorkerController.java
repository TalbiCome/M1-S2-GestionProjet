package demo.controller;

import demo.model.Worker;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

@Profile("worker")
@Controller
@EnableScheduling
public class WorkerController {
  private String hostname;
  private Worker self;

  @EventListener(ApplicationReadyEvent.class)
  public void afterStartup()
  {
    try{
      this.hostname = System.getenv().get("HOSTNAME");
      this.self = new Worker(hostname);
      sendPostRequest("http://registery:8081/workers", this.self);
    }catch (Exception e){
      onFailRelaunchStartUpInit();
    }
  }

  private void onFailRelaunchStartUpInit() 
  {
    try {
      TimeUnit.MILLISECONDS.sleep(100);
    } catch (InterruptedException e1) {
      afterStartup();
    }
  }

  @Scheduled(fixedRate = 60000)
  public void manifestation()
  {
    System.out.println(hostname + " is manifesting to the registery.");
    sendPostRequest("http://registery:8081/workers", this.self);
  }

  @GetMapping("/hello/{name}")
  public ResponseEntity<String> helloRequest(@PathVariable("name") String name) {
    String res = "Hello " + name + ", I am " + hostname;
    System.out.println("LoadBalancer: " + name);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  private void sendPostRequest(String uri, Object obj) 
  {
    RestClient restClient = RestClient.create();
    restClient.post()
    .uri(uri)
    .contentType(MediaType.APPLICATION_JSON)
    .body(obj).retrieve();
  }
}
