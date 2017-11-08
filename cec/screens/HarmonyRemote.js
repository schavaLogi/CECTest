"use strict";

import React from 'react';
import {connect} from 'react-redux'
import {Image, NativeModules, StyleSheet, View} from 'react-native';
import {Body, Button, Container, Content, Header, Icon, Left, Right, Text, Title} from 'native-base';
import CustomSpinner from './CustomSpinner';
import {loginSuccess, logOut} from '../../actions/auth';

import locStrings from '../../localization';
import commonStyles from '../styles';

const lipModule = NativeModules.LipModule;

class HarmonyRemoteScreen extends React.Component {

    constructor() {
        super();
        this.state = {
            showProgress: false
        }
    }

    componentWillUnmount() {
        console.log("HarmonyRemoteScreen componentWillUnmount");
    }

    async fetchDevices() {
        this.setState({showProgress: false});
        this.props.navigation.navigate("HDMIInputs")
    }

    async requestLogin() {
        try {
            let response = await lipModule.requestLogin({create: false});
            console.log("newly fulfilled:", response);

            if (response !== null && response.accountToken !== null) {
                this.props.dispatch(loginSuccess(response.accountToken));
                this.setState({showProgress: true});

                setTimeout(this.fetchDevices.bind(this), 4000);
            }
        } catch (e) {
            this.props.dispatch(logOut());
            console.log("newly Error received", e);
        }
    }

    render() {
        console.log("Harmony Remote render ");
        return (
            <Container style={commonStyles.container}>
                <Header>
                    <Left style={commonStyles.headerLeft}>
                        <Button transparent onPress={() => {
                            console.log("Pressed on back key");
                            this.props.navigation.goBack()
                        }}>
                            <Icon name='arrow-back'/>
                        </Button>
                    </Left>

                    <Body style={commonStyles.headerBody}>
                    <Title>{locStrings.harmony_remote}</Title>
                    </Body>

                    <Right style={commonStyles.headerRight}/>

                </Header>

                <Content style={commonStyles.content}>

                    <Text style={RemoteStyles.textDesc}>
                        {locStrings.harmony_remote_desc}
                    </Text>

                    <View style={RemoteStyles.imgContainer}>

                        <Image style={RemoteStyles.img}
                               source={require('../images/harmony-ultimate-one.png')}/>
                    </View>


                    <View style={RemoteStyles.buttonContainer}>

                        <Button block info style={{flex: 1}} onPress={() => {
                            this.props.navigation.navigate("HDMIInputs")
                        }}>
                            <Text>{locStrings.harmony_account_no}</Text>
                        </Button>

                        <View style={{flex: 0.1}}></View>

                        <Button block primary style={{flex: 1}} onPress={() => {
                            this.requestLogin()
                        }}>
                            <Text>{locStrings.harmony_account_yes}</Text>
                        </Button>
                    </View>

                </Content>

                {/*Working code *//*{this.state.showProgress &&
                    <View style={RemoteStyles.loading}>
                        <Text style={{color : 'blue'}}>
                            {'Pairing ...'}
                        </Text>
                        <Spinner size='large' color='blue'/>
                    </View>
                }*/}

                {this.state.showProgress &&
                <View style={RemoteStyles.loading}>
                    <CustomSpinner visible={this.state.showProgress} textContent={"Loading..."}
                                   textStyle={{color: '#FFF'}}/>
                </View>}
            </Container>
        );
    }
}

const RemoteStyles = StyleSheet.create({
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
    buttonContainer: {
        padding: 10,
        flexDirection: 'row'
    },
    loading: {
        position: 'absolute',
        left: 0,
        right: 0,
        top: 0,
        bottom: 0,
        backgroundColor: '#FFFFFF7F',
        alignItems: 'center',
        justifyContent: 'center'
    }
});
export default connect()(HarmonyRemoteScreen);
