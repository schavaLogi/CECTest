"use strict";

import React from 'react';
import {connect} from 'react-redux';

import {Keyboard, StyleSheet, View} from 'react-native';

import PropTypes from 'prop-types';

import {Body, Button, Container, Content, Form, Header, Input, Item, Label, Text, Title} from 'native-base';

import locStrings from '../../localization';
import commonStyles from '../styles';

import {enableBTWithPermission} from '../util/BTUtils';
import {updateUserInfoStatus} from '../../actions/user';

export class MeProfileScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            firstName: '',
            lastName: ''
        };
    }

    componentWillUnmount() {
        console.log("MeProfileScreen componentWillUnmount");
    }

    componentDidMount() {
    }

    async onNextButton() {
        console.log("User clicked on Next button");
        Keyboard.dismiss();
        this.props.dispatch(updateUserInfoStatus(this.state));

        let isBtReady = await enableBTWithPermission();
        if (isBtReady) {
            this.props.navigation.navigate("HarmonyRemote");
        }
    }

    render() {
        console.log("Me profile Render");
        return (
            <Container style={commonStyles.container}>
                <Header>
                    <Body style={commonStyles.headerBody}>
                    <Title>{locStrings.your_name}</Title>
                    </Body>
                </Header>

                <Content keyboardShouldPersistTaps={'handled'} style={profileStyles.container}>
                    <Text style={profileStyles.textDesc}>
                        {locStrings.me_desc}
                    </Text>

                    <Form>

                        <Item stackedLabel>

                            <Label>{locStrings.first_name}</Label>

                            <Input
                                  autoCapitalize='words'
                                onChangeText={(text) => this.setState({firstName: text})}
                                blurOnSubmit={false}
                                value={this.state.firstName}
                                autoFocus={true}
                                maxLength={20}
                                placeholder={locStrings.name_placeHolder}
                                placeholderTextColor="gray"
                                returnKeyType='next'/>
                        </Item>

                        <Item stackedLabel last>

                            <Label>{locStrings.last_name}</Label>

                            <Input
                                autoCapitalize='words'
                                onChangeText={(text) => this.setState({lastName: text})}
                                blurOnSubmit={true}
                                value={this.state.lastName}
                                placeholder={locStrings.name_placeHolder}
                                placeholderTextColor="gray"
                                returnKeyType='done'
                                maxLength={20}/>
                        </Item>
                    </Form>
                    <View>
                        {(this.state.firstName.length >= 4 && this.state.lastName.length >= 4) ?
                            <Button block primary title={''} onPress={() => {
                                this.onNextButton()
                            }}>
                                <Text>{locStrings.next}</Text>
                            </Button> :
                            <Button disabled block title={''}>
                                <Text>{locStrings.next}</Text>
                            </Button>}
                    </View>

                </Content>
            </Container>
        )
    }
}

MeProfileScreen.propTypes = {
    dispatch: PropTypes.func.isRequired,
    navigation: PropTypes.object.isRequired,
};

const profileStyles = StyleSheet.create({
    container: {
        padding: 10
    },
    textDesc: {
        padding: 10,
        fontWeight: 'bold'
    }
});

export default connect()(MeProfileScreen);