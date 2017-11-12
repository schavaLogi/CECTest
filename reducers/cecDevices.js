import { ADD_CEC_DEVICES, UPDATE_CEC_DEVICE } from '../actions/cecDevices';
import { CEC_DEVICES } from '../cec/model/mockCecDevices';

export const initialCECDevices = CEC_DEVICES;

function cecDevices(state = initialCECDevices, action) {
    switch (action.type) {
    case ADD_CEC_DEVICES: {
        const newState = [];
        newState.push(action.devices);
        return newState;
    }

    case UPDATE_CEC_DEVICE: {
        return state.map(cecDevice =>
            ((cecDevice.id === action.id)
                ? { ...cecDevice, ...action.device }
                : cecDevice));
    }
    default:
        return state;
    }
}

export default cecDevices;
