import React from 'react';
import ChartComponent from "./ChartComponent";
export default class RealtimeContainer extends React.Component {
    render() {
      return(
      <div className="container-fluid">
        <div className="row">
          <div className="col">
            <div className="btn-group">
              <button type="button" className="btn btn-danger">Primary</button>
              <button type="button" className="btn btn-danger dropdown-toggle dropdown-toggle-split" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <span className="sr-only">Toggle Dropdown</span>
              </button>
              <div className="dropdown-menu">
                <a className="dropdown-item" href="#">Action</a>
                <a className="dropdown-item" href="#">Another action</a>
                <a className="dropdown-item" href="#">Something else here</a>
                <div className="dropdown-divider"></div>
                <a className="dropdown-item" href="#">Separated link</a>
              </div>
            </div>
          </div>
          <div className="col">
            <button type={"button"} className="btn btn-primary">Primary</button>
          </div>
        </div>
        <div className="row">
          <ChartComponent />
        </div>
      </div>
      );
    }
  }