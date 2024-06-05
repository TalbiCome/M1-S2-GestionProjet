package demo.controller;

import java.util.stream.Stream;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import demo.model.Worker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient.Request;



@Profile("server")
@Controller
@RequestMapping("/launch")
public class Server {
  @GetMapping()
  public void createWorker() 
  {
    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
      .withDockerHost("http://node:2375")
      .build();

    DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
    .dockerHost(config.getDockerHost())
    .sslConfig(config.getSSLConfig())
    .build();

    DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
    dockerClient.createContainerCmd("gestiondeprojet-loginapp:latest")
      .withName("worker")
      .exec();
  }
}
