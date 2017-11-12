const cecCommandMap = new Map();

cecCommandMap.set('Select', '00');
cecCommandMap.set('Up', '01');
cecCommandMap.set('Down', '02');
cecCommandMap.set('Left', '03');
cecCommandMap.set('Right', '04');
cecCommandMap.set('Right-Up', '05');
cecCommandMap.set('Right-Down', '06');
cecCommandMap.set('Left-Up', '07');
cecCommandMap.set('Left-Down', '08');
cecCommandMap.set('Root Menu', '09');
cecCommandMap.set('Setup Menu', '0A');
cecCommandMap.set('Contents Menu', '0B');
cecCommandMap.set('Favorite Menu', '0C');
cecCommandMap.set('Exit', '0D');

// 0x0E -0x1F : Reserved

cecCommandMap.set('0', '20');
cecCommandMap.set('1', '21');
cecCommandMap.set('2', '22');
cecCommandMap.set('3', '23');
cecCommandMap.set('4', '24');
cecCommandMap.set('5', '25');
cecCommandMap.set('6', '26');
cecCommandMap.set('7', '27');
cecCommandMap.set('8', '28');
cecCommandMap.set('9', '29');
cecCommandMap.set('Dot', '2A');
cecCommandMap.set('Enter', '2B');
cecCommandMap.set('Clear', '2C');

// 0x2D - 0x2E : Reserved

cecCommandMap.set('Next Favorite', '2F');
cecCommandMap.set('Channel Up', '30');
cecCommandMap.set('Channel Down', '31');
cecCommandMap.set('Previous Channel', '32');
cecCommandMap.set('Sound Select', '33');
cecCommandMap.set('Input Select', '34');
cecCommandMap.set('Display Information', '35');
cecCommandMap.set('Help', '36');
cecCommandMap.set('Page Up', '37');
cecCommandMap.set('Page Down', '38');

// 0x39 -0x3F : Reserved

cecCommandMap.set('Power', '40');
cecCommandMap.set('Volume Up', '41');
cecCommandMap.set('Volume Down', '42');
cecCommandMap.set('Mute', '43');
cecCommandMap.set('Play', '44');
cecCommandMap.set('Stop', '45');
cecCommandMap.set('Pause', '46');
cecCommandMap.set('Record', '47');
cecCommandMap.set('Rewind', '48');
cecCommandMap.set('Fast forward', '49');
cecCommandMap.set('Eject', '4A');
cecCommandMap.set('Forward', '4B');
cecCommandMap.set('Backward', '4C');
cecCommandMap.set('Stop-Record', '4D');
cecCommandMap.set('Pause-Record', '4E');

// 0x4F : Reserved

cecCommandMap.set('Angle', '50');
cecCommandMap.set('Sub picture', '51');
cecCommandMap.set('Video on Demand', '52');
cecCommandMap.set('Electronic Program Guide', '53');
cecCommandMap.set('Timer Programming', '54');
cecCommandMap.set('Initial Configuration', '55');

// 0x56 -0x5F : Reserved

cecCommandMap.set('Play Function', '60');
cecCommandMap.set('Pause-Play Function', '61');
cecCommandMap.set('Record Function', '62');
cecCommandMap.set('Pause-Record Function', '63');
cecCommandMap.set('Stop Function', '64');
cecCommandMap.set('Mute Function', '65');
cecCommandMap.set('Restore Volume Function', '66');
cecCommandMap.set('Tune Function', '67');
cecCommandMap.set('Select Media Function', '68');
cecCommandMap.set('Select A/V Input Function', '69');
cecCommandMap.set('Select Audio Input Function', '6A');
cecCommandMap.set('PowerToggle', '6B');
// cecCommandMap.set("PowerOff", "6C");
// cecCommandMap.set("PowerOn", "6D");
cecCommandMap.set('PowerOff', '36');
cecCommandMap.set('PowerOn', '04');
// 0x6E –0x70 : Reserved

cecCommandMap.set('F1 (Blue)', '71');
cecCommandMap.set('F2 (Red)', '72');
cecCommandMap.set('F3 (Green)', '73');
cecCommandMap.set('F4 (Yellow)', '74');
cecCommandMap.set('F5', '75');
cecCommandMap.set('Data', '76');

// 0x77 –0xFF : Reserved


