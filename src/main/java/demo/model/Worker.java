package demo.model;

import jakarta.persistence.*;

@Entity
public class Worker {
    @Id
    private String hostname;
    private String service;
    private String port;

    public Worker() {
    }
    public Worker(String hostname) {
        this.hostname = hostname;
    }

    public Worker(String hostname, String service, String port) {
        this.hostname = hostname;
        this.service = service;
        this.port = port;
  }

    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
