import {UPDATE_SETTINGS} from '../actions/settings';

const initialSettingsState = {selected: 'Raspberry-Pi'};

function settings(state = initialSettingsState, action) {
    switch (action.type) {
        case UPDATE_SETTINGS:
            return {...state, selected: action.selected};
        default:
            return state;
    }
}

export default settings;