package com.redhat.integration.myquickstarts.quarkus.model;

public class KafkaRequirements {

    private Double inthroughput;   // MB per sec
    private Double outthroughput;  // MB/s    

    private Integer nbtopics; 
    private Integer nbpartitions; 
    private Integer avgpartitions;      
    private Integer replicas;

    private Integer netspeed;       // Gbps
    private Integer netsat;         // %
    private Integer diskspeed;      // MB per sec
    private Integer disksat;        // %
    private String disktype;        // SSD...
    private Integer nbdisks; 

    private Integer consumers;
    private Integer lagtime;        // sec

    private Integer faulttolerance;
    private Integer throughputtolerance;
    private Integer deviation;      // %
    private Integer margin;         // %

    private Boolean mm;             // true|false
    private Integer retention;      // days

    private boolean ssl=false;
    private boolean compaction=false;
    private boolean useshare = false;
    private Double cpushare=0.1;
    private Integer limit=500;

    public Double getInthroughput() {
        return inthroughput;
    }

    public void setInthroughput(Double inthroughput) {
        this.inthroughput = inthroughput;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public Integer getNetspeed() {
        return netspeed;
    }

    public void setNetspeed(Integer netspeed) {
        this.netspeed = netspeed;
    }

    public Integer getDiskspeed() {
        return diskspeed;
    }

    public void setDiskspeed(Integer diskspeed) {
        this.diskspeed = diskspeed;
    }

    public Integer getConsumers() {
        return consumers;
    }

    public void setConsumers(Integer consumers) {
        this.consumers = consumers;
    }

    public Integer getLagtime() {
        return lagtime;
    }

    public void setLagtime(Integer lagtime) {
        this.lagtime = lagtime;
    }

    public Integer getFaulttolerance() {
        return faulttolerance;
    }

    public void setFaulttolerance(Integer faulttolerance) {
        this.faulttolerance = faulttolerance;
    }

    public Integer getThroughputtolerance() {
        return throughputtolerance;
    }

    public void setThroughputtolerance(Integer throughputtolerance) {
        this.throughputtolerance = throughputtolerance;
    }

    public Integer getDeviation() {
        return deviation;
    }

    public void setDeviation(Integer deviation) {
        this.deviation = deviation;
    }

    public Integer getMargin() {
        return margin;
    }

    public void setMargin(Integer margin) {
        this.margin = margin;
    }

    public Boolean getMm() {
        return mm;
    }

    public void setMm(Boolean mm) {
        this.mm = mm;
    }

    public Integer getRetention() {
        return retention;
    }

    public void setRetention(Integer retention) {
        this.retention = retention;
    }

    public Double getOutthroughput() {
        return outthroughput;
    }

    public void setOutthroughput(Double outthroughput) {
        this.outthroughput = outthroughput;
    }

    public String getDisktype() {
        return disktype;
    }

    public void setDisktype(String disktype) {
        this.disktype = disktype;
    }

    public Integer getNbdisks() {
        return nbdisks;
    }

    public void setNbdisks(Integer nbdisks) {
        this.nbdisks = nbdisks;
    }

    public Integer getNetsat() {
        return netsat;
    }

    public void setNetsat(Integer netsat) {
        this.netsat = netsat;
    }

    public Integer getDisksat() {
        return disksat;
    }

    public void setDisksat(Integer disksat) {
        this.disksat = disksat;
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

    public Integer getNbtopics() {
        return nbtopics;
    }

    public void setNbtopics(Integer nbtopics) {
        this.nbtopics = nbtopics;
    }

    public Integer getNbpartitions() {
        return nbpartitions;
    }

    public void setNbpartitions(Integer nbpartitions) {
        this.nbpartitions = nbpartitions;
    }

    public Double getCpushare() {
        return cpushare;
    }

    public void setCpushare(Double cpushare) {
        this.cpushare = cpushare;
    }

    public Integer getAvgpartitions() {
        return avgpartitions;
    }

    public void setAvgpartitions(Integer avgpartitions) {
        this.avgpartitions = avgpartitions;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public boolean isUseshare() {
        return useshare;
    }

    public void setUseshare(boolean useshare) {
        this.useshare = useshare;
    }

    @Override
    public String toString() {

        String str = "inbound throunghput : " + inthroughput + "\n"; 
        str += "nb topics : " + nbtopics + "\n"; 
        str += "nb partitions :  " + nbpartitions + "\n"; 
        str += "nb replicas : " + replicas + "\n"; 
        str += "network : " + netspeed + " (saturation = " + netsat + ")\n"; 
        str += "disk : "  + diskspeed + " (saturation = " + disksat + " ; type = " +  disktype + " ; number = " +  nbdisks + ")\n";   
        str += "nb consumers : " + consumers + "\n"; 
        str += "lag time : " + lagtime + "\n"; 
        str += "inbound throughput = " + inthroughput + "\n"; 
        str += "fault tolerannce on zookeeper : " + faulttolerance + "\n"; 
        str += "throughput tolerance on brokers : " + throughputtolerance + "\n"; 
        str += "deviation factor : " + deviation + "\n"; 
        str += "security margin : " + margin + "\n"; 
        str += "storage retention period : " + retention + "\n"; 

        str += "mirror maker enabled : " + mm + "\n"; 
        str += "ssl enabled : " + ssl + "\n"; 
        str += "compaction enabled : " + compaction + "\n"; 
        str += "use CPU share enabled : " + useshare + "\n"; 
        str += "CPU share value : " + cpushare + "\n"; 
        str += "hard partition limit : " + limit + "\n\n"; 

        return str;
    }
 

}
