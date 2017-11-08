import {combineReducers} from 'redux';
import auth from './auth';
import nav from './navigation';
import net from './network'
import userInfo from './user'
import userDevices from './userDevices'
import harmonyDevices from './harmonyDevices'
import cecDevices from './cecDevices'
import settings from "./settings";

const AppReducer = combineReducers({
    nav,
    net,
    auth,
    userInfo,
    settings,
    userDevices,
    harmonyDevices,
    cecDevices
});

export default AppReducer;
