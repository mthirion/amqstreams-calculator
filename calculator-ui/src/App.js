import logo from './logo.svg';
import './App.css';

import React, { Component} from 'react';
import CalcForm from './CalcForm';

class App extends Component {

  constructor(props) {
    super(props);
    this.state = {}
  }
  
  componentDidMount() {

  }

  render() {
    const { posts } = this.state;
    return (
      <div onClick={this.callKafkaCalculator}>
        <CalcForm/>
      </div>
    );
  }

  callKafkaCalculator = (e) => {
    console.log("");  // intercepting child event
  }

}

export default App;
