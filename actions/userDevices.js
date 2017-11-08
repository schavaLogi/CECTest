export const ADD_USER_DEVICE = 'ADD_USER_DEVICE';

export function updateUserDevice(deviceInfo) {
    return {
        type: ADD_USER_DEVICE,
        device: deviceInfo
    }
}