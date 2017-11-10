import 'react-native';
import React from 'react';
import App ,{AppNavigator}from '../AppNavigator';
// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';
import configureStore from 'redux-mock-store';
import { shallow, configure } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
configure({ adapter: new Adapter() });


const middlewares = []; // you can mock any middlewares here if necessary
const mockStore = configureStore(middlewares);
const initialState = {
    nav : {},
    net : {isConnected : false}
};

it('renders Connected AppNavigator as expected', () => {

    const props = {
        navigation : {navigate : jest.fn()}
    };

    let store = mockStore(initialState)
    const wrapper = shallow(
        <App {...props}/>,
        { context: { store: store } },
    );
    expect(wrapper.dive()).toMatchSnapshot();
});