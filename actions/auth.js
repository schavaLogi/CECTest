export const LOGIN_SUCCESS = 'LOGIN_SUCCESS';
export const LOGOUT = 'LOGOUT';

export function loginSuccess(accountToken) {
    return {
        type: LOGIN_SUCCESS,
        accountToken,
    };
}

export function logOut() {
    return {
        type: LOGOUT,
    };
}
