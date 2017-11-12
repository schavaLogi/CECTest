import 'react-native';
import React from 'react';
// Mock store
import configureStore from 'redux-mock-store';
import { configure, shallow } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';

// Note: test renderer must be required after react-native.
// UI Dependency
import { Button, Container, Icon, Title } from 'native-base';

import { CEC_DEVICES } from '../../model/mockCecDevices';
import ExpandableList, { ExpandableListScreen } from '../ExpandableList';

configure({ adapter: new Adapter() });

const middlewares = []; // you can mock any middlewares here if necessary
const mockStore = configureStore(middlewares);
const initialState = {
    dispatch: jest.fn(),
    cecDevices: CEC_DEVICES,
};

const props = {
    navigation: {
        navigate: jest.fn(), state: {}, dispatch: jest.fn(), goBack: jest.fn(),
    },
    dispatch: jest.fn(),
    cecDevices: CEC_DEVICES,
};

const defaultState = { activeSection: false };


describe('ExpandableListScreen Testing', () => {
    let wrapper;

    beforeEach(() => {
        wrapper = shallow(<ExpandableListScreen {...props} />);
    });

    it('renders ExpandableListScreen as expected', () => {
        expect(wrapper.dive()).toMatchSnapshot();
    });

    it('Connected Render ExpandableListScreen', () => {
        wrapper = shallow(
            <ExpandableList {...props} />,
            { context: { store: mockStore(initialState) } },
        );
        expect(wrapper.dive()).toMatchSnapshot();
    });

    it('Check screen must have Mandatory components Accordion , Title, Icon , Buttons', () => {
        const render = wrapper.dive();

        expect(wrapper.find('Accordion').length).toBe(1);
        expect(wrapper.type()).toEqual(Container);

        // Check Title & It's value
        expect(wrapper.find(Title).length).toBe(1);

        // Icon and it's name
        expect(wrapper.find(Icon).length).toBe(1);
        expect(render.find(Icon).props().name).toEqual('arrow-back');

        // Button count to press
        expect(wrapper.find(Button).length).toBe(2);
    });

    it('Check We are able to click on Buttons', () => {
        const render = wrapper.dive();
        render.find(Button).forEach((child) => {
            child.simulate('press');
        });
    });

    it('Check the component rendered with default state', () => {
        wrapper.setState(defaultState);
        const render = wrapper.dive();

        const childProps = render.find('Accordion').props();

        expect(childProps.activeSection).toEqual(false);
        expect(childProps.sections[1]).toEqual(initialState.cecDevices[1]);

        wrapper.instance()._onPress();
    });

    // This is Tweak to test entire component but we should test Down->Up instead of Up->Down
    it('Check we are able to click on Header so that We can render Content', () => {
        const render = wrapper.dive();
        // Note: Shallow will not render child components we can't click header
        // We are calling child prop method and identifying state changed or not
        const childProps = render.find('Accordion').props();
        childProps.onChange(3);

        // Trigger Render Header and Content using child props as Active
        childProps.renderHeader(childProps.sections[2], 2, true);
        childProps.renderContent(childProps.sections[2], 2, true);

        childProps.renderHeader(childProps.sections[2], 2, false);
        childProps.renderContent(childProps.sections[2], 2, false);

        // Check state changed after invoking child prop
        expect(wrapper.state()).toEqual({ activeSection: 3 });

        render.find(Button).forEach((child) => {
            child.simulate('press');
        });

        wrapper.instance()._onPress();
    });
});

