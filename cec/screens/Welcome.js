"use strict";

import React from 'react';
import {connect} from 'react-redux'
import PropTypes from 'prop-types';
import {Alert, Image, NetInfo, Platform, StyleSheet, View} from 'react-native';

import {NavigationActions} from 'react-navigation';

import {Body, Button, Container, Content, Header, Icon, Left, Right, Text, Title} from 'native-base'


import SendIntentAndroid from 'react-native-send-intent'
import locStrings from '../../localization';
import commonStyles from '../styles';

export class WelcomeScreen extends React.Component {

    constructor(props) {
        super(props);
    }

    componentWillMount() {
        console.log("WelcomeScreen componentWillMount");
    }

    componentDidMount() {
        console.log("WelcomeScreen componentDidMount");
    }

    componentWillReceiveProps(nextProps, nextContext) {
        console.log("WelcomeScreen componentWillReceiveProps" + nextProps + nextContext);
    }

    shouldComponentUpdate(nextProps, nextState, nextContext) {
        console.log("WelcomeScreen shouldComponentUpdate" + nextProps + nextState + nextContext);

        const currentRoute = this.props.nav.routes[this.props.nav.index];

        // Check we are active screen or not
        if (currentRoute.routeName !== 'Welcome') return false;
        console.log("currentRoute =" + currentRoute.routeName + "key =" + currentRoute.key);

        return this.props.isConnected !== nextProps.isConnected;
    }


    showAlertDialog() {
        Alert.alert('No Internet connection',
            'Connect to wifi by going through settings App',
            [{
                text: 'OK',
                onPress: () => {
                    console.log('OK Pressed');
                    if (Platform.OS === 'android') {
                        SendIntentAndroid.openSettings("android.settings.SETTINGS")
                    }
                }
            }], {cancelable: false});
    }

    componentDidUpdate(prevProps, prevState, prevContext) {
        console.log("WelcomeScreen componentDidUpdate" + prevProps + prevState + prevContext);

        if (!this.props.isConnected) {
            this.showAlertDialog();
        }
    }

    componentWillUnmount() {
        console.log("WelcomeScreen componentWillUnmount");
    }

    async onNextButtonClick() {
        try {
            let connected = await NetInfo.isConnected.fetch();

            if (!connected) {
                this.showAlertDialog();
            } else {
                const profileAction = NavigationActions.reset({
                    index: 0,
                    actions: [
                        NavigationActions.navigate({routeName: 'MeProfile'})
                    ]
                });
                this.props.navigation.dispatch(profileAction)
            }
        } catch (err) {
            console.warn("NetInfo connection check failed err=" + err);
        }
    }

    render() {
        console.log("WelcomeScreen render");
        return (
            <Container style={commonStyles.container}>
                <Header>
                    <Left style={commonStyles.headerLeft}/>

                    <Body style={commonStyles.headerBody}>
                    <Title>{locStrings.welcome}</Title>
                    </Body>

                    <Right style={commonStyles.headerRight}>
                        <Button transparent title={''}
                                onPress={() => {
                                    this.props.navigation.navigate("Settings")
                                }}>
                            <Icon name="settings"/>
                        </Button>
                    </Right>
                </Header>

                <Content style={welcomeStyles.container}>

                    <Text style={welcomeStyles.textDesc}>
                        {locStrings.welcome_desc}
                    </Text>

                    <View style={welcomeStyles.imgContainer}>

                        <Image style={welcomeStyles.img}
                               source={require('../images/002-wifi-connection-signal-symbol.png')}/>

                    </View>

                    <Button block primary onPress={this.onNextButtonClick.bind(this)} title={locStrings.next}>
                        <Text>{locStrings.next}</Text>
                    </Button>

                    <View style={welcomeStyles.bottom}>

                    </View>

                </Content>
            </Container>
        )
    }
}

WelcomeScreen.propTypes = {
    isConnected: PropTypes.bool,
    navigation: PropTypes.object.isRequired,
    nav: PropTypes.object.isRequired
};

const welcomeStyles = StyleSheet.create({
    container: {
        padding: 10
    },
    imgContainer: {
        padding: 10,
        justifyContent: 'center',
        alignItems: 'center'
    },
    img: {
        padding: 10,
        justifyContent: 'center',
        alignItems: 'center',
        width: 200,
        height: 200
    },
    textDesc: {
        padding: 10,
        fontWeight: 'bold'
    },
    bottom: {
        height: 30,
        paddingBottom: 10
    }
});

const mapStateToProps = state => {
    return {
        isConnected: state.net.isConnected,
        nav: state.nav,
    }
};

export default connect(mapStateToProps)(WelcomeScreen);

