package demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.model.Worker;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;


@Profile("loadBalancer")
@Controller
public class LoadBalancer {
  private List<Worker> workers;
  private boolean hasWorkerListBeenInitialized = false;

  private int index = 0;

  private String getNextWorkerUri() {
    //first init is needed because the order of creation of services can't be changed
    //and worker can be created after the Registery thus not appearing in the list when posted
    //still need to be sure that all worker are done with their init
    if(hasWorkerListBeenInitialized == false) 
    {
      initialiseWorkerListFromRegistery();
    }

    this.index = (this.index + 1) % this.workers.size();
    String uri = "http://" + this.workers.get(this.index).getHostname();
    return uri;
  }

  private void initialiseWorkerListFromRegistery() {
    RestClient restClient = RestClient.create();
    String r = restClient.get().uri("http://registery:8081/workers")
            .retrieve().body(String.class);
    ObjectMapper mapper = new ObjectMapper();
    try {
      this.workers = mapper.readValue(r, new TypeReference<List<Worker>>() {
      });
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    hasWorkerListBeenInitialized = true;
  }
    

  @GetMapping("/hello/{name}")
  public ResponseEntity<String> helloRequest(@PathVariable("name") String name) {
    RestClient restClient = RestClient.create();
    String uri = getNextWorkerUri() + ":8081/hello/" + name;
    String r = restClient.get().uri(uri)
      .retrieve().body(String.class);
    System.out.println("LoadBalancer is sending to worker: " + uri);
    System.out.println("LoadBalancer: " + name);
    return new ResponseEntity<>(r, HttpStatus.OK);
  }

  @GetMapping("/workersList")
  public ResponseEntity<?> workerListDisplay() {
    System.out.println("Workers: " + this.workers);
    return new ResponseEntity<List<Worker>>(this.workers, HttpStatus.OK);
  }

  @PostMapping("/workersList")
  public ResponseEntity<?> workerListUpdate(@RequestBody String workerList) {
    ObjectMapper mapper = new ObjectMapper();
    System.out.println("Received WorkerList: " + workerList);
    try {
      this.workers = mapper.readValue(workerList, new TypeReference<List<Worker>>() {});
    } catch (Exception e) {
      System.out.println("Error: " + e);
    }
    System.out.println("Workers: " + this.workers);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
}
