import { PermissionsAndroid, Platform } from 'react-native';

import BleManager from 'react-native-ble-manager';


async function requestBTPermission() {
    try {
        const hasAccess = await PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION);
        if (hasAccess) {
            return true;
        }
        const granted = await PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION);

        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
            return true;
        }
    } catch (err) {
        throw err;
    }
    return false;
}

async function enableBT() {
    try {
        await BleManager.enableBluetooth();
        return true;
    } catch (err) {
        console.warn(`There is error in bluetooth err=${err}`);
        throw err;
    }
}

export async function enableBTWithPermission() {
    let btEnabled = true;
    if (Platform.OS === 'android') {
        btEnabled = false;
        btEnabled = await enableBT();
        if (btEnabled && Platform.Version >= 23) {
            btEnabled = await requestBTPermission();
        }
    }
    return btEnabled;
}