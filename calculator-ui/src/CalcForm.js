import './CalcForm.css';

import React, { Component, useState, useEffect, setState  } from 'react';
import KafkaClusterDetails from './KafkaClusterDetails';

class CalcForm extends Component {

  constructor(props) {
    super(props);
    this.state = {}

    this.initDisplayStates();
    this.initRequestStates();
    this.initResponseStates();
    this.initComputingDisplayStates();

    this.state.isOnRequest = true;
    this.state.isValid=new Map();
  }

  initDisplayStates() {
    this.state.throuhputdefault=true;
    this.state.displayDisableThroughputBox=false;
    this.state.DisplayMessagesRateAreHidden=true;    
    this.state.net10default=true;
    this.state.diskAutoDefault=true;
    this.state.DisplayAutoDisHidden=false;
    this.state.diskSSDDefault=true;
  }

  initRequestStates() {
    this.state.req_inthroughput=0;
    this.state.req_outthroughput=this.state.req_inthroughput;

    this.state.req_replicas=3;
    this.state.req_avgpartitions=5;
    this.state.req_nbpartitions=300;
    this.state.req_nbtopics=100;

    this.state.req_netspeed=10;
    this.state.req_netpercent=80;
    this.state.req_diskspeed=400;
    this.state.req_diskpercent=80;
    this.state.req_disktype="";
    this.state.req_nbdisks=1;    

    this.state.req_consumers=5;
    this.state.req_lagtime=0;

    this.state.req_faultolerance=0;
    this.state.req_throughputtolerance=0;
    this.state.req_deviation=50;
    this.state.req_margin=25;

    this.state.req_mm=false;
    this.state.req_retention=5;

    this.state.req_ssl = false;
    this.state.req_compaction = false;
    this.state.req_useshare = false;
    this.state.req_cpushare = 0.1;
    this.state.req_limit=500;
  }

  initResponseStates() {
    this.state.resp_storage=0;
    
    this.state.resp_brokercpu=0;
    this.state.resp_nbbroker=0;
    this.state.resp_brokermem=0;

    this.state.resp_zkcpu=0;
    this.state.resp_zknb=0;
    this.state.resp_zkmem=0;

    this.state.resp_replicas=0;
    this.state.resp_insync=0;
    this.state.resp_pnode=0;

    this.state.resp_parallel=0;
    this.state.resp_cpushare=0

    this.state.resp_maxthroughput=0;
    this.state.resp_avglag=0;
    this.state.resp_maxlag=0;
    this.state.resp_overload={};
    
  }  

  initComputingDisplayStates() {
    this.state.msgtimeunit=1;       // default = second
    this.state.msgsizeunit=1/1000;    // default = kilobytes

    this.state.nbmessages=1;
    this.state.messagesize=1;
  }

  isForm() {
      return this.state.isOnRequest==true;
  }

  componentDidMount() {

    if (this.isForm()) {
        document.getElementById("diskcustdiv").hidden=true;
        document.getElementById("ssd").checked = true;
        document.getElementById("cgcustdiv").hidden = true;
        document.getElementById("lagvaldiv").hidden = true;
        document.getElementById("lag-pod").checked = true;
        document.getElementById("cpusharediv").hidden=true;
    }
  }


