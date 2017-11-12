import { NavigationActions } from 'react-navigation';

import Root, { defaultGetStateForAction } from '../Root';

function nav(state, action) {
    let nextState;
    switch (action.type) {
    case 'Login':
        nextState = Root.router.getStateForAction(
            NavigationActions.back(),
            state,
        );
        break;
    case 'Logout':
        nextState = Root.router.getStateForAction(
            NavigationActions.navigate({ routeName: 'Login' }),
            state,
        );
        break;
    case 'Reset':
        /* const resetNavigator = NavigationActions.reset({
                index: 1,
                actions: [
                    NavigationActions.navigate({ routeName: 'Main'}),
                    NavigationActions.navigate({ routeName: 'Login'})
                ]
            }); */
        const resetNavigator = NavigationActions.reset({
            index: 0,
            actions: [
                NavigationActions.navigate({ routeName: 'Welcome' }),
            ],
        });
        nextState = Root.router.getStateForAction(resetNavigator);
        break;
    case 'subRoot':
        nextState = Root.router.getStateForAction(
            NavigationActions.navigate({ routeName: 'subRoot' }),
            state,
        );
        break;

    case 'Navigate':
        nextState = Root.router.getStateForAction(
            NavigationActions.navigate({ routeName: action.routeName }),
            state,
        );
        break;

    case 'ReplaceScreen':
        /* this.props.navigation.dispatch({
                key: 'MeProfile',
                type: 'ReplaceScreen',
                routeName: 'MeProfile'
            }); */
        if (state && action.type === 'ReplaceScreen') {
            const routes = state.routes.slice(0, state.routes.length - 1);
            routes.push(action);
            return {
                ...state,
                routes,
                index: routes.length - 1,
            };
        }
        nextState = defaultGetStateForAction(action, state);
        break;

    default:
        nextState = Root.router.getStateForAction(action, state);
        break;
    }
    // nextState = Root.router.getStateForAction(action, state);
    // Simply return the original `state` if `nextState` is null or undefined.
    const currentRoute = nextState.routes[nextState.index];
    console.log(`currentRoute =${currentRoute.routeName}key =${currentRoute.key}`);
    return nextState || state;
}


export default nav;
