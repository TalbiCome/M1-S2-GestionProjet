package demo.controller;

import demo.model.Worker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.stream.Stream;

@EnableScheduling
@RestController
@RequestMapping("/workers")
public class RegisteryController {
    @Autowired
    private WorkerRepository workersRepo;
    private HashMap<String, LocalDateTime> manifestationMap = new HashMap<>();

    @Transactional
    @GetMapping()
    public ResponseEntity<Object> getUsers() {
        Stream<Worker> s = workersRepo.streamAllBy();
        return new ResponseEntity<>(s.toList(), HttpStatus.OK);
    }

    @GetMapping("/map")
    public ResponseEntity<Object> getManifestationMap() {
        return new ResponseEntity<>(manifestationMap.toString(), HttpStatus.OK);
    }
    
    @PostMapping()
    public ResponseEntity<Worker> put(@RequestBody Worker user) {
        
        if(! workersRepo.existsById(user.getHostname())){
          workersRepo.save(user);
        }

        if(manifestationMap.get(user.getHostname()) != null)
        {
          manifestationMap.replace(user.getHostname(), LocalDateTime.now());
        } else {
          manifestationMap.put(user.getHostname(), LocalDateTime.now());
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Scheduled(fixedRate = 12000)
    public void cleanManifestationMap()
    {
      LocalDateTime now = LocalDateTime.now();
      manifestationMap.entrySet().removeIf(entry -> entry.getValue().isBefore(now.minusMinutes(1)));
    }
}