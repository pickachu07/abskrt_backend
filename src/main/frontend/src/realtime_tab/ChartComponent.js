import React from 'react';
import Chart from './Chart';
import { getData } from "./utils";

export default class ChartComponent extends React.Component {
	constructor(props, context){
		super(props,context);
		
	}
	
	
	componentDidMount() {
		getData().then(data => {
			data = data.slice(0,2);
			this.setState({ data })
		})
	}
	
	componentWillReceiveProps(nextProps) {
		this.setState({data : nextProps.chartData});
	} 


	render() {
		//console.log("*****************props:"+this.props.chartData);
		
		if (this.state == null) {
			return <div>Loading...</div>
		}else if(this.state.data.length <= 3){
			return <div>Not enough data...</div>
		}
		return (
			
			<Chart type={"hybrid"} data={this.state.data} />
		
		)
	}
}


