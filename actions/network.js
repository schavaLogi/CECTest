export const UPDATE_NET = 'UPDATE_NET';

export function updateNetStatus(isConnected) {
    return {
        type: UPDATE_NET,
        isConnected,
    };
}
