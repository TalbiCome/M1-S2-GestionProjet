package demo.controller;

import demo.model.Worker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Profile("registery")
@EnableScheduling
@RestController
@RequestMapping("/workers")
public class RegisteryController 
{
  @Autowired
  private WorkerRepository workersRepo;
  private HashMap<String, LocalDateTime> manifestationMap = new HashMap<>();

  @Transactional
  @GetMapping()
  public ResponseEntity<Object> getUsers() 
  {
    Stream<Worker> s = workersRepo.streamAllBy();
    return new ResponseEntity<>(s.toList(), HttpStatus.OK);
  }

  @GetMapping("/map")
  public ResponseEntity<Object> getManifestationMap() 
  {
    return new ResponseEntity<>(manifestationMap.toString(), HttpStatus.OK);
  }
  
  @PostMapping()
  public ResponseEntity<Worker> put(@RequestBody Worker user) 
  {
    if(! workersRepo.existsById(user.getHostname()))
      workersRepo.save(user);

    addHostNameToMap(user);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @Transactional
  @Scheduled(fixedRate = 5000)
  public void cleanManifestationMap()
  {
    LocalDateTime now = LocalDateTime.now();
    List<String> toRemove = new ArrayList<>();
    for (String hostname : manifestationMap.keySet()) {
      if(manifestationMap.get(hostname).isBefore(now.minusMinutes(1)))
      {
        toRemove.add(hostname);
        workersRepo.deleteById(hostname);
      }
    }

    for (String hostname : toRemove) {
      manifestationMap.remove(hostname);
    }
    sendUpdatedWorkersListToLoadBalancer();
  }

  private void sendUpdatedWorkersListToLoadBalancer() {
    
    sendPostRequest("http://loadBalancer:8081/workersList", workersRepo.streamAllBy().toList());
  }

  private void addHostNameToMap(Worker user) 
  {
    if(manifestationMap.get(user.getHostname()) != null)
    {
      manifestationMap.replace(user.getHostname(), LocalDateTime.now());
    } else {
      manifestationMap.put(user.getHostname(), LocalDateTime.now());
    }
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