const tvCommands = [
    {command: 'PowerOn', id: '0'},
    {command: 'PowerOff', id: '1'},
    {command: 'PowerToggle', id: '2'},
    {command: 'Left-Up', id: '3'},
    {command: 'Up', id: '4'},
    {command: 'Right-Up', id: '5'},
    {command: 'Left', id: '6'},
    {command: 'Select', id: '7'},
    {command: 'Right', id: '8'},
    {command: 'Left-Down', id: '9'},

    {command: 'Down', id: '10'},
    {command: 'Right-Down', id: '11'},
    {command: 'Root Menu', id: '12'},
    {command: 'Setup Menu', id: '13'},
    {command: 'Contents Menu', id: '14'},
    {command: 'Favorite Menu', id: '15'},
    {command: 'Exit', id: '16'},
    {command: 'Clear', id: '17'},
    {command: '1', id: '18'},
    {command: '2', id: '19'},
    {command: '3', id: '20'},
    {command: '4', id: '21'},
    {command: '5', id: '22'},
    {command: '6', id: '23'},
    {command: '7', id: '24'},
    {command: '8', id: '25'},
    {command: '9', id: '26'},
    {command: 'Dot', id: '27'},
    {command: '0', id: '28'},
    {command: 'Enter', id: '29'},
    {command: 'Volume Up', id: '30'},
    {command: 'Input Select', id: '31'},
    {command: 'Channel Up', id: '32'},
    {command: 'Volume Down', id: '33'},
    {command: 'Select A/V Input Function', id: '34'},
    {command: 'Channel Down', id: '35'},
    {command: 'Next Favorite', id: '36'},
    {command: 'Previous Channel', id: '37'},
    {command: 'Sound Select', id: '38'},
    {command: 'Select Audio Input Function', id: '39'},
    {command: 'Display Information', id: '40'},
    {command: 'Help', id: '41'},
    {command: 'Page Up', id: '42'},
    {command: 'Page Down', id: '43'},


    {command: 'Power', id: '44'},
    {command: 'Mute', id: '45'},
    {command: 'Play', id: '46'},
    {command: 'Stop', id: '47'},
    {command: 'Pause', id: '48'},

    {command: 'Record', id: '49'},
    {command: 'Rewind', id: '50'},
    {command: 'Fast forward', id: '51'},
    {command: 'Eject', id: '52'},
    {command: 'Forward', id: '53'},


    {command: 'Backward', id: '54'},
    {command: 'Stop-Record', id: '55'},
    {command: 'Pause-Record', id: '56'},
    {command: 'Angle', id: '57'},
    {command: 'Sub picture', id: '58'},

    {command: 'Video on Demand', id: '59'},
    {command: 'Electronic Program Guide', id: '60'},
    {command: 'Timer Programming', id: '61'},
    {command: 'Initial Configuration', id: '62'},
    {command: 'Play Function', id: '63'},


    {command: 'Pause-Play Function', id: '64'},
    {command: 'Record Function', id: '65'},
    {command: 'Pause-Record Function', id: '66'},
    {command: 'Stop Function', id: '67'},
    {command: 'Mute Function', id: '68'},


    {command: 'Restore Volume Function', id: '69'},
    {command: 'Tune Function', id: '70'},
    {command: 'Select Media Function', id: '71'},
    {command: 'F1 (Blue)', id: '72'},
    {command: 'F2 (Red)', id: '73'},
    {command: 'F3 (Green)', id: '74'},
    {command: 'F4 (Yellow)', id: '75'},
    {command: 'F5', id: '76'},
    {command: 'Data', id: '77'},
];

const playBackCommands = [
    {command: 'PowerOn', id: '0'},
    {command: 'PowerOff', id: '1'},
    {command: 'PowerToggle', id: '2'},
    {command: 'Left-Up', id: '3'},
    {command: 'Up', id: '4'},
    {command: 'Right-Up', id: '5'},
    {command: 'Left', id: '6'},
    {command: 'Select', id: '7'},
    {command: 'Right', id: '8'},
    {command: 'Left-Down', id: '9'},
    {command: 'Down', id: '10'},
    {command: 'Right-Down', id: '11'},
    {command: 'Root Menu', id: '12'},
    {command: 'Setup Menu', id: '13'},
    {command: 'Contents Menu', id: '14'},
    {command: 'Favorite Menu', id: '15'},
    {command: 'Exit', id: '16'},
    {command: 'Power', id: '17'},
    {command: 'Play', id: '18'},
    {command: 'Stop', id: '19'},
    {command: 'Pause', id: '20'},
    {command: 'Record', id: '21'},
    {command: 'Rewind', id: '22'},
    {command: 'Fast forward', id: '23'},
    {command: 'Eject', id: '24'},
    {command: 'Forward', id: '25'},
    {command: 'Backward', id: '26'},
    {command: 'Stop-Record', id: '27'},
    {command: 'Pause-Record', id: '28'},
    {command: 'Play Function', id: '29'},
    {command: 'Pause-Play Function', id: '30'},
    {command: 'Record Function', id: '31'},
    {command: 'Pause-Record Function', id: '32'},
    {command: 'Stop Function', id: '33'},
    {command: 'Mute Function', id: '34'},
    {command: 'Restore Volume Function', id: '35'},
];

const avAmplifierCommands = [
    {command: 'PowerOn', id: '0'},
    {command: 'PowerOff', id: '1'},
    {command: 'PowerToggle', id: '2'},
    {command: 'Left-Up', id: '3'},
    {command: 'Up', id: '4'},
    {command: 'Right-Up', id: '5'},
    {command: 'Left', id: '6'},
    {command: 'Select', id: '7'},
    {command: 'Right', id: '8'},
    {command: 'Left-Down', id: '9'},
    {command: 'Down', id: '10'},
    {command: 'Right-Down', id: '11'},
    {command: 'Volume Up', id: '12'},
    {command: 'Mute Function', id: '13'},
    {command: 'Restore Volume Function', id: '14'},
    {command: 'Volume Down', id: '15'},
    {command: 'Mute', id: '16'},
    {command: 'Power', id: '17'},
    {command: 'Input Select', id: '18'},
    {command: 'Select A/V Input Function', id: '19'},
    {command: 'Select Audio Input Function', id: '20'},
    {command: 'Play', id: '21'},
    {command: 'Stop', id: '22'},
    {command: 'Pause', id: '23'},
    {command: 'Record', id: '24'},
    {command: 'Rewind', id: '25'},
    {command: 'Fast forward', id: '26'},
    {command: 'Eject', id: '27'},
    {command: 'Forward', id: '28'},
    {command: 'Backward', id: '29'},
    {command: 'Stop-Record', id: '30'},
    {command: 'Pause-Record', id: '31'},
    {command: 'Play Function', id: '32'},
    {command: 'Pause-Play Function', id: '33'},
    {command: 'Record Function', id: '34'},
    {command: 'Pause-Record Function', id: '35'},
    {command: 'Stop Function', id: '36'},
];


const Commands = {
    cecCommands: cecCommandMap,
    tvCommands,
    playBackCommands,
    avAmplifierCommands,
};

export default Commands;