  /** 
     render() is the main method of the component
     it is where the page html is displayed
     with the binding to the functions that will react to page events
  */
  render() {

    // some logic can be added here to compute dynamic value before the return method is called

    // we do this rendering if we are displaying the Form
    if (this.isForm()) {    
       return (

        <div className="container">
          <h1 className="title">Red Hat AMQStreams sizing</h1>

            <div class="frame">
                <div class="form" id="throughput">
                    <h2>Inbound throughput</h2><br/>

                    {/* Throughput as absolute or based on messages rate radio button box */}
                    <input class="lag-space" type="radio" id="throughput-msg" name="throughput" value="msgsec" onClick={this.onThroughputSelectChange}/>
                    <label class="label-space" for="throughput-msg">message based</label><br/>
                    <input class="lag-space" type="radio" id="throughput-absolute" name="throughput" value="thru" checked={this.state.throuhputdefault} onClick={this.onThroughputSelectChange}/>
                    <label class="label-space" for="throughput-absolute"> absolute value</label><br/>
                
                            {/* Throughput box */}
                    <div class="align-absthroughput"> 
                        <div><label class="label-space-align" for="inboundrate">Throughput</label>
                             <input class="space-throughput" type="text" pattern="[0-9]*" onInput={this.validateNumberKey} onChange={this.updateDeviation} size="8"  name="inboundrate" id="inrate" defaultValue="10" disabled={this.state.displayDisableThroughputBox}/> 
                             <label class="t-in">MB/sec</label> 
                        </div>
                    </div> 
                    <br/><br/>
                            {/* message rate box (when selected by radio button)*/} 
                    <div class="align-custthroughput" hidden={this.state.DisplayMessagesRateAreHidden}> 
                        <div><label class="label-space-align" for="nbmessages">Number of messages</label> 
                              <input class="space-nbmessages" type="text" size="8" value={this.state.nbmessages} name="nbmessages" id="nbmessages" onInput={this.displayComputedThroughput} pattern="[0-9]*" onChange={this.validateNumberKey}/> 
                              <select class="space-units" name="msg-time-unit" id="msg-time-unit" onChange={this.displayComputedThroughput}> 
                                <option value="sec">msg/sec</option> 
                                <option value="min">msg/min</option> 
                              </select> 
                        </div> 
                        
                        <div><label class="label-space-align" for="messagesize">Average message size</label> 
                              <input class="space-units" type="text" size="8" value={this.state.messagesize} name="messagesize" id="messagesize" onInput={this.displayComputedThroughput} pattern="[0-9]*" onChange={this.validateNumberKey}/> 
                              <select class="space-units" name="msg-size-unit" defaultValue="kbytes" id="msg-size-unit" onChange={this.displayComputedThroughput}> 
                              <option value="bytes">bytes</option> 
                              <option value="kbytes">KB</option> 
                            </select> 
                        </div> 
                    </div> 
                    <br/><br/><br/>                
                </div>
                <div class="doc">
                    <h2 class="doc">How to set the throughput ?</h2>
                    <p>The throughput is the average message rate that the target kafka cluster will process.<br/>
                        It's the total amount of data per unit of time that all the producers will produce.<br/>
                        It's usually an average value. <br/>
                        It can be set as an absolute value, which is a specific amount of MB/s (which can also be a decimal).<br/>
                        It can also be set as a computation from the average number of messages per second and average message size.  
                    </p>
                </div>
            </div>
            <br/>
        
            <div class="frame" id="network">
                <div class="form">
                    <h2>Network Adapter capabilities</h2><br/>
                    <div>
                            <input class="lag-space"type="radio" id="net-speed-1" name="netspeed" value="1" onClick={this.onNetSelectChange}/>
                            <label class="label-space" for="net-speed-1">1 Gbps</label>
                            <input class="net-space" type="radio" id="net-speed-10" name="netspeed" value="10" defaultChecked onClick={this.onNetSelectChange}/>
                            <label class="label-space" for="net-speed-10">10 Gbps</label>
                    </div>
                    <label class="label-space-align" for="netsaturation">Network saturation</label> 
                    <input class="space-units" type="number"  min="1" max="100" size="4" defaultValue="80" name="netsaturation" id="netsaturation" /> 
                    <label class="label-space" for="netsaturation">%</label> 
                    
                </div>
                <div class="doc">
                    <h2 class="doc">How to adjust network parameters ? </h2>
                    <p>Use the radio buttons to select the network adpater capability of of the future node<br/>
                       The network saturation is the usable network bandwidth.<br/>
                       It's a safety margin to take into account network required by other administrative operations.
                    </p>
                </div> 
            </div>                               
                <br/>

            <div class="frame" id="disks">
                <div class="form">
                    <h2>I/O speed</h2><br/>
                    <div class="disk">
                            <input class="space-units" type="radio" id="disk-choice1" name="diskchoice" value="auto" defaultChecked onClick={this.onDiskChoiceChange}/>
                            <label class="label-space" for="disk-choice1">auto</label>
                            <input class="space-units" class="net-space" type="radio" id="disk-choice2" name="diskchoice" value="custom" onClick={this.onDiskChoiceChange}/>
                            <label class="label-space" for="disk-choice2">custom</label>
                    </div>
                    <div class="align-disks" id="diskcustdiv">
                        <label class="label-space-align" for="disk-abs">Custom disk R/W throughput</label>
                        <input class="space-units" type="text" pattern="[0-9]*" id="disk-abs" name="diskabs" onChange={this.validateNumberKey}/>
                        <label class="label-space" for="disk-abs">MB/s</label>
  
                    </div>                    
                    <div class="align-disks" id="diskautodiv">
                        <input class="disk-space-first" type="radio" id="xtreme" name="disktype" value="1000" onClick={this.onDiskTypeChange}/>
                        <label class="label-space" for="xtreme">Xtreme (1000MB/s)</label> <br/>
                        <input class="disk-space-first" type="radio" id="ssd" name="disktype" value="400" onClick={this.onDiskTypeChange} />
                        <label class="label-space" for="ssd">SSD (400MB/s)</label> <br/>
                        <input class="disk-space-first" type="radio" id="ultra-high-io" name="disktype" value="250" onClick={this.onDiskTypeChange}/>
                        <label class="label-space" for="ultra-high-io">Ultra I/O (250MB/s)</label> <br/>  
                        <input class="disk-space-first" type="radio" id="high-io" name="disktype" value="150" onClick={this.onDiskTypeChange}/>
                        <label class="label-space" for="high-io">High I/O (150MB/s)</label>  <br/>    
                        <input class="disk-space-first" type="radio" id="standard" name="disktype" value="50" onClick={this.onDiskTypeChange}/>
                        <label class="label-space" for="standard">Standard (50MB/s)</label>     
                    </div>
                        <div id="diskpara">
                        <label class="label-space-align" for="nbdisks">Number of disk in parallel</label> 
                        <input class="space-units" type="number"  min="1" max="10" size="4" defaultValue="1" name="nbdisks" id="nbdisks" /> 
                        </div>               
                        <label class="label-space-align" for="disksaturation">I/O saturation</label> 
                        <input class="space-units" type="number" min="1" max="100" size="4" defaultValue="80" name="disksaturation" id="disksaturation" /> 
                        <label class="label-space" for="disksaturation">%</label> 

                    </div>
                    <div class="doc">
                        <h2 class="doc">How to adjust the I/O parameters ?</h2>
                        <p>Select the disk speed, either automatically with the radio buttons, or as a custom amount of MB/s.<br/>
                        It's also possible to adjust the number of disks set in parallel, in RAID or JBOD deployment.<br/>
                        Multiple disks in parallel might affect the total amount of CPU for best performance.<br/>
                        The network saturation works the same way as for the network settings.</p>
                    </div>   
                </div>                  
                <br/>


                <div class="frame" id="consumers">
                    <div class="form">
                        <h2>Message transfer</h2><br/>
                        <div class="consumers" >
                                <input class="cg-space" type="radio" id="abscg" defaultChecked name="cgchoice" value="cgratio" onClick={this.onConsumersChoiceChange}/>
                                <label class="label-space" for="abscg">ratio</label>
                                <input class="cg-space" type="radio" id="custcg" name="cgchoice" value="cgtopic" onClick={this.onConsumersChoiceChange}/>
                                <label class="label-space" for="custcg">based on applications</label>
                        </div>             
                        <div>
                            <div id="cgabsdiv" class="consumers" >   
                                <label class="label-space-align" for="cgval">Average number of consuming applications per topic</label>
                                <input class="cg-space" type="number" name="cgval" id="cgval" min="1" max="30" size="4" defaultValue="1"/>
                            </div>
                            <div id="cgcustdiv" class="consumers">   
                                <label class="label-space-align" for="cgtopics">Total number of topics</label>
                                <input class="cg-space-plus" type="text" name="cgtopics" id="cgtopics" defaultValue="100" onChange={this.updateNumTopics}/><br/>
                                <label class="label-space-align" for="cgapps">Total number of consumer applications</label>
                                <input class="cg-space" type="text" name="cgapps" id="cgapps" defaultValue="100"/>
                            </div>  
                        </div> 
                    </div> 
                    <div class="doc">
                        <h2 class="doc">How to adjust the CG parameters ? </h2>
                        <p>The delivery of messages to consumers need to be confronted to the network capacity<br/>
                            For that, the total number of applications consuming from the cluster needs to be know.
                            This is the number of applications reading from topics, not partitions, 
                            thus it would corresponds to the number of consumer groups, not consumers, in AMQ Streams. 
                            The value can be an average ratio, or can derive from the total number of topics and applications. </p>                  
                    </div>
                </div>
                <br/>

                <div id="lagging" class="frame"> 
                    <div class="form">
                    <h2>Lagging support</h2><br/>
                        <div>
                            <input class="lag-space" type="radio" id="lagauto" name="lag" value="lagauto" defaultChecked onClick={this.onLagChoiceChange}/>
                            <label class="label-space" for="lagauo">auto</label>
                            <input class="lag-space" type="radio" id="lagval" name="lag" value="lagval" onClick={this.onLagChoiceChange}/>
                            <label class="label-space" for="lagval">fixed value</label>
                        </div>   
                        <div id="lagautodiv">
                            <input class="lag-space"type="radio" id="lag-vm" name="lagtype" value="60" onClick={this.onLagTypeChange}/>
                            <label class="label-space" for="lag-vm">VM (1 min)</label>
                            <input class="disk-space" type="radio" id="lag-pod" name="lagtype" value="30" onClick={this.onlagTypeChange} />
                            <label class="label-space" for="lag-pod">Pod (30 sec)</label>
                            <input class="disk-space" type="radio" id="lag-func" name="lagtype" value="10" onClick={this.onLagTypeChange}/>
                            <label class="label-space" for="lag-func">Serverless (10 sec)</label>       
                        </div>                              
                        <div>
                            <div id="lagvaldiv">   
                                <label class="label-space-align" for="lagvalue">maximum allowed lagging time</label>
                                <input class="lag-space" type="number" name="lagvalue" id="lagvalue" min="O" max="300" defaultValue="0" />
                                <label class="label-space" for="lagvalue">sec</label>
                            </div>  
                        </div>                  
                    </div>
                    <div class="doc">
                        <h2 class="doc">How to adjust the lagging parameters ?</h2>
                        <p>The lag is the amount of time we allow for a consuming application to restore itself from a failing state.<br/>
                           This value affects the RAM memory required by each node.<br/>
                           Indeed, a certain amount of historical data can be kept in the page cache for fast redelivery, 
                           without the need to read back from the disk, which is an heavier operation.<br/>
                           The value is a fixed number of second. <br/>
                           Example of RTO for common deployment types can also be used.</p>                  
                    </div>                    
                </div>
                <br/>


                <div class="frame" id="reliability">
                    <div class="form" >
                        <h2>Reliability enforcement</h2><br/>
                        <label class="label-space-align" for="replicas">Replication factor</label>
                        <input class="ha-space" type="number" name="replicas" id="replicas" min="1" max="5" defaultValue="3" /> 
                        <br/>    
                        <label class="label-space-align" for="zkha">Fault tolerance (maintenance mode)</label>
                        <input class="ha-space" type="number" name="akha" id="zkha" min="0" max="2" defaultValue="0" /><br/>   
                        <label class="label-space-align" for="extranode">Throughput tolerance</label>
                        <input class="ha-space-pp" type="number" name="extranode" id="extranode" min="0" max="20" defaultValue="1" /> 
                        <br/>
                        <label class="label-space-align" for="deviation">Peak Deviation</label>
                        <input class="ha-space" type="number" name="deviation" id="deviation" min="1" max="1000" defaultValue="80" onChange={this.updateDeviation}/>  
                        <label class="label-space" for="deviation">%</label> <br/>
                        <input class="ha-space" type="text" name="deviationpc" id="deviationpc" defaultValue="18" pattern="[0-9]*" onInput={this.validateNumberKey} onChange={this.updateDeviation}/>  
                        <label class="label-space" for="deviationpp">MB/s</label><br></br>
                        <input class="ha-space" type="text" name="deviationpmsg" id="deviationpmsg" defaultValue={this.state.messagesize} disabled pattern="[0-9]*" onInput={this.validateNumberKey} onChange={this.updateDeviation}/>  
                        <label class="label-space" for="deviationpmsg">messages/s</label> <br/><br/>
                        <label class="label-space-align" for="margin">Security margin</label>
                        <input class="ha-space" type="number" name="margin" id="margin" min="1" max="100" defaultValue="25" />  
                        <label class="label-space" for="margin">%</label>    
                        <br/><br/>              
                    </div>
                    <div class="doc">
                        <h2 class="doc">How to adjust the HA parameters ?</h2>
                        <p>There are a fe parameters required to express the level of reliability of the cluster.<br/>
                            The fault tolerance only affects the controler plane.  
                            Valid values are 0,1 or 2 and tell how many nodes we might want to be able to put under maintenance on purpose while still having a highly availabile solution.                
                            The throughput tolerance is similar but affects the data plane.  
                            It tells the maximum number of nodes that can be down (on purpose or following an incident) letting the cluster is a condition such as it can still process 100% of the throughput.
                            The peak deviation comes into play when the throughput result in an average number of message per seconds.<br/>
                            It allows to express what throughput we can expect at peak times.  For example, if the average number of message per second is 800, 
                            but, at peak time, the system can receive up to 2000 message per seconds, the value should be 150%, 
                            to tell that the peak value adds up 150% of 800 to 800.<br/>
                            The security margin is an overal margin to cover the fact that the woakload of a cluster is not always 100% balanced ver each node.
                            The security marin is a pure percentage added to the total computed workload. </p>                  
                    </div>                    
                </div>   

                <br/>
                <div class="frame" id="mirrormaker">
                    <div class="form">
                        <h2>Mirror Maker</h2> <br/>                        
                        <input class="ex-space"  type="checkbox" id="usemm" name="usemm" onClick={this.enableMM}/>  
                        <label class="label-space" for="usemm">Deploy MirrorMaker 2</label>                                              
                    </div>     
                     
                    <div class="doc">
                        <h2 class="doc">How to configure MM ?</h2> <br/>
                        <p>Mirror maker<br/>    
                        </p>                  
                    </div>                    
                </div>

                <br/>
                <div class="frame" id="limits">
                    <div class="form">
                        <h2>Cluster details and limits</h2> <br/>
                        <label class="label-space-align" for="retention">Storage retention period</label>
                        <input class="ex-space-align-pp" type="number" name="retention" id="retention" min="1" max="365" defaultValue="5" /> 
                        <label class="label-space" for="retention">days</label>
                        <br/>  

                        <label class="label-space-align" for="nbtopics">Total number of topics</label>
                        <input class="ex-space-align" size="8" type="text" pattern="[0-9]*" name="nbtopics" id="nbtopics" defaultValue="100" onInput={this.validateNumberKey} onChange={this.updateNumTopics}/><br/>                         
                        <label class="label-space-align" for="nbpartitions">Total number of partitions</label>
                        <input class="ex-space" size="8" type="text" pattern="[0-9]*" name="nbpartitions" id="nbpartitions" defaultValue="300" onInput={this.validateNumberKey} onChange={this.adjustPartitionRatio}/> <br/>       
                        <label class="label-space-align" for="nbpartitions">Average partitions per topic</label>
                        <input class="ex-space-align-plus" type="number" name="avgpartitions" id="avgpartitions" min="1" max="30" defaultValue="3" onChange={this.adjustPartitions} />                                        
                        <br/>
                        <label class="ex-space" for="outrate">Expected outbound throuhput</label> 
                        <input class="ex-space" type="text" size="5" pattern="[0-9]*" defaultValue="30" name="outrate" id="outrate" onChange={this.validateNumberKey}/>   
                        <label class="label-space" for="outrate">MB/s</label> 
                        <br/><br/>
                        <label class="label-space-align" for="maxparts">Maximum partitions per node</label> 
                        <input class="ex-space" type="number" name="maxparts" id="maxparts" min="100" max="2000" defaultValue="500"/> 
                        <br/>                          
                        <br/>  
                        <div class="sslcheck">
                            <input class="ex-space"  type="checkbox" id="ssl" name="ssl"/>
                            <label class="label-space" for="ssl">Use of SSL</label> 
                            
                            <input class="ex-space" type="checkbox" id="compaction" name="compaction"/>
                            <label class="label-space" for="compaction">Use compaction</label>
                        </div>   
                        <br/><br/>                          
                        <input class="ex-space"  type="checkbox" id="useshare" name="useshare" onClick={this.enableCpushare}/>  
                        <label class="label-space" for="useshare">Use fixed CPU share</label>                       
                        <div class="cpushare" id="cpusharediv">
                            <input class="ex-space" type="number" name="cpushare" id="cpushare" min="0.01" max="1" defaultValue="0.1" step=".01"/>    
                        </div>                          
                    </div>     
                     
                    <div class="doc">
                        <h2 class="doc">How to adjust the cluster limits ?</h2> <br/>
                        <p>Some extra parameters might influence the overal performance of the AMQ Streams cluster.<br/>
                            For example, the use of SSL or Compaction generaly requires a little bit of extra CPU.<br/>
                            <br/>There is a theorical limit of 50 nodes per AMQ Streams cluster. <br/>
                            There is also a theorical limit in the number of partitions per node.<br/>
                            Some benchmarks go up to 2000 or even 4000 partitions per node but we encourage setting the limit a little bit lower.<br/>
                            A specific CPU share can be assigned.  It will fix the quantity of CPU allocatable to one single partition on one node<br/>
                            <br/>
                            Adding partitions to topics can theoretically increase the outbound throughput in regard to the inbound throughput.<br/>
                            The calculator will respond with an indication of the resulting CPU share, if not explicitely set, 
                            as well as an assertion of the expected outbound throuput.
                            </p>                  
                    </div>                    
                </div> 
                <br/>        
                <button id="submit" type="submit" onClick={this.callKafkaCalculator}>Compute cluster size</button>
                <br/><br/>

        </div>
        )
      }
      // we do this rendering if we are calling the API, to display the response
      else  {

        return (
  
          <div className="container">
            <h1 class="result">Red Hat AMQStreams sizing result</h1>

            <div class="result">
            <br/><br/><br/>
                <div>
                    <KafkaClusterDetails
                        cluster_sto={this.state.resp_storage}
                        
                        broker_cpu={this.state.resp_brokercpu}
                        broker_nb={this.state.resp_brokernb}
                        broker_mem={this.state.resp_brokermem}

                        zk_cpu={this.state.resp_zkcpu}
                        zk_nb={this.state.resp_zknb}
                        zk_mem={this.state.resp_zkmem}

                        t_replicas={this.state.resp_replicas}
                        t_insync={this.state.resp_insync}
                        t_topics={this.state.resp_topics}
                        t_partitions={this.state.resp_partitions}
                        t_pnode={this.state.resp_pnode}

                        t_parallel={this.state.resp_parallel}
                        t_cpushare={this.state.resp_cpushare}

                        cluster_maxout={this.state.resp_maxthroughput}
                        cluster_overload={this.state.resp_overload}

                        cluster_maxlag={this.state.resp_maxlag}
                        cluster_avglag={this.state.resp_avglag}
                        
                    />
                </div>
            </div>

          </div>
       )
      }
  
  }

