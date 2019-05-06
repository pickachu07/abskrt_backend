import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

const styles = {
  root: {
    width: '100%',
    overflowX: 'auto',
  },
  table: {
    minWidth: 700,
  }
};

class TransactionTable extends React.Component{
  
  constructor(){
    super();
    this.state={trans:{data:[]}};
  }
  /*let id = 0;
  function createData(number, action, price) {
    id += 1;
    return { id, number, action, price };
  }*/
  componentWillReceiveProps = (nextProps) => {
    this.setState({trans: nextProps})
    console.log("state");
    console.log(this.state.trans);
  }


  render() {
    const { classes } = this.props;
  
    return (
      <Paper className={classes.root}>
        <Table className={classes.table}>
          <TableHead>
            <TableRow>
              <TableCell>Trade No.</TableCell>
              <TableCell align="right">Action</TableCell>
              <TableCell align="right">Price (INR)</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
          {
            this.state.trans.data.length > 0 && this.state.trans.data.map(n => (
            <TableRow key={n.columnkey}>
              <TableCell component="th" scope="row">
                {n.columnKey}
              </TableCell>
              <TableCell align="right">{n.rowKey}</TableCell>
              <TableCell align="right">{n.value}</TableCell>
            </TableRow>
          ))
          }
          
          </TableBody>
        </Table>
      </Paper>
    );
  }
}
export default withStyles(styles)(TransactionTable);