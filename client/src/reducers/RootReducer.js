import {combineReducers} from "redux";
import {taskSummaryReducers} from "./TaskSummaryReducer";
import userReducers from "./UserReducers";


const rootReducer = combineReducers({
    taskSummary: taskSummaryReducers,
    user: userReducers
})


export default rootReducer;