  validateNumberKey = (e) => {
    var flag=0; 
    if (this.state.isValid.get(e.target.id) >=0) flag = this.state.isValid.get(e.target.id);
    console.log("valid = "+ this.state.isValid.get(e.target.id));
     
    if (e.target.validity.valid===false) {
        e.target.style="background-color:red;"; 
        if (flag < 1) this.state.isValid.set(e.target.id, 1);
    }
    else {
        e.target.style=""; 
        this.state.isValid.set(e.target.id, 0);
    }

    var sum=0;
    console.log(this.state.isValid.entries());
    this.state.isValid.forEach( (entry) => {
        sum=sum+entry;
    })
    console.log("sum = " + sum);
    if (sum===0)
        document.getElementById("submit").disabled=false;
    else
        document.getElementById("submit").disabled=true;
  }

  // this function is linked to the radio buttons of the "throughput" section
  // we adjust the display according to the selected radio button
  // in the memory (setState, which is initialized in the constructor), we store the name and the value of the event
  // the name is the name of the radio button, and the value is the value of the radio button from the radio button definition
  // the event.target contains both.  it's also possible to use e.target.name ; e.target.value
  // the re-rendering is automated  
  onThroughputSelectChange = (e) => {
    
    const { name, value } = e.target;
    this.setState({
      [name]: value
    });

    // we need to adject the 'checked' value of th radio button for the next rendering
    if (e.target.value==="thru") {
        this.state.throuhputdefault=true;
        this.state.displayDisableThroughputBox=false;
        this.state.DisplayMessagesRateAreHidden=true;
        document.getElementById("deviationpmsg").disabled=true;
        document.getElementById("deviationpc").disabled=false;
    }
    if (e.target.value==="msgsec") {
        this.state.throuhputdefault=false;
        this.state.displayDisableThroughputBox=true;
        this.state.DisplayMessagesRateAreHidden=false;
        document.getElementById("inrate").value = this.state.req_inthroughput;
        document.getElementById("deviationpc").disabled=true;
        document.getElementById("deviationpmsg").disabled=false;
        document.getElementById("deviationpmsg").value = document.getElementById("nbmessages").value;
    }
  }

