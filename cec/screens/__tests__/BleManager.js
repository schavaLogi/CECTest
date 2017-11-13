'use strict';

import BleManager from 'react-native-ble-manager';

jest.mock('react-native-ble-manager');

describe('BleManager Mock Module Testing', () =>{

    const bleMockOptions = { enabled : true };
    beforeEach(() => {
        BleManager.__setMockOptions(bleMockOptions);
    });

    it('Test Bluetooth Enabled Must success', () => {
        return BleManager.enableBluetooth()
            .then(() => {expect.assertions(0);});
    });

    it('Test Bluetooth Enabled Must Reject', () => {
        BleManager.__setMockOptions({...bleMockOptions, enabled : false});

        return BleManager.enableBluetooth()
            .then(expect.assertions(1))
            .catch(value => {expect(value).toBe('Mock options not enabled');});
    });
});