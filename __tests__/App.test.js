import 'react-native';
import React from 'react';
// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';

import Adapter from 'enzyme-adapter-react-16';
import { shallow, configure } from 'enzyme';

import App from '../App';

configure({ adapter: new Adapter() });


it('renders App correctly', () => {
    const tree = renderer.create(<App />).toJSON();
    expect(tree).toMatchSnapshot();
});

it('renders App as expected', () => {
    const wrapper = shallow(<App />);
    expect(wrapper.dive()).toMatchSnapshot();
});