  displayComputedThroughput = (e) => {    
    
    const { name, value } = e.target;
    this.setState({
      [name]: value
    });  

    switch (e.target.name) {
        case "nbmessages": 
            this.state.nbmessages = e.target.value;
            if (document.getElementById("msg-time-unit").value==="min") document.getElementById("deviationpmsg").value = e.target.value / 60;
            else document.getElementById("deviationpmsg").value = e.target.value * (1 + (parseInt(document.getElementById("deviation").value) / 100) );
            break;
        case "messagesize":
            this.state.messagesize = e.target.value;
            break;
        case "msg-time-unit": 
            if (e.target.value=="sec")  //TODO: hour, day
                this.state.msgtimeunit = 1;      
            if (e.target.value=="min")  //TODO: hour, day
                this.state.msgtimeunit = 60;
            break;
        case "msg-size-unit": 
            if (e.target.value=="bytes")  //TODO: ?
                this.state.msgsizeunit = 1/1000000;
            if (e.target.value=="kbytes")  //TODO: ?
                this.state.msgsizeunit = 1/1000;  
            break;
        default: break;
    };
    
    this.state.req_inthroughput = this.state.nbmessages / this.state.msgtimeunit * this.state.messagesize * this.state.msgsizeunit;
    document.getElementById("inrate").value = this.state.req_inthroughput;
    document.getElementById("outrate").value = document.getElementById("inrate").value * document.getElementById("nbpartitions").value;

    document.getElementById("deviationpc").value = document.getElementById("inrate").value * (1 + parseInt(document.getElementById("deviation").value)/100);
  };     
  

