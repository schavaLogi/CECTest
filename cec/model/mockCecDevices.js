'use strict';

import {CecDevice} from './CecDevice';

let device0 = new CecDevice('Sony', "LN52", "TV", 100);
device0.updateIpAddress('10.0.0.1');
device0.updateCecVersion('1.5');

let device1 = new CecDevice('LG', "SN123", "TV", 101);
device1.updateIpAddress('10.0.0.2');
device1.updateCecVersion('1.5');

let device2 = new CecDevice('Samsung', "LN52-XDL", "TV", 102);
device2.updateIpAddress('10.0.0.3');
device2.updateCecVersion('1.5');

let device3 = new CecDevice('TataSky', "KDL", "SetupBox", 103);
device3.updateIpAddress('10.0.0.4');
device3.updateCecVersion('1.5');

let device4 = new CecDevice('DishTv', "LN52", "PlayBack", 104);
device4.updateIpAddress('10.0.0.5');
device4.updateCecVersion('1.5');


let device5 = new CecDevice('Samsung', "LN52XD", "TV", 105);
device5.updateIpAddress('10.0.0.6');
device5.updateCecVersion('1.5');

let device6 = new CecDevice('Sony', "LDX", "TV", 107);
device6.updateIpAddress('10.0.0.7');
device6.updateCecVersion('1.5');

let device7 = new CecDevice('Sony', "AMP", "Amplifier", 108);
device7.updateIpAddress('10.0.0.8');
device7.updateCecVersion('1.5');

export const CEC_DEVICES = [device0, device1, device2, device3, device4, device5, device6, device7];
