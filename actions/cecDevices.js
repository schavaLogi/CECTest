export const ADD_CEC_DEVICES = 'ADD_CEC_DEVICES';
export const UPDATE_CEC_DEVICE = 'UPDATE_CEC_DEVICE';

export function addCECDevice(devices) {
    return {
        type: ADD_CEC_DEVICES,
        devices,
    };
}

export function updateCECDevice(id, device) {
    return {
        type: UPDATE_CEC_DEVICE,
        id,
        device,
    };
}