  onNetSelectChange = (e) => {
    const { name, value } = e.target;
    this.setState({
      [name]: value
    });
    if (e.target.value==="1") {
        this.state.net10default=false;
        this.state.req_netspeed=1;
    }
    if (e.target.value==="10") {
        this.state.net10default=true;
        this.state.req_netspeed=10;
    }    
  }

  onDiskChoiceChange = (e) => {

    const { name, value } = e.target;
    this.setState({
      [name]: value
    });
    if (e.target.value==="auto") {
        document.getElementById("diskautodiv").hidden=false;
        document.getElementById("diskcustdiv").hidden=true;
        document.getElementById("diskpara").hidden=false;
    }
    if (e.target.value==="custom") {
        document.getElementById("diskautodiv").hidden=true; 
        document.getElementById("diskcustdiv").hidden=false;
        document.getElementById("diskpara").hidden=true;
    }
}

  onDiskTypeChange = (e) => {

    switch (e.target.value) {
        case "ussd": document.getElementById("disk-ussd").checked = true; break;
        case "ssd": document.getElementById("disk-ssd").checked = true; break;
        case "uhigh": document.getElementById("disk-uhigh").checked = true; break;
        case "high": document.getElementById("disk-high").checked = true; break;
      }
  }

  onConsumersChoiceChange = (e) => {

    if (e.target.value==="cgratio") {
        document.getElementById("cgabsdiv").hidden = false;
        document.getElementById("cgcustdiv").hidden = true;
    }
    
    if (e.target.value==="cgtopic") {
        document.getElementById("cgabsdiv").hidden = true;
        document.getElementById("cgcustdiv").hidden = false;
    }
  }

