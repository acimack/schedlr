import {useState} from "react";
import config from "../config"
import axios from "axios";
import {Redirect} from "react-router-dom";
import TimeEntry from "../utils/TimeEntry";
import "./Onboard.css"

function Index({newUser, setNewUser, existingUser, setExistingUser}) {
    const [startTime, setStartTime] = useState("09:00");
    const [endTime, setEndTime] = useState("17:00");

    if (existingUser) return <Redirect to="/dashboard" />
    if (!newUser) return <Redirect to="/" />


    const handleClick = () => {
        // todo: handle error input
        const currDate = new Date();
        currDate.setHours(0,0,0,0);
        const startDate = new Date(currDate.toDateString() + " " + startTime);
        const startMin = (startDate.getTime() - currDate.getTime()) / 60000;

        const endDate = new Date(currDate.toDateString() + " " + endTime);
        const endMin = (endDate.getTime() - currDate.getTime()) / 60000;
        const toSend = {
            startMin: startMin,
            endMin: endMin,
        };
        axios.post(
            'http://localhost:4567/setdailytimes',
            toSend,
            config
        )
            .then(response => {
                const success = response.data["success"];
                if (success) {
                    setNewUser(false)
                    setExistingUser(true)
                } else {
                    setNewUser(false)
                    setExistingUser(false)
                    alert("An error occurred. Sign in again.")
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    return (
        <div className='onboard'>
            <div className='white-box'>
                <div className="onboard-content">
                    <h1 id='welcome'>welcome to schedlr!</h1>
                    <p>
                        We're so glad to have you.
                        To get set up, enter what times you want to start and end your tasks each day:
                    </p>
                    <div className={"time-entry"}>
                        <TimeEntry label={"Start Time"} value={startTime} change={setStartTime}/>
                        <TimeEntry label={"End Time"} value={endTime} change={setEndTime}/>
                    </div>
                    <button className="blue-button" id='onboard' onClick={handleClick}>get schedling!</button>
                </div>
            </div>
        </div>
    );
}

export default Index;