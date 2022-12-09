package com.redhat.integration.myquickstarts.quarkus.model;

import java.time.temporal.ValueRange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaZkDesc {

    @JsonIgnore public final Integer ZK_NUM_NODE_DEFAULT=3; 

    private Integer numnodes;  
    private String memory;
    private Integer cpu; 
    //private Integer socket; 
    //private Integer core; 

    public KafkaZkDesc (Integer partitions) {

        ValueRange range2k = ValueRange.of(0, 2000);
        ValueRange range10k = ValueRange.of(2000, 10000);
        ValueRange range50k = ValueRange.of(10000, 50000);
        ValueRange range100k = ValueRange.of(50000, 100000);
        ValueRange range200k = ValueRange.of(10000, 200000);

        if (range2k.isValidIntValue(partitions)) {cpu=2; memory="4 GB";}
        if (range10k.isValidIntValue(partitions)) {cpu=4; memory="8 GB";}
        if (range50k.isValidIntValue(partitions)) {cpu=8; memory="16 GB";}
        if (range100k.isValidIntValue(partitions)) {cpu=12; memory="24 GB";}
        if (range100k.isValidIntValue(partitions)) {cpu=16; memory="32 GB";}

    }
    

    public Integer getNumnodes() {
        return numnodes;
    }

    public void setNumnodes(Integer numnodes) {
        this.numnodes = numnodes;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
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