  updateNumTopics = (e) => {
    if (e.target.id==="cgtopics")
        document.getElementById("nbtopics").value = e.target.value;
    if (e.target.id==="nbtopics") 
        document.getElementById("cgtopics").value = e.target.value;
       
    this.adjustPartitionRatio(e);
  }
  adjustPartitionRatio = (e) => {
    document.getElementById("avgpartitions").value = document.getElementById("nbpartitions").value / document.getElementById("cgtopics").value ;
  }
  updateDeviation = (e) => {
      if (e.target.id==="deviation") {   // the deviation percentage has changed, we adjust throughput and msg/sec
        console.log("deviationpc :" + document.getElementById("deviationpc").value);
        console.log("inrate : " + document.getElementById("inrate").value);
        console.log("% :" + e.target.value/100);
        document.getElementById("deviationpc").value = parseInt(document.getElementById("inrate").value) + parseInt((document.getElementById("inrate").value ) * (e.target.value/100));

        if (document.getElementById("deviationpmsg").disabled===false)
            document.getElementById("deviationpmsg").value = parseInt(document.getElementById("nbmessages").value) +  parseInt(document.getElementById("nbmessages").value) * parseInt(e.target.id);
      }
      if (e.target.id==="deviationpc"){
        var diff = parseInt(e.target.value) - parseInt(document.getElementById("inrate").value);
        if (diff<=0) document.getElementById("deviation").value = 0;
        else 
            document.getElementById("deviation").value = (diff * 100) / parseInt(document.getElementById("inrate").value);
      }
      if (e.target.id==="deviationpmsg"){
        var diff = parseInt(e.target.value) - parseInt(document.getElementById("nbmessages").value);
        if (diff<=0) document.getElementById("deviation").value = 0;
        else {
            var tnew = parseInt(document.getElementById("deviationpmsg").value) * parseInt(document.getElementById("messagesize").value) / 1000;
            var dif2 = tnew - parseInt(document.getElementById("inrate").value);
            document.getElementById("deviation").value = (dif2 * 100) / parseInt(document.getElementById("inrate").value);
            document.getElementById("deviationpc").value = tnew;
        }

      }
      if (e.target.id==="inrate") {
        document.getElementById("deviationpc").value = document.getElementById("inrate").value * (1 + parseInt(document.getElementById("deviation").value)/100);
      }
  }

