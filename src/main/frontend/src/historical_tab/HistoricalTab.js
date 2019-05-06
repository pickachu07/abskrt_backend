import React from 'react'
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import TextField from '@material-ui/core/TextField';
import CloudDownloadIcon from '@material-ui/icons/CloudDownload';
import Button from '@material-ui/core/Button';
import { Paper } from '@material-ui/core';
import TransactionTable from './TransactionTable';
import HistoricalRenkoContainer from './HistoricalRenkoContainer';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import Avatar from '@material-ui/core/Avatar';
import Chip from '@material-ui/core/Chip';
import FaceIcon from '@material-ui/icons/Face';


const styles = theme => ({
  appBarSpacer: theme.mixins.toolbar,
  content: {
    flexGrow: 1,
    padding: theme.spacing.unit * 3,
    height: '100vh',
    overflow: 'auto',
    width:'100%'
  },
  tableContainer: {
    width: '100%',
    height: '420px'
  },
  textField: {
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    width: 500,
  },
  brickSizeField: {
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    width: 200,
  },
  dateField:{
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    marginTop: theme.spacing.unit*2,
    width: 200,
  },
  rightIcon: {
    marginLeft: theme.spacing.unit,
  },
  fetchButton:{
    marginleft: theme.spacing.unit*2,
    marginTop: theme.spacing.unit*2,
  },
  toolbarPaper:{
      padding: theme.spacing.unit*2,
      marginLeft: theme.spacing.unit*2,

  }
});

class HistoricalTab extends React.Component {
  constructor(){
      super()
      this.state = {brick_size:10,ticker_name:"AXISBANK",start_date:"2017-05-24",end_date:"2017-05-29",data:[],trans:[],profit:0};
      this.handleBSChange = this.handleBSChange.bind(this);
      this.handleDateChange = this.handleDateChange.bind(this);
      this.handleTNChange = this.handleTNChange.bind(this);
      this.stompClient = null;
      this.getStompClient = this.getStompClient.bind(this);
      this.SocketConnect = this.SocketConnect.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
  }

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
      

      stompClientInstance.subscribe('/topic/historical_calculated_data_stream', cdata => {
        let cProfit = JSON.parse(cdata.body);
        this.setState({profit:cProfit})
        console.log("profit:"+cProfit);
      });


      stompClientInstance.subscribe('/topic/historical_trans_stream', transactions => {
        let results = JSON.parse(transactions.body);
        let output = [];  
        for(let i = 0;i<results.length;i++){
          let seq = results[i].columnKey;
          let val = results[i];
          output[seq] =  val; 
        }
        this.setState({trans:output});
        console.log("trans:"+this.state.trans);
      });


      stompClientInstance.subscribe('/topic/historical_data_stream', ndata => {
        var bricks = [];
        for(var i=0;i<JSON.parse(ndata.body).length;i++){
          //console.log("****nDataBody:"+JSON.parse(ndata.body)[i].data);
          var nd =new Date(JSON.parse(ndata.body)[i].data.timestamp)
          var op = JSON.parse(ndata.body)[i].data.open;
          var hi = JSON.parse(ndata.body)[i].data.high;
          var lo = JSON.parse(ndata.body)[i].data.low;
          var cl = JSON.parse(ndata.body)[i].data.close;
          var vol = JSON.parse(ndata.body)[i].data.volume;
          var newBrick = {date: nd, open: op, high: hi, low: lo, close: cl, volume : vol};
          this.setState({data : [...this.state.data, newBrick]});
          //bricks.push(newBrick);
        }
        //console.log("****stateData:"+this.state.data);
      });
});
}


  handleSubmit(event) {
    this.getStompClient().send("/app/StartHistoricalDataStream", {}, JSON.stringify({'brick_size' : this.state.brick_size,'ticker_name': this.state.ticker_name}))
    this.setState({streamingStarted: true});
    console.log('Historical Tab: A ticker was submitted: ' + this.state.ticker +": BS: "+this.state.brick_size);
    event.preventDefault();
  }

  handleBSChange = (event) => {
    console.log("Brick Size changed");
    this.setState({brick_size: event.target.value});
  }

  handleTNChange = (event) => {
    console.log("Ticker name changed");
    this.setState({ticker_name: event.target.value});
  }

  handleDateChange = (event) =>{
    if(event.target.id == "historical-start-date"){
        console.log("Start date changed");
        this.setState({start_date: event.target.value});
    }else if(event.target.id == "historical-end-date"){
        console.log("End date changed");
        this.setState({end_date: event.target.value});
    }
  }

  

  render() {
    const { classes } = this.props;
    return (
      <div>
        <div className={classes.appBarSpacer} />
        <main className={classes.content}>
        <Typography variant="h6" gutterBottom component="h6">
            Historical Data | Profit: {this.state.profit}
          </Typography>
          <Grid container spacing={8}>
            <Grid item xs={12}>
                <Paper elevation={1} className={classes.toolbarPaper}>
                    <TextField
                        required
                        id="historical-ticker-name"
                        label="Ticker"
                        defaultValue={this.state.ticker_name}
                        onChange={this.handleTNChange}
                        className={classes.textField}
                        margin="normal"
                    />
                    <TextField
                        id="historical-brick-size"
                        label="Brick size"
                        value={this.state.brick_size}
                        onChange={this.handleBSChange}
                        type="number"
                        className={classes.brickSizeField}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        margin="normal"
                    />
                    <TextField
                        id="historical-start-date"
                        label="Start"
                        type="date"
                        defaultValue={this.state.start_date}
                        onChange={this.handleDateChange}
                        className={classes.dateField}
                        InputLabelProps={{
                        shrink: true,
                        }}
                    />
                    <TextField
                        id="historical-end-date"
                        label="Start"
                        type="date"
                        defaultValue={this.state.end_date}
                        onChange={this.handleDateChange}
                        className={classes.dateField}
                        InputLabelProps={{
                        shrink: true,
                        }}
                    />
                    <Button variant="contained" color="default" className={classes.fetchButton} onClick={this.handleSubmit}>
                        Fetch and Draw
                        <CloudDownloadIcon className={classes.rightIcon} />
                    </Button>
                    <Chip 
                      onClick={this.SocketConnect}
                      className={classes.connectedChip} 
                      label={this.state.connected ? "Connected" : "Connect"} 
                      color={this.state.connected ? "primary" : "default"}
                      avatar={<Avatar><FaceIcon /></Avatar>}//TODO: change avatar based on state
                    />
                </Paper>
            </Grid>
            <Grid item xs={12}>
                <Paper className={classes.toolbarPaper}>
                    <HistoricalRenkoContainer data={this.state.data}/>
                </Paper>
            </Grid>
          </Grid>
          {this.state.data.length>2 && 
          <div>
          <Typography variant="h6" gutterBottom component="h6">
            Actions
          </Typography>
          <div className={classes.tableContainer}>
            <TransactionTable data={this.state.trans}/>
          </div>
          </div>}
        </main>
      </div>
    )
  }
}
export default withStyles(styles)(HistoricalTab)