import { UPDATE_USER } from '../actions/user';

const initialUserInfoState = { firstName: '', lastName: '' };

function userInfo(state = initialUserInfoState, action) {
    switch (action.type) {
    case UPDATE_USER:
        return { ...state, ...action.userInfo };
    default:
        return state;
    }
}

export default userInfo;
