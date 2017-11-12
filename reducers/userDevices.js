import { ADD_USER_DEVICE } from '../actions/userDevices';

const initialUserDevices = [];

function userDevices(state = initialUserDevices, action) {
    switch (action.type) {
    case ADD_USER_DEVICE: {
        const _newState = state;
        _newState.push(action.device);
        return _newState;
    }
    default:
        return state;
    }
}

export default userDevices;
