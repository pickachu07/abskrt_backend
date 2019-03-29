import React from 'react'
import { withStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import { FormHelperText } from '@material-ui/core';

const styles = theme => ({
  appBarSpacer: theme.mixins.toolbar,
  content: {
    flexGrow: 1,
    padding: theme.spacing.unit * 3,
    height: '100vh',
    overflow: 'auto',
    width:'100%'
  }
});

class SettingsTab extends React.Component {
  
  render() {
    const { classes } = this.props;
    return (
      <div>
        <div className={classes.appBarSpacer} />
        <main className={classes.content}>
        <Typography variant="h4" gutterBottom component="h4">
            Settings
          </Typography>
        </main>
      </div>
    )
  }
}
export default withStyles(styles)(SettingsTab)