import React, { Component } from "react";
import CurrentState from "./currentState";
import ServiceState from "./serviceStatus";
import CurrentLocation from "./currentLocation";
import RoadService from "./roadService";
import VehicleId from "./vehicleId";
import auth from "../services/authService";
import { Link } from "react-router-dom";
import { socket } from "../App";

// import { getJwt } from "../services/authService";
import { getAvStates } from "../services/avService";
import { getVehicles } from "../services/userService";

import _ from "lodash";
import axios from "axios";

// import { io } from "socket.io-client";
// const socket = io("http://localhost:3900", {
//   query: {
//     jwtToken: getJwt(),
//   },
// });

const user = auth.getCurrentUser();
// let user1 = user.name.slice(0,1).toUpperCase() + user.name.slice(1,user.name.length);
let user1 = "";
if (user != null) {
  user1 =
    user.name.slice(0, 1).toUpperCase() + user.name.slice(1, user.name.length);
}

class UserDashboard extends Component {
  state = {
    currentState: "",
    serviceState: "",
    currentLocation: "",
    roadService: "",
    vid: "",
  };

  async componentDidMount() {
    //this.populateAVStatusListData();
    console.log("MADE IT TO SOCKET");
    socket.on("activeVehicleData", this.reRenderAV);
    socket.on("activeSensorInformation", this.reRenderAV1);
    console.log("MADE IT PAS SOCKET");

    const { data: getV } = await getVehicles();
    this.setState({getV});
    console.log("THIS: ",  this.state.getV[0]);  
    if(this.state.getV.length>0){
      this.setState({
        currentState : this.state.getV[0].vcurrentstatus,
        vid: this.state.getV[0].vid,
        roadService : this.state.getV[0].roadservice
      })
      setTimeout(this.statuschange,3000);
      
    }

  }

  statuschange =()=>{
    console.log("entered in status");
    var data = ({
      status : this.state.getV[0].vcurrentstatus,
      vid : this.state.getV[0].vid
    })
    var stringdata = JSON.stringify(data);
    console.log("data"+stringdata);

    axios.post(`http://18.225.6.194:3900/statusupdate/${stringdata}`)
    //add ip
    .then(response=>{
      console.log("hello entered here"+JSON.stringify(response));
      setTimeout(function(){
        window.location.reload();
      }, 5000);

    })
    .catch(error=>{
      if(error){
        console.log(error);
      }
    }) 
   }

  async populateAVStatusListData() {
    const { data: avStatus } = await getAvStates();
    console.log("MADE ", avStatus);
    // const avStatusd = [];
    // avStatus.map((item) => {
    //     avStatusd.push({
    //     state: item.state,
    //   });
    // });
    console.log("MADE ", avStatus);
    this.setState(avStatus);
    // const avState = await getAvStates();
    // console.log("LIST DATA: ", avStatus);
    // this.setState({ avStatus });

  }

   reRenderAV = (data) => {
    console.log("SOCKET INCOMING DATA: ", data);
    this.setState({
      currentState: data.currentState,
      serviceState: data.serviceState,
      roadService: data.roadService,
      vid: data.vid,
    });
    
    console.log("Populating count data");
  };

  reRenderAV1 = (data) => {
    console.log("SOCKET INCOMING DATA1: ", data);
    this.setState({ currentLocation: data.currentLocation, 
            vid: data.vid, });
    const index = _.findIndex(this.state.getV, (v) => {
        
        return (v.vId == data.vId)
    })
    console.log(index)
    this.setState({
        currentState: this.state.getV[index].currentState,
        serviceState: this.state.getV[index].serviceState,
        roadService: this.state.getV[index].roadService,
        vid: this.state.getV[index].vid,
      });
    console.log("Populating count data");
  };

  render() {
    return (
      <React.Fragment>
        <h1 className="text-center" style={{ marginBottom: "25px" }}>
          My Dashboard
        </h1>
       
        <div class="card-deck mb-3 text-center">
        <VehicleId
          style={{ marginTop: "30px" }}
          data={this.state.vid}
        ></VehicleId>
       
        <CurrentState
          style={{ marginTop: "30px" }}
          data={this.state.currentState}
        ></CurrentState>

        
        <ServiceState
          style={{ marginTop: "35px" }}
          data={this.state.serviceState}
        ></ServiceState>
        {/* <CurrentLocation
          style={{ marginTop: "35px" }}
          data={this.state.currentLocation}
        ></CurrentLocation> */}
        <RoadService
          style={{ marginTop: "35px" }}
          data={this.state.roadService}
        ></RoadService>
         <Link
          className="btn btn-dark"
          to={{
            pathname: "/sensorinfo",
          }}
        >
          View Sensor Information
        </Link>
        </div>
      </React.Fragment>
    );
  }
}

export default UserDashboard;
