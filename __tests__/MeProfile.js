import 'react-native';
import React from 'react';
import MeProfile , {MeProfileScreen} from '../cec/screens/MeProfile'
import locStrings from '../localization';

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';

// Mock store
import configureStore from 'redux-mock-store';
import {shallow , configure } from 'enzyme';
import {Button, Icon, Input, Text} from "native-base";
import Adapter from 'enzyme-adapter-react-16';
configure({ adapter: new Adapter() });

const middlewares = []; // you can mock any middlewares here if necessary
const mockStore = configureStore(middlewares);
const initialState = {
    nav : {},
    navigation : {},
    user : {firstName: '', lastName: ''}
};

describe('MeProfileScreen Testing', () => {
    let wrapper;
    const defaultState = {firstName: '', lastName: ''};
    const props = {
        dispatch: jest.fn(),
        navigation : {}
    };

    beforeEach(() => {
        wrapper = shallow(<MeProfileScreen {...props}/>);
    });

    it('Render MeProfileScreen', () => {
        wrapper = shallow(<MeProfileScreen {...props}/>);
        expect(wrapper.dive()).toMatchSnapshot();
    });

    it('Connected Render MeProfileScreen', () => {
        wrapper = shallow(
            <MeProfile {...props}/>,
            { context: { store: mockStore(initialState) } },
        );
        expect(wrapper.dive()).toMatchSnapshot();
    });

    it('check Matched to Default state ', () => {
        const render = wrapper.dive();
        expect(wrapper.state()).toEqual(defaultState);
    })

    it('check We have 2 Input and 1 Button ', () => {
        expect(wrapper.state()).toEqual(defaultState);

        expect(wrapper.find(Button).length).toBe(1);

        expect(wrapper.find(Input).length).toBe(2);
    })

    it('check We have Text label and its text ', () => {

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


    it('Check Text getting changed after update text', () =>{
        const newTextValue = 'Hello';
        const modifiedState = {firstName: newTextValue, lastName: newTextValue};

        wrapper.setState(modifiedState);

        expect(wrapper.find(Input).get(1).props.value).toEqual(newTextValue);
        expect(wrapper.find(Input).get(0).props.value).toEqual(newTextValue);

        expect(wrapper.state()).toEqual(modifiedState);
    });


    it('check the Next button is enabled after Enter Input fileds', () => {
        let render = wrapper.dive();

        expect(render.find(Button).props().disabled).toEqual(true);

        const newTextValue = 'Hello';
        const modifiedState = {firstName: newTextValue, lastName: newTextValue};

        wrapper.setState(modifiedState);

        // get newly render as per latest state
        render = wrapper.dive();
        expect(wrapper.find(Button).length).toBe(1);

        expect(wrapper.state()).toEqual(modifiedState);

        expect(wrapper.find(Input).get(0).props.value).toEqual(newTextValue);
        expect(wrapper.find(Input).get(1).props.value).toEqual(newTextValue);

        console.log('props=' + render.find(Button).props());

        /*for (var prop in render.find(Button).props()) {
            console.log(prop);
        }*/

        expect(render.find(Button).props().primary).toEqual(true);
    });
});


