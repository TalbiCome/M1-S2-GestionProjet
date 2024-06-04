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

  private int index = 0;

  @GetMapping("/hi")
  public ResponseEntity<String> hello() throws JsonMappingException, JsonProcessingException 
  {
    RestClient restClient = RestClient.create();
    String r = restClient.get().uri("http://registery:8081/workers")
            .retrieve().body(String.class);
    ObjectMapper mapper = new ObjectMapper();
    this.workers = mapper.readValue(r, new TypeReference<List<Worker>>() {
    });

    this.index = (this.index + 1) % this.workers.size();
    String uri = "http://" + this.workers.get(this.index).getHostname() + ":8081/hello2";
    String rw = restClient.get().uri(uri).retrieve().body(String.class);

    return new ResponseEntity<>(rw, HttpStatus.OK);
  }

  private String getNextWorkerUri() {

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

    this.index = (this.index + 1) % this.workers.size();
    String uri = "http://" + this.workers.get(this.index).getHostname();
    return uri;
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
