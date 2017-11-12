'use strict';

import React from 'react';
import PropTypes from 'prop-types';
import {Dimensions, FlatList, StyleSheet, Text, View} from 'react-native';

import {Body, Button, Header, Icon, Left, Right, Title} from 'native-base';


import Commands from './mockdata';
import commonStyles from '../styles';

const {width} = Dimensions.get('window');

const equalWidth = ((width - 20) / 3 );


class GridItem extends React.PureComponent {

    constructor(props) {
        super(props);
    }

    _onPress() {
        this.props.onPressItem(this.props.item);
    }

    render() {
        return (
            <Button
                bordered
                dark
                style={gridStyles.item}
                androidRippleColor="#0A74F0"
                onPress={this._onPress.bind(this)}
                title=""
            >

                <Text
                    style={gridStyles.itemText}
                >{this.props.item.command}
                </Text>

            </Button>
        );
    }
}

GridItem.propTypes = {
    onPressItem: PropTypes.func.isRequired,
    item: PropTypes.object
};

const gridStyles = StyleSheet.create({
    item: {
        height: 50,
        width: equalWidth,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 5
    },
    itemText: {
        alignSelf: 'center',
        flexWrap: 'wrap'
    }
});

/**
 * Commands Screen , Which displays Available commands for a given device
 * input: Device must be passed as navigation params
 */
class CommandsListScreen extends React.Component {


    onPressItem = (item) => {
        console.log("User clicked on item =" + item.id)
    };
    renderRowItem = (itemData) => {
        return (
            <GridItem
                onPressItem={this.onPressItem}
                item={itemData.item}
            />
        );
    };

    constructor(props) {
        super(props);
        const {state} = this.props.navigation;
        this.state = {
            activeDevice: state.params.device
        };
    }

    getDeviceCommands() {
        if (typeof this.state.activeDevice !== 'undefined' && this.state.activeDevice !== null) {
            if (this.state.activeDevice.type === 'TV') {
                return Commands.tvCommands;
            } else if (this.state.activeDevice.type === 'PlayBack') {
                return Commands.playBackCommands;
            } else {
                return Commands.avAmplifierCommands;
            }
        }
    }

    keyExtractor(item, index) {
        return item.id;
    }

    render() {
        return (
            <View style={commandStyles.container}>
                <Header>
                    <Left style={commonStyles.headerLeft}>
                        <Button
                            transparent
                            onPress={() => {
                                this.props.navigation.goBack();
                            }}
                        >
                            <Icon name='arrow-back' />
                        </Button>
                    </Left>

                    <Body style={commonStyles.headerBody}>
                        <Title>
                            {typeof this.state.activeDevice !== 'undefined' && this.state.activeDevice !== null &&
                        this.state.activeDevice.make + ' ' + this.state.activeDevice.model}
                        </Title>
                    </Body>
                    <Right style={commonStyles.headerRight} />

                </Header>

                <FlatList
                    contentContainerStyle={commandStyles.listContainer}
                    data={this.getDeviceCommands()}
                    numColumns={3}
                    keyExtractor={this.keyExtractor}
                    renderItem={this.renderRowItem}
                />
            </View>);
    }

}

CommandsListScreen.propTypes = {
    navigation: PropTypes.object.isRequired,
};

export default CommandsListScreen;

const commandStyles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'column'
    },
    listContainer: {
        padding: 10
    }
});


