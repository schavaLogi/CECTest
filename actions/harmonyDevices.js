export const ADD_HARMONY_DEVICE = 'ADD_HARMONY_DEVICE';

export function addHarmonyDevice(deviceInfo) {
    return {
        type: ADD_HARMONY_DEVICE,
        device: deviceInfo
    }
}