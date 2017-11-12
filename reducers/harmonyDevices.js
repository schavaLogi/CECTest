import { ADD_HARMONY_DEVICE } from '../actions/harmonyDevices';

const initialHarmonyDevices = new Map();

function harmonyDevices(state = initialHarmonyDevices, action) {
    switch (action.type) {
    case ADD_HARMONY_DEVICE: {
        const _newState = new Map(state);
        _newState.set(action.device.id, action.device);
        return _newState;
    }
    default:
        return state;
    }
}

export default harmonyDevices;
