import 'react-native';
import React from 'react';
import configureStore from 'redux-mock-store';
import Adapter from 'enzyme-adapter-react-16';
import { shallow, configure } from 'enzyme';

import AppNavigator from '../AppNavigator';

configure({ adapter: new Adapter() });


const middlewares = []; // you can mock any middlewares here if necessary
const mockStore = configureStore(middlewares);
const initialState = {
    nav: {},
    net: { isConnected: false },
};

it('renders Connected AppNavigator as expected', () => {
    const props = {
        navigation: { navigate: jest.fn() },
    };

    const store = mockStore(initialState);
    const wrapper = shallow(
        <AppNavigator {...props} />,
        { context: { store } },
    );
    expect(wrapper.dive()).toMatchSnapshot();
});
