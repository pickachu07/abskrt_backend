import React from 'react';
import ChartComponent from "./ChartComponent";
import Avatar from '@material-ui/core/Avatar';
import Chip from '@material-ui/core/Chip';
import FaceIcon from '@material-ui/icons/Face';
import { withStyles } from '@material-ui/core/styles';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
const styles = theme =>({
  
});

class OHLCChartContainer extends React.Component {
  
  constructor(){
    super();
    this.state = {ticker: 'SBIN', brick_size : 1000, streamingStarted : false,connected:false,data:[]};

    // this.handleTChange = this.handleTChange.bind(this);
    // this.handleBSChange = this.handleBSChange.bind(this);
    // this.handleSubmit = this.handleSubmit.bind(this);
    // this.handleStop = this.handleStop.bind(this);
    this.stompClient = null;
    this.getStompClient = this.getStompClient.bind(this);
    this.SocketConnect = this.SocketConnect.bind(this);
  }

//   handleTChange(event) {
//     this.setState({ticker: event.target.value});
//   }

//   handleBSChange(event) {
//     this.setState({brick_size: event.target.value});
//   }

//   handleSubmit(event) {
//     this.getStompClient().send("/app/start_streaming", {}, JSON.stringify({'brick_size' : this.state.brick_size,'ticker_name': this.state.ticker}))
//     this.setState({streamingStarted: true});
//     console.log('A ticker was submitted: ' + this.state.ticker +": BS: "+this.state.brick_size);
//     event.preventDefault();
//   }
//   handleStop(event){
//     this.getStompClient().send("/app/stop_streaming", {}, {});
//     this.setState({streamingStarted: false});
//     console.log('Stop Streaming pressed');
//     event.preventDefault();
//   }

  getStompClient = () =>{
    if(this.stompClient== null){
      let socket = new SockJS("http://localhost:8080/gs-guide-websocket");
      this.stompClient= Stomp.over(socket);
    }
    return this.stompClient;
  }

  SocketConnect = () =>{
    if(this.state.connected === true)return;
    let stompClientInstance = this.getStompClient();
    stompClientInstance.connect({}, frame => {
      this.setState({connected: true});
      
      stompClientInstance.subscribe('/topic/ohlc_stream', ndata => {
        
        if(this.state.data!=null){
          var nd =new Date(JSON.parse(ndata.body).data.timestamp)
          var op = JSON.parse(ndata.body).data.open;
          var hi = JSON.parse(ndata.body).data.high;
          var lo = JSON.parse(ndata.body).data.low;
          var cl = JSON.parse(ndata.body).data.close;
          var vol = JSON.parse(ndata.body).data.volume;
          var newArr = {date: nd, open: op, high: hi, low: lo, close: cl, volume : vol};
          this.setState({data : [...this.state.data, newArr]});
          //console.log("****stateData:"+this.state.data);
        }
    
      });
});
stompClientInstance.ws.onclose = () =>{
  this.setState({connected: false});
}

  }

  render() {
    const {classes} = this.props;
      return(
      <div className="container-fluid">
        <div className="row">
         <div className="col">
          <Chip 
                onClick={this.SocketConnect}
                className={classes.connectedChip} 
                label={this.state.connected ? "Connected" : "Connect"} 
                color={this.state.connected ? "primary" : "default"}
                avatar={<Avatar><FaceIcon /></Avatar>}//TODO: change avatar based on state
              />
         </div>
        </div>
        <div className="row">
          <ChartComponent chartData={this.state.data}/>
        </div>
      </div>
      );
    }
  }


  export default withStyles(styles)(OHLCChartContainer)