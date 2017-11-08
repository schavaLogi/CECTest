import {LOGIN_SUCCESS, LOGOUT} from '../actions/auth';

const initialAuthState = {isLoggedIn: false, accessToken: '', idToken: ''};

function auth(state = initialAuthState, action) {
    switch (action.type) {
        case LOGIN_SUCCESS:
            return {
                ...state, isLoggedIn: true,
                accessToken: action.accountToken.accessToken,
                idToken: action.accountToken.idToken
            };
        case LOGOUT:
            return {...state, isLoggedIn: false, accessToken: '', idToken: ''};
        default:
            return state;
    }
}

export default auth;