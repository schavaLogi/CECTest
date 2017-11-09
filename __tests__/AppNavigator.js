import 'react-native';
import React from 'react';
import Welcome , {WelcomeScreen} from '../cec/screens/Welcome'

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';

// Mock store
import configureStore from 'redux-mock-store';
import { shallow, configure } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
configure({ adapter: new Adapter() });


import {Button, Icon} from "native-base";

const middlewares = []; // you can mock any middlewares here if necessary
const mockStore = configureStore(middlewares);
const initialState = {
    nav : {},
    net : {isConnected : false}
};

it('should display WelcomeScreen', () => {
    // This is where we create a mock state/props
    const props = {
        nav: {},
        navigation : {},
        isConnected : true
    };
    const wrapper = shallow(<WelcomeScreen {...props} />);
    console.log("to =" + wrapper.find('settings').toString() +  "    "+expect(wrapper.find('settings')).to);//.length(1);

});

it('renders WelcomeScreen as expected', () => {
    const wrapper = shallow(
        <Welcome />,
        { context: { store: mockStore(initialState) } },
    );
    expect(wrapper.dive()).toMatchSnapshot();
});



it('new renders WelcomeScreen as expected', () => {
    const wrapper = shallow(
        <Welcome />,
        { context: { store: mockStore(initialState) } },
    );
    expect(wrapper.dive()).toMatchSnapshot();

});

it('renders WelcomeScreen as expected after alter state', () => {
    const wrapper = shallow(
        <Welcome />,
        { context: { store: mockStore(initialState) } },
    );
    expect(wrapper.dive()).toMatchSnapshot();

    const render = wrapper.dive();

    render.find(Icon).forEach(child => {
        child.simulate('click');
    });

    render.find(Button).forEach(child => {
        child.simulate('click');
    });
});
