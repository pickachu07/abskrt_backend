import React from 'react';
import ChartComponent from "./ChartComponent";
import StompClient from './socketInstance';
export default class RealtimeContainer extends React.Component {
  
  constructor(){
    super();
    this.state = {ticker: 'SBIN', brick_size : 1000, streamingStarted : false};

    this.handleTChange = this.handleTChange.bind(this);
    this.handleBSChange = this.handleBSChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleStop = this.handleStop.bind(this);
  }

  handleTChange(event) {
    this.setState({ticker: event.target.value});
  }

  handleBSChange(event) {
    this.setState({brick_size: event.target.value});
  }

  handleSubmit(event) {
    StompClient.send("/app/start_streaming", {}, JSON.stringify({'brick_size' : this.state.brick_size,'ticker_name': this.state.ticker}))
    this.setState({streamingStarted: true});
    console.log('A ticker was submitted: ' + this.state.ticker +": BS: "+this.state.brick_size);
    event.preventDefault();
  }
  handleStop(event){
    StompClient.send("/app/stop_streaming", {}, {});
    this.setState({streamingStarted: false});
    console.log('Stop Streaming pressed');
    event.preventDefault();
  }

  render() {
      return(
      <div className="container-fluid">
        <div className="row">
         <div className="col">
          <form onSubmit={this.handleSubmit}>
              <label>
                Ticker:
                <input disabled={this.state.streamingStarted} id="ticker-input" type="text" value={this.state.ticker || ""} onChange={this.handleTChange} />
              </label>
              <label>
                Brick Size:
                <input  disabled={this.state.streamingStarted}id="bs-input" type="number" value={this.state.brick_size || 0} onChange={this.handleBSChange} />
              </label>
              <input disabled={this.state.streamingStarted} className="btn-primary"  value="Start" onClick={this.handleSubmit}/>
              <input disabled={!this.state.streamingStarted} className="btn-danger" value="Stop" onClick={this.handleStop}/>
          </form>
         </div>
        </div>
        <div className="row">
          <ChartComponent />
        </div>
      </div>
      );
    }
  }