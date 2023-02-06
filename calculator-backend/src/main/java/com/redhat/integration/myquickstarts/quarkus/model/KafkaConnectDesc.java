
package com.redhat.integration.myquickstarts.quarkus.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaConnectDesc {

    private Integer workers;
    private Integer tasks;
    private Integer cpu;
    private Integer mem; // MB

    public Integer getWorkers() {
        return workers;
    }

    public void setWorkers(Integer workers) {
        this.workers = workers;
    }

    public Integer getTasks() {
        return tasks;
    }

    public void setTasks(Integer tasks) {
        this.tasks = tasks;
    }

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    public Integer getMem() {
        return mem;
    }

    public void setMem(Integer mem) {
        this.mem = mem;
    }

        
    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return new String();
        }
    }
}