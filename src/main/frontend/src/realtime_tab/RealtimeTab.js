import React from 'react'
import SimpleTable from './SimpleTable';
import RealtimeRenkoContainer from './RealtimeRenkoContainer';
import OHLCChartContainer from './OHLCChartContainer';
import Paper from '@material-ui/core/Paper';
import Switch from '@material-ui/core/Switch';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Typography from '@material-ui/core/Typography';
import { withStyles } from '@material-ui/core/styles';

const styles = theme => ({
    appBarSpacer: theme.mixins.toolbar,
    content: {
      flexGrow: 1,
      padding: theme.spacing.unit * 3,
      height: '100vh',
      overflow: 'auto',
      width:'100%'
    },
    container:{
      width:'100%'
    },
    chartContainer: {
      marginLeft: -2,
    },
    tableContainer: {
      height: 320,
    },
    h5: {
      marginBottom: theme.spacing.unit * 2,
    },
  });


class RealtimeTab extends React.Component {
  constructor(){
    super();
    this.state = {ohlc_visible:false}
  };
  showOHLC = () =>{
    if(this.state.ohlc_visible === false){
      this.setState({ohlc_visible : true});
    }else{
      this.setState({ohlc_visible : false});
    }
  };
  render() {
    const { classes } = this.props;
    return (
      <div className={classes.container}>
       <main className={classes.content}>
          <div className={classes.appBarSpacer} />
          <Paper elevation={1}>
            
          </Paper>
          <div className={classes.chartContainer}>
            <Paper elevation={1}>
                <RealtimeRenkoContainer />
            </Paper>
          </div>
          {/* <Typography variant="h6" gutterBottom component="h6">
            OHLC
          </Typography> */}
          <FormControlLabel
          control={
            <Switch
              checked={this.state.ohlc_visible}
              onChange={this.showOHLC}
              value="true"
              color="primary"
            />
          }
          label="Show OHLC"
        />{
          this.state.ohlc_visible&&<div className={classes.chartContainer}>
          <Paper elevation={1}>
              <OHLCChartContainer />
          </Paper>
        </div>
        }
          <Typography variant="h6" gutterBottom component="h6">
            Actions
          </Typography>
          <div className={classes.tableContainer}>
            <SimpleTable />
          </div>
        </main>
      </div>
    )}
}
export default withStyles(styles)(RealtimeTab)