  enableCpushare = (e) => {
      if (document.getElementById("useshare").checked) document.getElementById("cpusharediv").hidden=false;
      else document.getElementById("cpusharediv").hidden=true;
  }

  adjustPartitions = (e) => {
    document.getElementById("nbpartitions").value = document.getElementById("nbtopics").value * document.getElementById("avgpartitions").value;
    document.getElementById("outrate").value = document.getElementById("avgpartitions").value * document.getElementById("inrate").value ;
  }

  onLagChoiceChange = (e) => {

    if (e.target.value==="lagauto") {
        document.getElementById("lagvaldiv").hidden = true; 
        document.getElementById("lagautodiv").hidden = false;
    }
    if (e.target.value==="lagval") {
        document.getElementById("lagvaldiv").hidden = false; 
        document.getElementById("lagautodiv").hidden = true;
    }
  }

  onLagtypeChange = (e) => {

    switch (e.target.value) {
        case "lag-pod": document.getElementById("lag-pod").checked = true; 
        case "lag-vm": document.getElementById("lag-vm").checked = true; break;
        case "lag-func": document.getElementById("lag-func").checked = true; break;
    }
      
  }
  callKafkaCalculator = () => {


    this.computeParams();

    let payload = {
        inthroughput: this.state.req_inthroughput,
        outthroughput: this.state.req_outthroughput,

        nbpartitions:this.state.req_nbpartitions,
        avgpartitions: this.state.req_avgpartitions,
        nbtopics: this.state.req_nbtopics,
        replicas: this.state.req_replicas,

        netspeed: this.state.req_netspeed,
        netsat: this.state.req_netpercent,
        diskspeed: this.state.req_diskspeed,
        disksat: this.state.req_diskpercent,
        disktype: this.state.req_disktype,
        nbdisks: this.state.req_nbdisks,
        consumers: this.state.req_consumers,
        
        lagtime: this.state.req_lagtime,

        faulttolerance: this.state.req_faultolerance,
        throughputtolerance: this.state.req_throughputtolerance,
        deviation: this.state.req_deviation,
        margin: this.state.req_margin,

        mm: this.state.req_mm,
        retention: this.state.req_retention,
        limit: this.state.req_limit,
        ssl: this.state.req_ssl,
        compaction: this.state.req_compaction,  

        useshare: this.state.req_useshare,
        cpushare: this.state.req_cpushare
      }

    console.log(payload);
    
    fetch('http://localhost:8090/api/kafkasizing', {
      method: 'POST',
      body: JSON.stringify(payload),
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      })
      .then((response) => response.json())
      .then((data) => {
          console.log(data);
          this.state.resp_storage=data.storage;
          
          this.state.resp_brokercpu=data.node.cpu;
          this.state.resp_brokernb=data.node.numnodes;
          this.state.resp_brokermem=data.node.memory;

          this.state.resp_zkcpu=data.zk.cpu;
          this.state.resp_zknb=data.zk.numnodes;
          this.state.resp_zkmem=data.zk.memory;

          this.state.resp_replicas=data.topic.replicas;
          this.state.resp_insync=data.topic.insync;
          this.state.resp_pnode=data.topic.pnode;  
          
          this.state.resp_parallel=data.topic.parallel;
          this.state.resp_cpushare=data.topic.cpushare;          

          this.state.resp_maxthroughput=data.maxthroughput;
          this.state.resp_avglag=data.avglag;
          this.state.resp_maxlag=data.maxlag;

          this.state.resp_overload=data.overload;

          this.state.isOnRequest=false;     // careful : asynchronous !!!
          this.forceUpdate();

      })
      .catch((err) => {
        console.log(err.message);
      });
      

      /* Needed to force the component to execute render() again
         React is optimize for dynamic rendering
         Normally, only the state variables are dynamically modified from the dyn cache
         but the full render() method is not re-executed */
      
  }
  
