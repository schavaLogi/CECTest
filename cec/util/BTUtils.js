'use strict';

import React from 'react';
import {NativeModules, PermissionsAndroid, Platform} from 'react-native';

import BleManager from 'react-native-ble-manager';

const BleManagerModule = NativeModules.BleManager;

async function requestBTPermission() {
    try {
        const hasAccess = await PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION);
        if (hasAccess) {
            console.log("Permission is OK");
            return true;
        } else {
            const granted = await PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION);

            if (granted === PermissionsAndroid.RESULTS.GRANTED) {
                console.log("You can use the BT now");
                return true;
            } else {
                console.log("BT permission denied")
            }
        }
    } catch (err) {
        console.warn(err)
    }
    return false;
}

async function enableBT() {
    try {
        let status = await BleManager.enableBluetooth();
        return true;
    } catch (err) {
        console.warn('There is error in bluetooth err=' + err);
    }
    return false;
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