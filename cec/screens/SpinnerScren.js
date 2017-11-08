'use strict';

import React from 'react';
import PropTypes from 'prop-types';

import {StyleSheet, View} from 'react-native';
import {Container, Content, Spinner, Text} from 'native-base';

class SpinnerScreen extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Container style={spinnerStyles.container}>
                <Content contentContainerStyle={spinnerStyles.container}>
                    <View style={spinnerStyles.loading}>
                        <Text style={spinnerStyles.text}>
                            {'Pairing ...'}
                        </Text>
                        <Spinner color="green"/>
                    </View>
                </Content>
            </Container>
        );
    }
}

SpinnerScreen.propTypes = {
    showProgress: PropTypes.bool,
    text: PropTypes.string,
    onExit: PropTypes.func.isRequired,
};

SpinnerScreen.defaultProps = {
    showProgress: true,
    text: 'Pairing ...'
}

const spinnerStyles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'black',
        justifyContent: 'center',
        alignItems: 'center'
    },
    text: {
        color: 'green'
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

export default SpinnerScreen;
