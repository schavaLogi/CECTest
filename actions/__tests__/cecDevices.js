import 'react-native';

import configureStore from 'redux-mock-store';

import {addCECDevice, updateCECDevice} from '../../actions/cecDevices';

import {CEC_DEVICES} from "../../cec/model/mockCecDevices";

const store = configureStore()();

console.log("mockStore =" + store);

for (let prop in store) {
    console.log(prop);
}



it('should handle addCECDevice action', async () => {
    await store.dispatch(addCECDevice(CEC_DEVICES));
    expect(store.getActions()).toMatchSnapshot();
});

it('should handle updateCECDevice action', async () => {
    await store.dispatch(updateCECDevice(101, CEC_DEVICES[0]));
    expect(store.getActions()).toMatchSnapshot();
});