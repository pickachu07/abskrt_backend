import React from 'react'
import SimpleTable from './SimpleTable';
import RealtimeRenkoContainer from './RealtimeRenkoContainer';
import Paper from '@material-ui/core/Paper';
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
          <Typography variant="h4" gutterBottom component="h4">
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