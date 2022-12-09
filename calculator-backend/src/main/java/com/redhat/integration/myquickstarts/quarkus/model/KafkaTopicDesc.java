package com.redhat.integration.myquickstarts.quarkus.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaTopicDesc {

    private Integer replicas;
    private Integer insync;

    private Integer topics;
    private Integer partitions;
    private Integer pnode;      // partition per node
    private Integer parallel;   // partition per topic
    private Double cpushare;

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public Integer getInsync() {
        return insync;
    }

    public void setInsync(Integer insync) {
        this.insync = insync;
    }

    public Integer getTopics() {
        return topics;
    }

    public void setTopics(Integer topics) {
        this.topics = topics;
    }

    public Integer getPartitions() {
        return partitions;
    }

    public void setPartitions(Integer partitions) {
        this.partitions = partitions;
    }

    public Integer getPnode() {
        return pnode;
    }

    public void setPnode(Integer pnode) {
        this.pnode = pnode;
    }

    public Integer getParallel() {
        return parallel;
    }

    public void setParallel(Integer parallel) {
        this.parallel = parallel;
    }

    public Double getCpushare() {
        return cpushare;
    }

    public void setCpushare(Double cpushare) {
        this.cpushare = cpushare;
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