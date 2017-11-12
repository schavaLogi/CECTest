import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {Dimensions, Keyboard, StyleSheet, View} from 'react-native';

import {
    Body,
    Button,
    Container,
    Content,
    Form,
    Header,
    Icon,
    Input,
    Item,
    Left,
    Picker,
    Right,
    Text,
    Title,
} from 'native-base';

import locStrings from '../../localization';
import commonStyles from '../styles';

import {updateUserDevice} from '../../actions/userDevices';
import {Device} from '../model/Device';

const PickerItem = Picker.Item;
const {width} = Dimensions.get('window');

/**
 * Expects the deviceCount via navigation params
 */
class DeviceInfoScreen extends React.Component {

    static removeSelectedKey(labels, key) {
        const obj = Object.assign({}, labels);
        delete obj[key];
        return obj;
    }

    constructor(props) {
        super(props);
        const {state} = this.props.navigation;
        this.state = {
            selected: '0',
            make: '',
            model: '',
            deviceCount: state.params.deviceCount,
            currDevice: 1,
        };

        this.focusNextField = this.focusNextField.bind(this);
        this.inputs = {};
        this.options = {
            0: locStrings.choose_device_type,
            1: locStrings.tv,
            2: locStrings.avr,
            3: locStrings.playback,
        };
    }

    onValueChange(value) {
        this.setState({
            selected: value,
        });
    }

    onNextButtonClick() {
        const device = new Device(
            this.state.make, this.state.model,
            this.options[this.state.selected], this.state.currDevice,
        );

        this.props.dispatch(updateUserDevice(device));

        if (this.state.currDevice === this.state.deviceCount) {
            Keyboard.dismiss();
            this.props.navigation.navigate('ExpandableList');
        } else {
            this.setState({
                currDevice: this.state.currDevice + 1,
                selected: '0',
                make: '',
                model: '',
            });
        }
    }

    focusNextField(id) {
        this.inputs[id].focus();
    }

    render() {
        const TITLE = `${locStrings.device} ${this.state.currDevice} of ${this.state.deviceCount}`;
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
                        <Title>{TITLE}</Title>
                    </Body>

                    <Right style={commonStyles.headerRight} />

                </Header>

                <Content keyboardShouldPersistTaps="handled" style={commonStyles.content}>

                    <Text style={deviceInfoStyles.textDesc}>
                        {locStrings.device_desc}
                    </Text>

                    <Form>
                        <View style={deviceInfoStyles.pickerView}>
                            <Picker
                                style={deviceInfoStyles.pickerContainer}
                                iosHeader="Device Type"
                                mode="dropdown"
                                inLineLabel
                                selectedValue={this.state.selected}
                                onValueChange={this.onValueChange.bind(this)}
                            >
                                {
                                    Object.keys(this.state.selected !== '0' ? DeviceInfoScreen.removeSelectedKey(this.options, '0') : this.options).map(key => (
                                        <PickerItem
                                            label={this.options[key]}
                                            value={key}
                                            key={key}
                                        />))
                                }
                            </Picker>
                        </View>
                        <Item>
                            <Input
                                ref={(input) => {
                                    this.inputs.make = input;
                                }}
                                autoCapitalize="words"
                                onSubmitEditing={() => {
                                    this.focusNextField('model');
                                }}
                                editable={this.state.selected !== '0'}
                                selectTextOnFocus={this.state.selected !== '0'}
                                onChangeText={text => this.setState({make: text})}
                                blurOnSubmit={false}
                                value={this.state.make}
                                maxLength={20}
                                placeholder={locStrings.make}
                                placeholderTextColor="gray"
                                returnKeyType="next"
                            />
                        </Item>

                        <Item last>
                            <Input
                                ref={(input) => {
                                    this.inputs.model = input;
                                }}
                                autoCapitalize="words"
                                editable={this.state.selected !== '0'}
                                selectTextOnFocus={this.state.selected !== '0'}
                                onChangeText={text => this.setState({model: text})}
                                blurOnSubmit={false}
                                value={this.state.model}
                                placeholder={locStrings.model}
                                placeholderTextColor="gray"
                                returnKeyType="done"
                                maxLength={20}
                            />
                        </Item>
                    </Form>

                    <View style={deviceInfoStyles.dummy} />

                    <View>
                        {this.state.model.length >= 2 && this.state.make.length >= 2 ?
                            <Button
                                block
                                primary
                                onPress={() => {
                                    Keyboard.dismiss();
                                    this.onNextButtonClick();
                                }}
                            >
                                <Text>{locStrings.next}</Text>
                            </Button> :
                            <Button disabled block><Text>{locStrings.next}</Text></Button>}
                    </View>

                    <View style={deviceInfoStyles.transButtonContainer}>
                        <Button
                            transparent
                            info
                            onPress={() => {
                                Keyboard.dismiss();
                                this.props.navigation.dispatch({type: 'Reset'});
                            }}
                        >
                            <Text>{locStrings.no_more_devices}</Text>
                        </Button>
                    </View>

                </Content>
            </Container>
        );
    }
}

const borderColor = 'gray';

const deviceInfoStyles = StyleSheet.create({
    textDesc: {
        padding: 10,
        fontWeight: 'bold',
    },
    pickerContainer: {
        width: (width - 40),
        alignSelf: 'center',
        alignItems: 'center',
        justifyContent: 'center',
        borderWidth: 1
    },

    pickerView: {
        borderColor: borderColor,
        borderWidth: 0.5
    },
    transButtonContainer: {
        padding: 10,
        justifyContent: 'center',
        alignItems: 'center',
        alignSelf: 'center',
    },
    dummy: {
        height: 30,
        paddingBottom: 10,
    },
});

DeviceInfoScreen.propTypes = {
    dispatch: PropTypes.func.isRequired,
};

export default connect()(DeviceInfoScreen);
