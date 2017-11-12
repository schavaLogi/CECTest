export const UPDATE_SETTINGS = 'UPDATE_SETTINGS';

export const SETTINGS_RASPBERRY_PI = 'Raspberry-Pi';
export const SETTINGS_DONGLE = 'Dongle';

export function updateSettings(selected) {
    return {
        type: UPDATE_SETTINGS,
        selected,
    };
}
