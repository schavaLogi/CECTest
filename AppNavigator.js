import React from 'react';
import {connect} from 'react-redux';
import {BackHandler, NetInfo} from 'react-native';
import {addNavigationHelpers, NavigationActions} from 'react-navigation';
import Root from './Root';
import {updateNetStatus} from './actions/network';

class AppNavigator extends React.Component {

    shouldCloseApp = nav => {
        if (nav.index > 0) return false;

        if (nav.routes) {
            return nav.routes.every(this.shouldCloseApp);
        }

        return true;
    };

    goBack = () => this.props.dispatch(NavigationActions.back());

    handleBackPress = () => {
        if (this.shouldCloseApp(this.props.nav)) {
            return false
        }
        this.goBack();
        return true
    };

    handleConnectionInfoChange = (isConnected) => {
        console.log("handleConnectionInfoChange isConnected =" + isConnected);
        this.props.dispatch(updateNetStatus(isConnected));
    };

    componentWillMount() {
        console.log("AppNavigator componentWillMount >>>");
        BackHandler.addEventListener("hardwareBackPress", this.handleBackPress)
    }

    componentDidMount() {
        NetInfo.isConnected.addEventListener('connectionChange', this.handleConnectionInfoChange);
    }

    componentWillUnmount() {
        console.log("AppNavigator componentWillUnmount <<<")
        BackHandler.removeEventListener("hardwareBackPress", this.handleBackPress);

        NetInfo.isConnected.removeEventListener('connectionChange', this.handleConnectionInfoChange);
    }

    render() {
        return (
            <Root
                navigation={addNavigationHelpers({
                    dispatch: this.props.dispatch,
                    state: this.props.nav
                })}
            />
        );
    }
}

const mapStateToProps = state => {
    return {
        nav: state.nav,
        net: state.net
    }
};

export default connect(mapStateToProps)(AppNavigator);