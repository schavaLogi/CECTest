import 'react-native';


import cecDevices from '../cecDevices';

import { initialCECDevices } from '../cecDevices';
import {CEC_DEVICES} from "../../cec/model/mockCecDevices";

it('returns the same state on an unhandled action', () => {
    expect(cecDevices(initialCECDevices, CEC_DEVICES)).toMatchSnapshot();
});
