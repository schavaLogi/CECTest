/**
 * CECTest React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React from 'react';

import {Provider} from 'react-redux';
import {applyMiddleware, createStore} from 'redux';
import logger from 'redux-logger';
import codePush from 'react-native-code-push';
import AppReducer from './reducers/index';

import AppNavigator from './AppNavigator';


const store = createStore(AppReducer, applyMiddleware(logger));

class AppComponent extends React.Component {
    componentDidMount(){
    }

    render() {
        return (
            <Provider store={store}>
                <AppNavigator />
            </Provider>
        );
    }
}

/* Check Update When app Resumes and Install new version upon next restart*/
let codePushOptions = {
    checkFrequency: codePush.CheckFrequency.ON_APP_RESUME,
    InstallMode: codePush.InstallMode.ON_NEXT_RESTART
};

const App = codePush(codePushOptions)(AppComponent);

export default App;