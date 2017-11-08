import {StackNavigator} from 'react-navigation';

import WelcomeScreen from './cec/screens/Welcome';
import MeProfileScreen from './cec/screens/MeProfile';
import HarmonyRemoteScreen from './cec/screens/HarmonyRemote';
import HDMIInputScreen from './cec/screens/HDMIInputScreen';
import DeviceInfoScreen from "./cec/screens/DeviceInfo";
import CommandsListScreen from './cec/screens/CommandsList';
import ExpandableListScreen from "./cec/screens/ExpandableList";
import PairDevice from "./cec/screens/PairDevice";
import SpinnerScreen from "./cec/screens/SpinnerScren";
import Settings from "./cec/screens/Settings";
import DevicesCountScreen from "./cec/screens/DevicesCount";

const Root = StackNavigator({
        Welcome: {screen: WelcomeScreen},
        MeProfile: {screen: MeProfileScreen},
        HarmonyRemote: {screen: HarmonyRemoteScreen},
        HDMIInputs: {screen: HDMIInputScreen},
        DeviceInfo: {screen: DeviceInfoScreen},
        CommandsList: {screen: CommandsListScreen},
        ExpandableList: {screen: ExpandableListScreen},
        DevicesCount: {screen: DevicesCountScreen},
        PairDevice: {screen: PairDevice},
        Settings: {screen: Settings},
        Spinner: {screen: SpinnerScreen}
    },
    {
        initialRouteName: 'Welcome',
        mode: 'card',
        headerMode: 'none'
    }
);

/*
const defaultGetStateForAction = Root.router.getStateForAction;

Root.router.getStateForAction = (action, state) => {
  if (state && action.type === 'ReplaceScreen') {
    const routes = state.routes.slice(0, state.routes.length - 1);
    routes.push(action);
    return {
        ...state,
        routes,
        index: routes.length - 1,
    };
  }
  return defaultGetStateForAction(action, state);
};
*/

export const defaultGetStateForAction = Root.router.getStateForAction;

export default Root;