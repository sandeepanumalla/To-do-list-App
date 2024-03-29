import {applyMiddleware, createStore} from "redux";
import DrawerItemData from '../components/data/DrawerItemData'
import rootReducer from "../reducers/RootReducer";
import {thunk} from "redux-thunk";

export const store = createStore(rootReducer, applyMiddleware(thunk))