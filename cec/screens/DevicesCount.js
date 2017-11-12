import React from 'react';
import {Dimensions, StyleSheet} from 'react-native';
import {
    Body,
    Button,
    Container,
    Content,
    Form,
    Header,
    Icon,
    Left,
    Picker,
    Right,
    Text,
    Title,
    View,
} from 'native-base';

import locStrings from '../../localization/index';
import commonStyles from '../styles/index';

const PickerItem = Picker.Item;

const {width} = Dimensions.get('window');

class DevicesCountScreen extends React.Component {

    static removeSelectedKey(labels, key) {
        const obj = Object.assign({}, labels);
        delete obj[key];
        return obj;
    }

    constructor(props) {
        super(props);

        this.state = {
            selectedCount: '0',
        };

        this.options = {
            0: 'Number of Devices',
            1: '1',
            2: '2',
            3: '3',
            4: '4',
            5: '5',
            6: '6',
            7: '7',
            8: '8',
            9: '9',
            10: '10',
            11: '11',
            12: '12',
            13: '13',
            14: '14',
        };
    }

    onValueChange(value) {
        this.setState({
            selectedCount: value,
        });
    }

    onNextButtonClick() {
        this.props.navigation.navigate('DeviceInfo', {deviceCount: parseInt(this.state.selectedCount)});
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
                        <Title>{locStrings.devices}</Title>
                    </Body>

                    <Right style={commonStyles.headerRight} />
                </Header>


                <Content style={devicesCountStyles.container}>
                    <Text style={devicesCountStyles.textDesc}>
                        {locStrings.devices_desc}
                    </Text>

                    <View style={devicesCountStyles.dummy} />

                    <Form style={devicesCountStyles.pickerView}>
                        <Picker
                            style={devicesCountStyles.pickerContainer}
                            iosHeader="Device Count"
                            mode="dropdown"
                            inLineLabel
                            selectedValue={this.state.selectedCount}
                            onValueChange={this.onValueChange.bind(this)}
                        >
                            {
                                Object.keys(this.state.selectedCount !== '0' ?
                                    DevicesCountScreen.removeSelectedKey(this.options, '0') : this.options).map(key => (
                                    <PickerItem label={this.options[key]} value={key} key={key} />))
                            }
                        </Picker>
                    </Form>

                    <View style={devicesCountStyles.dummy} />


                    <Button
                        block
                        primary
                        onPress={this.onNextButtonClick.bind(this)}
                        title={locStrings.next}
                    >
                        <Text>{locStrings.next}</Text>
                    </Button>

                </Content>
            </Container>
        );
    }
}

export default DevicesCountScreen;

const borderColor = 'gray';
const devicesCountStyles = StyleSheet.create({
    container: {
        padding: 10,
    },
    textDesc: {
        padding: 10,
        fontWeight: 'bold',
    },
    pickerContainer: {
        width: (width - 40),
        alignSelf: 'center',
        alignItems: 'center',
        justifyContent: 'center',
        borderWidth: 1,
    },
    pickerView: {
        borderColor: borderColor,
        borderWidth: 0.5
    },
    dummy: {
        height: 30,
        paddingBottom: 10,
    },
});

