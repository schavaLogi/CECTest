/**
 * CECTest React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, {Component} from 'react';

import {Provider} from 'react-redux';
import {applyMiddleware, createStore} from 'redux';
import logger from 'redux-logger'
import AppReducer from "./reducers/index";

import AppNavigator from './AppNavigator'

import codePush from "react-native-code-push";

class App extends Component<{}> {
    store = createStore(AppReducer, applyMiddleware(logger));

    render() {
        return (
            <Provider store={this.store}>
                <AppNavigator/>
            </Provider>
        );
    }
}

/* Check Update When app Resumes and Install new version upon next restart*/
let codePushOptions = {
    checkFrequency: codePush.CheckFrequency.ON_APP_RESUME,
    InstallMode: codePush.InstallMode.ON_NEXT_RESTART
};

App = codePush(codePushOptions)(App);

export default App