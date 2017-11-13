import 'react-native';
import React from 'react';
// Mock store
import configureStore from 'redux-mock-store';
import { configure, shallow } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import Welcome, { WelcomeScreen } from '../Welcome';
// Note: test renderer must be required after react-native.
// UI Dependency
import { Button, Icon, Text } from 'native-base';
import locStrings from '../../../localization';
import sinon from 'sinon';

configure({ adapter: new Adapter() });


const middlewares = []; // you can mock any middlewares here if necessary
const mockStore = configureStore(middlewares);
const initialState = {
    nav: { routes: [{ routeName: 'Welcome', key: 'id_1eqweqwe' }], index: 0 },
    net: { isConnected: false },
};

describe('WelcomeScreen Testing', () => {
    let wrapper;
    let spy;


    const props = {
        nav: { routes: [{ routeName: 'Welcome', key: 'id_1eqweqwe' }], index: 0 },
        navigation: { navigate: jest.fn(), dispatch: jest.fn() },
        isConnected: true,
    };

    beforeEach(() => {
        wrapper = shallow(<WelcomeScreen {...props} />, { lifecycleExperimental: true });
        spy = jest.spyOn(WelcomeScreen.prototype, 'componentDidUpdate');
    });

    afterEach(() => {
        spy.mockReset();
        spy.mockRestore();
    });


    it('renders WelcomeScreen as expected', () => {
        expect(wrapper.dive()).toMatchSnapshot();
    });

    it('Check Welcome screen content Text with 2 Buttons', () => {
        const render = wrapper.dive();

        // Check Rendered View count (Button, Text, Icon)
        expect(wrapper.find(Button).length).toBe(2);
        expect(wrapper.find(Text).length).toBe(2);
        expect(wrapper.find(Icon).length).toBe(1);


        // Check Welcome text description
        const displayText = wrapper.find(Text).get(0).props.children;
        expect(displayText).toEqual(locStrings.welcome_desc);

        // Check Icon name property
        expect(render.find(Icon).props().name).toEqual('settings');

    /* for (var prop in render.find(Icon).props()) {
            console.log(prop);
        } */
    });


    it('Click on Next Button when not Connected state', () => {
        wrapper.setProps({ ...props, isConnected: false });
        const render = wrapper.dive();

        render.find(Button).forEach((child) => {
            child.simulate('press');
        });
    });


    it('Click on Next Button with Connected state', () => {
        wrapper.setProps({ ...props, isConnected: true });
        const render = wrapper.dive();

        render.find(Button).forEach((child) => {
            child.simulate('press');
        });
    });

    it('Check Lifecycle Api triggered when props changes', () => {
        wrapper.setProps({ ...props, isConnected: false });
        wrapper.dive();
        expect(spy).toHaveBeenCalled();
    });
});


it('Render Connected WelcomeScreen as expected', () => {
    const props = {
        navigation: { navigate: jest.fn() },
    };
    const wrapper = shallow(
        <Welcome {...props} />,
        { context: { store: mockStore(initialState) } },
    );
    expect(wrapper.dive()).toMatchSnapshot();
});

