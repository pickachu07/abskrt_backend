import React from 'react'
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import TextField from '@material-ui/core/TextField';
import CloudDownloadIcon from '@material-ui/icons/CloudDownload';
import Button from '@material-ui/core/Button';
import { Paper } from '@material-ui/core';

import HistoricalRenkoContainer from './HistoricalRenkoContainer';

const styles = theme => ({
  appBarSpacer: theme.mixins.toolbar,
  content: {
    flexGrow: 1,
    padding: theme.spacing.unit * 3,
    height: '100vh',
    overflow: 'auto',
    width:'100%'
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
      this.state = {brick_size:10,ticker_name:"AXISBANK",start_date:"2017-05-24",end_date:"2017-05-29",data:[]};
      this.handleBSChange = this.handleBSChange.bind(this);
      this.handleDateChange = this.handleDateChange.bind(this);
      this.handleTNChange = this.handleTNChange.bind(this);
      this.fetchAndDraw = this.fetchAndDraw.bind(this);
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

  fetchAndDraw =() =>{
      fetch("http://localhost:8080/historical/")
      .then(result => result.json())
      .then(results => this.parseData(results));
      

  }
  parseData = (results) => {
    let output = [];  
    for(let i = 0;i<results.length;i++){
        var nd =new Date(results[i].timestamp)
          var op = results[i].open;
          var hi = results[i].high;
          var lo = results[i].low;
          var cl = results[i].close;
          var vol = results[i].volume;
          var newArr = {date: nd, open: op, high: hi, low: lo, close: cl, volume : vol};
          output[i]= newArr;
    }
    this.setState({data:output});
    console.log(this.state);
  }

  render() {
    const { classes } = this.props;
    return (
      <div>
        <div className={classes.appBarSpacer} />
        <main className={classes.content}>
        <Typography variant="h6" gutterBottom component="h6">
            Historical Data
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
                    <Button variant="contained" color="default" className={classes.fetchButton} onClick={this.fetchAndDraw}>
                        Fetch and Draw
                        <CloudDownloadIcon className={classes.rightIcon} />
                    </Button>
                </Paper>
            </Grid>
            <Grid item xs={12}>
                <Paper className={classes.toolbarPaper}>
                    <HistoricalRenkoContainer data={this.state.data}/>
                </Paper>
            </Grid>
          </Grid>
        </main>
      </div>
    )
  }
}
export default withStyles(styles)(HistoricalTab)