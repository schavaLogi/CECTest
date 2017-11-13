
jest.mock('react-native-ble-manager',() => ({
    bleMockOptions: {enabled : true},

    enableBluetooth: function () {
        if(this.bleMockOptions.enabled === true) {
            return Promise.resolve();
        } else {
            return Promise.reject('Mock options not enabled');
        }
    },

    __setMockOptions: function (bleMockOptions) {
        this.bleMockOptions = bleMockOptions;
    },
    isPeripheralConnected: jest.genMockFn().mockReturnValue(false),
}));
