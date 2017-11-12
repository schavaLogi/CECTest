import React from 'react';
import {Image, Keyboard, StyleSheet, View} from 'react-native';

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

import locStrings from '../../localization';
import commonStyles from '../styles';

class HDMIInputScreen extends React.Component {
    componentWillUnmount() {
        console.log('HDMIInputScreen componentWillUnmount');
    }

    render() {
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
                        <Title>{locStrings.hdmi_inputs}</Title>
                    </Body>

                    <Right style={commonStyles.headerRight} />

                </Header>

                <Content style={commonStyles.content}>

                    <Text style={HDMIInputStyles.textDesc}>
                        {locStrings.hdmi_inputs_desc}
                    </Text>

                    <Text style={[HDMIInputStyles.textDesc, {
                        justifyContent: 'center',
                        alignSelf: 'center'
                    }]}
                    >
                        {locStrings.hdmi}
                    </Text>

                    <View style={HDMIInputStyles.imgContainer}>
                        <Image
                            style={HDMIInputStyles.img}
                            source={require('../images/hdmi.png')}
                        />
                    </View>

                    <View style={HDMIInputStyles.buttonContainer}>
                        <Button
                            block
                            info
                            style={{flex: 1}}
                            onPress={() => {
                                Keyboard.dismiss();
                                this.props.navigation.navigate('DevicesCount');
                            }}
                        >
                            <Text uppercase={false}>{locStrings.free_input_no}</Text>
                        </Button>

                        <View style={{flex: 0.1}} />

                        <Button
                            block
                            primary
                            style={{flex: 1}}
                            onPress={() => {
                                Keyboard.dismiss();
                                this.props.navigation.navigate('DevicesCount');
                            }}
                        >
                            <Text uppercase={false}>{locStrings.free_input_yes}</Text>
                        </Button>
                    </View>

                </Content>

            </Container>
        );
    }
}

const HDMIInputStyles = StyleSheet.create({
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
});

export default HDMIInputScreen;
