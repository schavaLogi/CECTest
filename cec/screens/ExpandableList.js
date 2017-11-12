import React, {Component} from 'react';
import {StyleSheet, Text, View} from 'react-native';

import {Body, Button, Container, Content, Header, Icon, Left, Right, Title} from 'native-base';
import {connect} from 'react-redux';
import * as Animatable from 'react-native-animatable';
import Accordion from 'react-native-collapsible/Accordion';
import commonStyles from '../styles/index';


export class ExpandableListScreen extends Component {
    constructor(props) {
        super(props);
        this.state = {
            activeSection: false,
        };
    }

    _setSection(section) {
        this.setState({activeSection: section});
    }

    _onPress() {
        if (this.state.activeSection !== false) {
            this.props.navigation.navigate('CommandsList', {device: this.props.cecDevices[this.state.activeSection]});
        }
    }

    _renderHeader(section, i, isActive) {
        return (
            <Animatable.View
                duration={400}
                style={[listStyles.header, isActive ? listStyles.active : listStyles.inactive]}
                transition="backgroundColor"
            >

                <View style={listStyles.containerHeader}>

                    <Text style={listStyles.headerText}>{`${section.make}  ${section.model}`}</Text>

                    <Icon style={listStyles.icon} name={isActive ? 'arrow-up' : 'arrow-down'} />

                </View>

            </Animatable.View>
        );
    }

    _renderContent(section, i, isActive) {
        return (
            <Animatable.View
                duration={400}
                style={[listStyles.contentText, isActive ? listStyles.active : listStyles.inactive]}
                transition="backgroundColor"
            >

                <Animatable.Text
                    animation={isActive ? 'bounceIn' : undefined}
                >{section.getDeviceInfo()}
                </Animatable.Text>

                <Button
                    block
                    primary
                    onPress={() => {
                        this._onPress();
                    }}
                >
                    <Text style={listStyles.activeButton}>Commands</Text>
                </Button>

            </Animatable.View>
        );
    }

    render() {
        return (

            <Container style={commonStyles.container}>
                <Header>
                    <Left style={commonStyles.headerLeft}>
                        <Button
                            transparent
                            onPress={() => {
                                this.props.navigation.goBack();
                            }}
                        >
                            <Icon name="arrow-back" />
                        </Button>
                    </Left>

                    <Body style={commonStyles.headerBody}>
                        <Title>Cec Devices</Title>
                    </Body>

                    <Right style={commonStyles.headerRight} />
                </Header>


                <Content
                    style={[commonStyles.container, listStyles.container]}
                    contentContainerStyle={listStyles.contentContainer}
                >

                    <Button
                        block
                        primary
                        onPress={() => {
                            this.props.navigation.navigate(
                                'CommandsList',
                                {device: this.props.cecDevices[this.state.activeSection]},
                            );
                        }}
                    >
                        <Text style={listStyles.activeButton}>Save & Email</Text>
                    </Button>


                    <Accordion
                        activeSection={this.state.activeSection}
                        sections={this.props.cecDevices}
                        renderHeader={this._renderHeader.bind(this)}
                        renderContent={this._renderContent.bind(this)}
                        duration={400}
                        onChange={this._setSection.bind(this)}
                    />

                    <View style={listStyles.bottomGap} />

                </Content>
            </Container>

        );
    }
}

const mapStateToProps = state => ({
    cecDevices: state.cecDevices,
});

export default connect(mapStateToProps)(ExpandableListScreen);

const borderColor = 'black';
const activeColor = 'rgba(245,255,255,1)';
const inActiveColor = 'rgba(255,255,255,1)';
const colorWhite = 'white';
const listStyles = StyleSheet.create({
    container: {
        padding: 10,
    },
    contentContainer: {
        flexGrow: 1,
        justifyContent: 'flex-start',
    },
    containerHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderColor: borderColor,
        borderBottomWidth: 0.5,
        height: 40,
        padding: 10,
    },
    icon: {
        width: 32,
        height: 32,
    },
    header: {
        justifyContent: 'flex-start',
    },
    headerText: {
        textAlign: 'center',
        fontSize: 16,
        fontWeight: '500',
    },
    contentText: {
        justifyContent: 'flex-start',
        padding: 20,
    },
    active: {
        borderBottomWidth: 0.5,
        borderColor: borderColor,
        backgroundColor: activeColor,
    },
    inactive: {
        backgroundColor: inActiveColor,
    },
    activeButton: {
        fontWeight: 'bold',
        color: colorWhite,
    },
    bottomGap: {
        height: 30,
        paddingBottom: 10,
    },
});
