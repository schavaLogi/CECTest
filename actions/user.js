export const UPDATE_USER = 'UPDATE_USER';

export function updateUserInfoStatus(userInfo) {
    return {
        type: UPDATE_USER,
        userInfo,
    };
}
