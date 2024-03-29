import './KafkaClusterDetails.css';

import React, { Component, useState, useEffect, setState  } from 'react';

class KafkaClusterDetails extends Component {

  constructor(props) {
    super(props);
    this.state = {}

  }

  componentDidMount() {
    
  }


  /** 
     render() is the main method of the component
     it is where the page html is displayed
     with the binding to the functions that will react to page events
  */
  render() {

    // some logic can be added here to compute dynamic value before the return method is called

        var overload_str = "";
        if (this.props.cluster_overload != null)
          this.props.cluster_overload.forEach(entry => {
            overload_str+=entry;
            overload_str+="\n";
          });

        var maxlag=this.props.cluster_maxlag + " sec";        
        if (this.props.cluster_maxlag > 180) {
           var mod = this.props.cluster_maxlag % 60;
           maxlag = parseInt(this.props.cluster_maxlag / 60) + "min " + parseInt(mod) + "'";
        }
        var avglag=this.props.cluster_avglag + " sec";        
        if (this.props.cluster_avglag > 180) {
          var mod = this.props.cluster_avglag % 60;
          avglag = parseInt(this.props.cluster_avglag / 60) + "min " + parseInt(mod) + "'";
       }

        return (
  
          <div className="container">
            <div class="warnings"> 
              <h3>Cluster warnings</h3> 
              <div class="overload">
                
                  {/*Conditional rendering*/}
                  {this.props.cluster_overload != null && 
                    <p>{overload_str} </p>
                  }  
                  {this.props.cluster_overload == null &&           
                  <p>No warnings</p>
                }
              </div>              
            </div>     
            <br/><br/>         

            <div class="plane">       
              <h3>Planes details</h3>     
              <div class="planeleft">
                <table>
                  <thead>
                    <tr>
                        <th colspan="3">Control plane (Zookeeper)</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                        <td>Number of node</td>
                        <td>CPU per node</td>
                        <td>memory per node</td>
                    </tr>
                    <tr>
                        <td>{this.props.zk_nb}</td>                  
                        <td>{this.props.zk_cpu}</td>
                        <td>{this.props.zk_mem}</td>
                    </tr>                
                  </tbody>
                </table>
              </div>

              <div class="planeright">
                <table>
                  <thead>
                    <tr>
                        <th colspan="3">Data plane (Brokers)</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                        <td>Number of node</td>
                        <td>CPU per node</td>
                        <td>Memory per node</td>
                    </tr>
                    <tr>
                        <td>{this.props.broker_nb}</td>                  
                        <td>{this.props.broker_cpu}</td>
                        <td>{this.props.broker_mem}</td>
                    </tr>                
                  </tbody>
                </table>
              </div>
            </div>
            <br/><br/>

            {(this.props.mm_enabled == true || this.props.kc_enabled == true) &&    
            <div class="connect">  
            <h3>Connect</h3>

              {/*Conditional rendering*/}
              {this.props.mm_enabled == true &&    
                <div class="planeleft">
                  <table>
                    <thead>
                      <tr>
                          <th colspan="3">Mirror Maker</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                          <td>Number of node</td>
                          <td>CPU per node</td>
                          <td>Memory per node</td>
                      </tr>
                      <tr>
                          <td>{this.props.mm_workers}</td>                  
                          <td>{this.props.mm_cpu}</td>
                          <td>{this.props.mm_mem} MB</td>
                      </tr>                
                    </tbody>
                  </table>
                </div>
              } 

              {/*Conditional rendering*/}
              {this.props.kc_enabled == true &&    
                <div class="planeright">
                  <table>
                    <thead>
                      <tr>
                          <th colspan="3">KafkaConnect</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                          <td>Number of node</td>
                          <td>CPU per node</td>
                          <td>Memory per node</td>
                      </tr>
                      <tr>
                          <td>{this.props.kc_workers}</td>                  
                          <td>{this.props.kc_cpu}</td>
                          <td>{this.props.kc_mem} MB</td>
                      </tr>                
                    </tbody>
                  </table>
                </div>
              } 
            </div>
            } 


            <br/><br/>
            <div class="settings"> 
              <h3>Cluster settings</h3> 
              <div class="sleft">
                <table>
                  <thead>
                    <tr>
                        <th colspan="2">Cluster-wide characteristics</th>
                    </tr>
                  </thead>
                  <tbody>
                
                    <tr>
                        <td>Total persistent storage</td>
                        <td>{this.props.cluster_sto}</td>     
                    </tr>
                    <tr>
                        <td>Persistent storage per broker node</td>
                        <td>{this.props.cluster_nsto}</td>     
                    </tr>                    
                    <tr>
                        <td>Replication factor</td>
                        <td>{this.props.t_replicas}</td> 
                    </tr>                                     
                    <tr>
                        <td>In-sync replicas</td>                   
                        <td>{this.props.t_insync}</td>
                    </tr>              
                  </tbody>
                </table>
                
              </div>
              <div class="sright">
                <table>
                  <thead>
                    <tr>
                        <th colspan="5">Performance data</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                        <td>Partitions per node</td>
                        <td>{this.props.t_pnode}</td>
                    </tr> 
                    <tr>
                        <td>Parallelism (partitions per topic)</td>                   
                        <td>{this.props.t_parallel}</td>
                    </tr>                      
                    <tr>
                        <td>CPU per partition</td>
                        <td>{this.props.t_cpushare}</td>
                    </tr>   
                    <tr>
                        <td>Maximum outbound throuhput</td>
                        <td>{this.props.cluster_maxout} MB/s</td>     
                    </tr>  
                    <tr>
                        <td>Possible Lag</td>
                        <td>{avglag}</td>     
                    </tr>                       
                    <tr>
                        <td>Max possible lag at peak time</td>
                        <td>{maxlag}</td>     
                    </tr>                                                                   
                  </tbody>
                </table>  
                <br/>
              </div> 
            </div>  
            
          </div>
       )
  
  }


} 

export default KafkaClusterDetails;
