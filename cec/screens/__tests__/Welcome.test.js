import 'react-native';
import React from 'react';
import Welcome , {WelcomeScreen} from '../Welcome'

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';

// Mock store
import configureStore from 'redux-mock-store';
import { shallow, configure } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
configure({ adapter: new Adapter() });

// UI Dependency
import {Button, Icon, Text} from "native-base";
import locStrings from "../../../localization";
import * as sinon from "sinon";

const middlewares = []; // you can mock any middlewares here if necessary
const mockStore = configureStore(middlewares);
const initialState = {
    nav : {routes: [{routeName: 'Welcome', key : 'id_1eqweqwe'}], index : 0},
    net : {isConnected : false}
};

describe('WelcomeScreen Testing', () => {
    let wrapper;
    let spy;
    const props = {
        nav: {routes: [{routeName: 'Welcome', key : 'id_1eqweqwe'}], index : 0},
        navigation : {navigate : jest.fn()},
        isConnected : true
    };

    beforeEach(() => {
        wrapper = shallow(<WelcomeScreen {...props}/>, { lifecycleExperimental: true });
        spy = sinon.spy(WelcomeScreen.prototype, 'componentDidUpdate');
    });

    afterEach(() =>{
        spy.restore();
    });


    it('renders WelcomeScreen as expected', () => {
        expect(wrapper.dive()).toMatchSnapshot();
    });

    it('Check Welcome screen content Text with 2 Buttons', () =>{
        const render = wrapper.dive();

        expect(wrapper.find(Button).length).toBe(2);

        expect(wrapper.find(Text).length).toBe(2);

        const displayText = wrapper.find(Text).get(0).props.children;

        expect(displayText).toEqual(locStrings.welcome_desc);


        expect(wrapper.find(Icon).length).toBe(1);

        for (var prop in render.find(Icon).props()) {
            console.log(prop);
        }

        expect(render.find(Icon).props().name).toEqual('settings');
    });


    it('renders WelcomeScreen as expected after alter state', () => {
        wrapper.setProps({...props, isConnected : false});
        const render = wrapper.dive();

        expect(spy.calledOnce).toBe(true);

        render.find(Button).forEach(child => {
            child.simulate('click');
        });
    });
});



it('Render Connected WelcomeScreen as expected', () => {
    const props = {
        navigation : {navigate : jest.fn()},
    };
    const wrapper = shallow(
        <Welcome {...props}/>,
        { context: { store: mockStore(initialState) } }
    );
    expect(wrapper.dive()).toMatchSnapshot();
});


