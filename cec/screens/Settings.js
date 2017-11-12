import React from 'react';
import {connect} from 'react-redux';

import {StyleSheet, View} from 'react-native';
import {
    Body,
    Button,
    Container,
    Content,
    Header,
    Icon,
    Left,
    ListItem,
    Radio,
    Right,
    Text,
    Title
} from 'native-base';
import commonStyles from '../styles/index';
import locStrings from '../../localization/index';


import {SETTINGS_DONGLE, SETTINGS_RASPBERRY_PI, updateSettings} from '../../actions/settings';

class Settings extends React.Component {
    onDevicesButtonClick() {
        this.props.navigation.navigate('ExpandableList');
    }

    onSelectionChanged(id) {
        console.log(`Checked id =${id}`);
        this.props.dispatch(updateSettings(id));
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
                        <Title>{locStrings.settings}</Title>
                    </Body>

                    <Right style={commonStyles.headerRight} />
                </Header>

                <Content style={settingsStyles.container}>
                    <ListItem
                        style={settingsStyles.listItem}
                        selected={this.props.selected === SETTINGS_RASPBERRY_PI}
                        onPress={() => this.onSelectionChanged(SETTINGS_RASPBERRY_PI)}
                    >
                        <Text style={settingsStyles.text}>{locStrings.raspberry_pi}</Text>
                        <Right style={settingsStyles.radio}>
                            <Radio
                                selected={this.props.selected === SETTINGS_RASPBERRY_PI}
                                onPress={() => this.onSelectionChanged(SETTINGS_RASPBERRY_PI)}
                            />
                        </Right>
                    </ListItem>

                    <ListItem
                        style={settingsStyles.listItem}
                        selected={this.props.selected === SETTINGS_DONGLE}
                        onPress={() => this.onSelectionChanged(SETTINGS_DONGLE)}
                    >
                        <Text style={settingsStyles.text}>{locStrings.dongle}</Text>
                        <Right style={settingsStyles.radio}>
                            <Radio
                                selected={this.props.selected === SETTINGS_DONGLE}
                                onPress={() => this.onSelectionChanged(SETTINGS_DONGLE)}
                            />
                        </Right>
                    </ListItem>

                    <View style={settingsStyles.dummyView} />

                    <Button
                        block
                        primary
                        onPress={this.onDevicesButtonClick.bind(this)}
                        title={locStrings.view_cec_devices}
                    >
                        <Text>{locStrings.view_cec_devices}</Text>
                    </Button>
                </Content>
            </Container>
        );
    }
}

const mapStateToProps = state => ({
    selected: state.settings.selected,
});

export default connect(mapStateToProps)(Settings);

const settingsStyles = StyleSheet.create({
    container: {
        padding: 10,
    },
    listItem: {
        flex: 1,
        justifyContent: 'space-between',
    },
    text: {
        justifyContent: 'flex-start',
    },
    radio: {
        justifyContent: 'flex-end',
    },

    dummyView: {
        height: 30,
        paddingBottom: 10,
    },
});
