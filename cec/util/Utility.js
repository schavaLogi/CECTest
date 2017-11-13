import { Platform } from 'react-native';

const onAndroid = () => {return Platform.OS === 'android';};

export { onAndroid };