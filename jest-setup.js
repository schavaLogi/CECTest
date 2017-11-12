// @flow

import { NativeModules } from 'react-native';

// monkey patching the locale to avoid the error:
//  Something went wrong initializing the native ReactLocalization module
NativeModules.ReactLocalization = {
    language: 'en_US',
};


jest.mock('react-native-code-push', () => {
    const cp = () => (app) => app;
    Object.assign(cp, {
        InstallMode: {},
        CheckFrequency: {},
        SyncStatus: {},
        UpdateState: {},
        DeploymentStatus: {},
        DEFAULT_UPDATE_DIALOG: {},

        checkForUpdate: jest.fn(),
        codePushify: jest.fn(),
        getConfiguration: jest.fn(),
        getCurrentPackage: jest.fn(),
        getUpdateMetadata: jest.fn(),
        log: jest.fn(),
        notifyAppReady: jest.fn(),
        notifyApplicationReady: jest.fn(),
        sync: jest.fn(),
    });
    return cp;
});