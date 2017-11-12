import React from 'react';
import {connect} from 'react-redux';
import {Image, NativeModules, StyleSheet, View} from 'react-native';
import {
    Body,
    Button,
    Container,
    Content,
    Header,
    Icon,
    Left,
    Right,
    Text,
    Title
} from 'native-base';
import CustomSpinner from './CustomSpinner';
import {loginSuccess, logOut} from '../../actions/auth';

import locStrings from '../../localization';
import commonStyles from '../styles';

//import { getDevices, signIn } from '../../Api/Api';

const lipModule = NativeModules.LipModule;

class HarmonyRemoteScreen extends React.Component {
    constructor() {
        super();
        this.state = {
            showProgress: false,
        };
    }

    componentWillUnmount() {
        console.log('HarmonyRemoteScreen componentWillUnmount');
    }

    async fetchDevices() {
        this.setState({showProgress: false});
        this.props.navigation.navigate('HDMIInputs');
    }

    async requestLogin() {
        try {
            const response = await lipModule.requestLogin({create: false});
            console.log('newly fulfilled:', response);

            if (response !== null && response.accountToken !== null) {
                console.log(`newly response.accountToken:${response.accountToken.accessToken}`);
                /*
                try {
                    console.log('newly signIn:', signIn);

                    signIn(response.accountToken)
                        .then(authResp => getDevices(response.accountToken, authResp.AccountId))
                        .then((devices) => {
                            console.log(`response =${devices}`);
                        });
                } catch (error) {
                    console.log(`newly error:${error}`);
                }
                */

                this.props.dispatch(loginSuccess(response.accountToken));


                this.setState({showProgress: true});

                setTimeout(this.fetchDevices.bind(this), 4000);
            }
        } catch (e) {
            this.props.dispatch(logOut());
            console.log('newly Error received', e);
        }
    }

    render() {
        console.log('Harmony Remote render ');
        return (
            <Container style={commonStyles.container}>
                <Header>
                    <Left style={commonStyles.headerLeft}>
                        <Button
                            transparent
                            onPress={() => {
                                console.log('Pressed on back key');
                                this.props.navigation.goBack();
                            }}
                        >
                            <Icon name="arrow-back" />
                        </Button>
                    </Left>

                    <Body style={commonStyles.headerBody}>
                        <Title>{locStrings.harmony_remote}</Title>
                    </Body>

                    <Right style={commonStyles.headerRight} />

                </Header>

                <Content style={commonStyles.content}>

                    <Text style={remoteStyles.textDesc}>
                        {locStrings.harmony_remote_desc}
                    </Text>

                    <View style={remoteStyles.imgContainer}>

                        <Image
                            style={remoteStyles.img}
                            source={require('../images/harmony-ultimate-one.png')}
                        />
                    </View>


                    <View style={remoteStyles.buttonContainer}>

                        <Button
                            block
                            info
                            style={remoteStyles.buttonBlock}
                            onPress={() => {
                                this.props.navigation.navigate('HDMIInputs');
                            }}
                        >
                            <Text>{locStrings.harmony_account_no}</Text>
                        </Button>

                        <View style={remoteStyles.buttongap} />

                        <Button
                            block
                            primary
                            style={remoteStyles.buttonBlock}
                            onPress={() => {
                                this.requestLogin();
                            }}
                        >
                            <Text>{locStrings.harmony_account_yes}</Text>
                        </Button>
                    </View>

                </Content>

                {this.state.showProgress &&
                <View style={remoteStyles.loading}>
                    <CustomSpinner
                        visible={this.state.showProgress}
                        textContent="Loading..."
                        textStyle={remoteStyles.loaderText}
                    />
                </View>}
            </Container>
        );
    }
}

const loaderBackground = '#FFFFFF7F';
const loaderTextColor = '#FFFFFF';

const remoteStyles = StyleSheet.create({
    imgContainer: {
        padding: 10,
        justifyContent: 'center',
        alignItems: 'center',
    },
    img: {
        padding: 10,
        justifyContent: 'center',
        alignItems: 'center',
        width: 200,
        height: 200,
    },
    textDesc: {
        padding: 10,
        fontWeight: 'bold',
    },
    buttonContainer: {
        padding: 10,
        flexDirection: 'row',
    },
    buttonBlock: {
        flex: 1,
    },
    buttongap: {
        flex: 0.1
    },
    loading: {
        position: 'absolute',
        left: 0,
        right: 0,
        top: 0,
        bottom: 0,
        backgroundColor: loaderBackground,
        alignItems: 'center',
        justifyContent: 'center',
    },
    loaderText: {
        color: loaderTextColor
    }
});
export default connect()(HarmonyRemoteScreen);