  computeParams = () => {
        
    /* Throughput */
    if (document.getElementById("throughput-msg").checked)          // the computed throughput is already in this.state.req_inthroughput
        this.state.req_inthroughput = this.state.req_inthroughput;
    if (document.getElementById("throughput-absolute").checked)     // the throughput is still in the input field
        this.state.req_inthroughput = document.getElementById("inrate").value;

    /* Replicas */
    this.state.req_replicas = document.getElementById("replicas").value;

    /* Partitions and topics */
    this.state.req_avgpartitions = document.getElementById("avgpartitions").value;
    this.state.req_nbtopics = document.getElementById("nbtopics").value;
    this.state.req_nbpartitions = document.getElementById("nbpartitions").value;  

    /* Network Speed */
    if (document.getElementById("net-speed-1").checked) this.state.req_netspeed = 1;
    if (document.getElementById("net-speed-10").checked) this.state.req_netspeed = 10; 
    this.state.req_netspeed = this.state.req_netspeed; 
    this.state.req_netpercent = document.getElementById("netsaturation").value;

    /* Disk Speed */
    if (document.getElementById("disk-choice2").checked)    // custom value for disk throughput is used
        this.state.req_diskspeed = document.getElementById("disk-abs").value;
    if (document.getElementById("disk-choice1").checked) {  // auto value based on disk cateory is used
        let x = document.getElementsByName('disktype')
        x.forEach((item, index) => {
            if (item.checked) {
                this.state.req_diskspeed = item.value;
                this.state.req_disktype = item.id;
            }
        })

        this.state.req_diskpercent = document.getElementById("disksaturation").value;
        this.state.req_nbdisks = document.getElementById("nbdisks").value;
        
    }
  
    /* Consumers */
    if (document.getElementById("abscg").checked)      // absolute number given for total number of consumers
        this.state.req_consumers = document.getElementById("cgval").value;
    if (document.getElementById("custcg").checked) {   // number of consumers come from topics and applications
        this.state.req_consumers = document.getElementById("cgapps").value / document.getElementById("cgtopics").value;
        if (this.state.req_consumers < 1) this.state.req_consumers = 1;
    }
    this.state.req_consumers = parseInt(this.state.req_consumers) + parseInt(this.state.req_replicas);  // parseInt, otherwise it concatenate strings
    if (document.getElementById("usemm"))  
        this.state.req_consumers = this.state.req_consumers + 1;

    /* Lag */  
    if (document.getElementById("lagval").checked)          // fix time is given
        this.state.req_lagtime = document.getElementById("lagvalue").value;
    if (document.getElementById("lagauto").checked) {       // auto value based on deployment model
        let x = document.getElementsByName('lagtype')
        x.forEach((item, index) => {
            if (item.checked) 
                this.state.req_lagtime = item.value;
        })
    }  

    this.state.req_faultolerance = document.getElementById("zkha").value;
    this.state.req_throughputtolerance = document.getElementById("extranode").value;
    this.state.req_deviation = document.getElementById("deviation").value;
    this.state.req_margin = document.getElementById("margin").value;

    this.state.req_mm = document.getElementById("usemm").checked;
    this.state.req_retention = document.getElementById("retention").value;

    this.state.req_outthroughput = document.getElementById("outrate").value;

    this.state.req_ssl = document.getElementById("ssl").checked;
    this.state.req_compaction = document.getElementById("compaction").checked;

    this.state.req_useshare=document.getElementById("useshare").checked;
    this.state.req_cpushare = document.getElementById("cpushare").value;
    this.state.req_limit = document.getElementById("maxparts").value;
  }
} 

export default CalcForm;