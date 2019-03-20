import React from 'react';
import { render } from 'react-dom';
import Chart from './Chart';
import { getData } from "./utils";
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

import { TypeChooser } from "react-stockcharts/lib/helper";

export default class ChartComponent extends React.Component {
	constructor(props, context){
		super(props,context)
		this.dataArr = [];
		this.socket = new SockJS("http://localhost:8080/gs-guide-websocket");
    	this.stompClient = Stomp.over(this.socket);
		
    	this.stompClient.connect({}, frame => {
      		console.log(`connected, ${frame}!`);
      		this.stompClient.subscribe('/topic/greetings', ndata => {
				//console.log("----->:"+JSON.parse(ndata.body).data);
				//this.dataArr.push(ndata);
				if(this.state.data!=null){
					//console.log(this.state.data.push({date: "Tue Jan 05 2010 00:00:00 GMT+0530 (India Standard Time)", open: 25.627344939513726, high: 25.83502196495549, low: 25.452895407434543, close: 25.718722, volume : 400}));
					var nd =new Date(JSON.parse(ndata.body).data.timestamp)
					var op = JSON.parse(ndata.body).data.open;
					var hi = JSON.parse(ndata.body).data.high;
					var lo = JSON.parse(ndata.body).data.low;
					var cl = JSON.parse(ndata.body).data.close;
					var vol = JSON.parse(ndata.body).data.volume;
					var newArr = {date: nd, open: op, high: hi, low: lo, close: cl, volume : 400};
					this.setState({data : [...this.state.data, newArr]});
					console.log(this.state);
				}
				
      		});
		});
	}
	
	
	componentDidMount() {
		getData().then(data => {
			data = data.slice(1,3);
			this.setState({ data })
		})
	}
	render() {
		//console.log(this.state.data);
		
		if (this.state == null) {
			return <div>Loading...</div>
		}
		return (
			
			<Chart type={"hybrid"} data={this.state.data} />
		
		)
	}
}


