import 'react-native';
import React from 'react';

// Mock store
import configureStore from 'redux-mock-store';
import {shallow , configure } from 'enzyme';
import {Button, Icon, Input, Text} from "native-base";
import Adapter from 'enzyme-adapter-react-16';

import MeProfile , {MeProfileScreen} from '../MeProfile'
import locStrings from '../../../localization';

import sinon from 'sinon';


configure({ adapter: new Adapter() });
const middlewares = []; // you can mock any middlewares here if necessary
const mockStore = configureStore(middlewares);
const initialState = {
    nav : {},
    navigation : {navigate : jest.fn() , dispatch: jest.fn()},
    user : {firstName: '', lastName: ''}
};

describe('MeProfileScreen Testing', () => {
    let wrapper;
    const defaultState = {firstName: '', lastName: ''};
    const props = {
        dispatch: jest.fn(),
        navigation : {navigate : jest.fn() , dispatch: jest.fn()}
    };

    beforeEach(() => {
        wrapper = shallow(<MeProfileScreen {...props}/>);
    });

    it('renders MeProfileScreen as expected', () => {
        expect(wrapper.dive()).toMatchSnapshot();
    });

    it('Connected Render MeProfileScreen', () => {
        wrapper = shallow(
            <MeProfile {...props}/>,
            { context: { store: mockStore(initialState) } },
        );
        expect(wrapper.dive()).toMatchSnapshot();

    });

    it('check state is matched to Default state ', () => {
        const render = wrapper.dive();
        expect(wrapper.state()).toEqual(defaultState);
    })

    it('check We have 2 Input and 1 Button ', () => {
        expect(wrapper.find(Button).length).toBe(1);
        expect(wrapper.find(Input).length).toBe(2);
    })

    it('check We have Text label with expected text', () => {

        expect(wrapper.find(Text).length).toBe(2);
        const render = wrapper.dive();
        const displayText = wrapper.find(Text).get(0).props.children;
        expect(displayText).toEqual(locStrings.me_desc);
    })


    it('check the Next button is disabled', () =>{
        const render = wrapper.dive();
        expect(render.find(Button).props().disabled).toEqual(true);
    });

    it('Check we have Input Placeholder and It\'s matching', () => {
        expect(wrapper.find(Input).get(0).props.placeholder).toEqual(locStrings.name_placeHolder);
        expect(wrapper.find(Input).get(1).props.placeholder).toEqual(locStrings.name_placeHolder);
    });

    it('Check Text getting changed after update state', () =>{
        const newTextValue = 'Hello';
        const modifiedState = {firstName: newTextValue, lastName: newTextValue};
        wrapper.setState(modifiedState);

        expect(wrapper.find(Input).get(1).props.value).toEqual(newTextValue);
        expect(wrapper.find(Input).get(0).props.value).toEqual(newTextValue);

        expect(wrapper.state()).toEqual(modifiedState);

        expect(wrapper.dive()).toMatchSnapshot();
    });

    it('check the Next button is enabled after Enter Input Fields', () => {
        let render = wrapper.dive();
        expect(render.find(Button).props().disabled).toEqual(true);

        const newTextValue = 'Hello';
        const modifiedState = {firstName: newTextValue, lastName: newTextValue};

        //Update state and check
        wrapper.setState(modifiedState);
        render = wrapper.dive();
        expect(wrapper.state()).toEqual(modifiedState);

        // Check Input Reflected
        expect(wrapper.find(Input).get(0).props.value).toEqual(newTextValue);
        expect(wrapper.find(Input).get(1).props.value).toEqual(newTextValue);

        // Check Button Enabled or not
        expect(render.find(Button).props().primary).toEqual(true);

        // Click on Next Button
        render.find(Button).forEach(child => {
            child.simulate('press');
        });
    });

    it('Enter Text Filed and check Button State', () =>{
        let render = wrapper.dive();
        const newTextValue = 'Hello';
        const modifiedState = {firstName: newTextValue, lastName: newTextValue};

        render.find(Input).forEach(child => {
            child.simulate('changeText', newTextValue);
        });
        expect(wrapper.state()).toEqual(modifiedState);
    })

    it('Check component unmounted successfully', ()=> {
        const spy = sinon.spy(MeProfileScreen.prototype, 'componentWillUnmount');
        expect(spy.calledOnce).toBe(false);
        wrapper.unmount();
        expect(spy.calledOnce).toBe(true);
        spy.restore();
    })
});


