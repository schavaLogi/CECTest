import 'react-native';

import cecDevices ,{ initialCECDevices } from '../cecDevices';


it('returns the same state on an unhandled action', () => {
    expect(cecDevices(initialCECDevices, {})).toMatchSnapshot();
});
