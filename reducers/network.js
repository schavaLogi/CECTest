import { UPDATE_NET } from '../actions/network';

const initialConnectionState = { isConnected: false };

function net(state = initialConnectionState, action) {
    switch (action.type) {
    case UPDATE_NET:
        return { ...state, isConnected: action.isConnected };
    default:
        return state;
    }
}

export default net;
