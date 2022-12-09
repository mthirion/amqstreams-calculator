package com.redhat.integration.myquickstarts.quarkus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaNodeDesc {

    @JsonIgnore public final Integer BROKER_NUM_NODE_DEFAULT=2;
    @JsonIgnore public final Integer BROKER_MEMORY_BASE_DEFAULT=6;
    @JsonIgnore public final Double BROKER_MEMORY_DEFAULT=8.0;
    @JsonIgnore public final Integer BROKER_CPU_DEFAULT=4;
    @JsonIgnore public final String DISK_MOUNT_TYPE_JBOD="JBOD";
    @JsonIgnore public final String DISK_MOUNT_TYPE_RAID="RAID";

    @JsonIgnore public final String NET_SPEED_1="1 Gbps";
    @JsonIgnore public final String NET_SPEED_10="10 Gbps";


    private Integer numnodes;  
    private String memory;
    private Integer cpu; 
    //private Integer socket; 
    //private Integer core; 

    private String netcard;
    private String diskspeed;
    private Integer nbdisks;
    private String disktype="unknown";
    private String mounttype="unknown";

    
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

    public String getNetcard() {
        return netcard;
    }

    public void setNetcard(String netcard) {
        this.netcard = netcard;
    }

    public String getDiskspeed() {
        return diskspeed;
    }

    public void setDiskspeed(String diskspeed) {
        this.diskspeed = diskspeed;
    }

    public Integer getNbdisks() {
        return nbdisks;
    }

    public void setNbdisks(Integer nbdisks) {
        this.nbdisks = nbdisks;
    }

    public String getDisktype() {
        return disktype;
    }

    public void setDisktype(String disktype) {
        this.disktype = disktype;
    }

    public String getMounttype() {
        return mounttype;
    }

    public void setMounttype(String mounttype) {
        this.mounttype = mounttype;
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