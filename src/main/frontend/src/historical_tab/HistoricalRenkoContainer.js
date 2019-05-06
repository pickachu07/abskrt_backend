import React from 'react';
import ChartComponent from "../realtime_tab/ChartComponent";
import { withStyles } from '@material-ui/core/styles';

const styles = theme =>({
  
});

class HistoricalRenkoContainer extends React.Component {
  
  constructor(){
    super();
    this.state = {data:[]}
  }
  componentWillReceiveProps = (nextProps) => {
    if(nextProps.data.length > 0)  {
      
    this.setState({data: nextProps.data})
    }
  }
  render() {
    const {classes} = this.props;
      return(
      <div className="container-fluid">
        <div className="row">
          <ChartComponent chartData={this.state.data}/>
        </div>
      </div>
      );
    }
  }


  export default withStyles(styles)(HistoricalRenkoContainer)