
import { Device, DETECTION_TYPE } from './Device';

export default class CecDevice extends Device {
    constructor(make, model, type, id) {
        super(make, model, type, id);
        this.detection = DETECTION_TYPE.CEC;

        this.ipAddress = '';
        this.cecVersion = '';
    }

    updateIpAddress(ipAddress) {
        this.ipAddress = ipAddress;
    }

    updateCecVersion(cecVersion) {
        this.cecVersion = cecVersion;
    }

    getDeviceInfo() {
        return `${super.getDeviceInfo()}IP Address  :  ${this.ipAddress}\n\n` + `CEC Version  :  ${this.cecVersion}\n\n`;
    }
}
