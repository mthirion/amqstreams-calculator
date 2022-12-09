package com.redhat.integration.myquickstarts.quarkus.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaClusterDesc {

    @JsonIgnore public final Integer MX_PARTITION_PER_CLUSTER=2000;
    @JsonIgnore public final Integer MAX_BROKERS_PER_CLUSTER=50;

    private List<String> overload;
    private String bottleneck;
    private double inthroughput;
    private double peakthroughput;
    private double maxthroughput;
    private int maxlag;
    private int avglag;
    private String storage;

    private boolean ssl;
    private boolean compaction;

    private KafkaNodeDesc node;

    
    private KafkaZkDesc zk;
    //private KafkaNodeDesc mm;
    private KafkaTopicDesc topic;
    

    public String getBottleneck() {
        return bottleneck;
    }

    public void setBottleneck(String bottleneck) {
        this.bottleneck = bottleneck;
    }

    public KafkaNodeDesc getNode() {
        return node;
    }

    public void setNode(KafkaNodeDesc node) {
        this.node = node;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public double getInthroughput() {
        return inthroughput;
    }

    public void setInthroughput(double inthroughput) {
        this.inthroughput = inthroughput;
    }

    public double getPeakthroughput() {
        return peakthroughput;
    }

    public void setPeakthroughput(double peakthroughput) {
        this.peakthroughput = peakthroughput;
    }

    public double getMaxthroughput() {
        return maxthroughput;
    }

    public void setMaxthroughput(double maxthroughput) {
        this.maxthroughput = maxthroughput;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public boolean isCompaction() {
        return compaction;
    }

    public void setCompaction(boolean compaction) {
        this.compaction = compaction;
    }

    public KafkaZkDesc getZk() {
        return zk;
    }

    public void setZk(KafkaZkDesc zk) {
        this.zk = zk;
    }

    public List<String> getOverload() {
        return overload;
    }

    public void setOverload(List<String> overload) {
        this.overload = overload;
    }
    
    public void addOverload(String item) {
        if (overload==null) overload = new ArrayList<String>();
        this.overload.add(item);
    }

    public KafkaTopicDesc getTopic() {
        return topic;
    }

    public void setTopic(KafkaTopicDesc topic) {
        this.topic = topic;
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
    /*
    @Override
    public boolean equals(KafkaClusterDesc desc){
        return false;
    }
    */

    public int getMaxlag() {
        return maxlag;
    }

    public void setMaxlag(int maxlag) {
        this.maxlag = maxlag;
    }

    public int getAvglag() {
        return avglag;
    }

    public void setAvglag(int avglag) {
        this.avglag = avglag;
    }
}