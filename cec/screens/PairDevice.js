'use strict';

import React from 'react';
import PropTypes from 'prop-types';

import {StyleSheet} from 'react-native';
import {Body, Container, Content, Header, Text, Title} from 'native-base';

import locStrings from '../../localization';
import commonStyles from '../styles';

class PairDevice extends React.Component {
    constructor(props) {
        super(props);
    }

    onNextButtonClick() {
        this.props.navigation.navigate("HarmonyRemote")
    }

    render() {
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
                    <Title>{locStrings.pair_test_device}</Title>
                    </Body>

                    <Right style={commonStyles.headerRight}/>
                </Header>


                <Content style={pairDeviceStyles.container}>
                    <Text style={pairDeviceStyles.textDesc}>
                        {locStrings.pair_test_desc}
                    </Text>

                    <Button block primary onPress={this.onNextButtonClick.bind(this)} title={locStrings.next}>
                        <Text>{locStrings.next}</Text>
                    </Button>

                </Content>
            </Container>);
    }
}

PairDevice.propTypes = {
    navigation: PropTypes.object.isRequired,
};

export default PairDevice;

const pairDeviceStyles = StyleSheet.create({
    container: {
        padding: 10
    },
    textDesc: {
        padding: 10,
        fontWeight: 'bold'
    }
});

