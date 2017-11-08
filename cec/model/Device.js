'use strict';


export const DETECTION_TYPE = {
    CEC: 'CEC',
    HARMONY: 'Harmony',
    MANUAL: 'Manual',
    NONE: 'None'
};

export class Device {
    constructor(make, model, type, id) {
        this.make = make;
        this.model = model;
        this.type = type;
        this.id = id;
        this.statesDetected = [];
        this.powerCommand = [];
        this.changeInputCommand = [];

        this.detection = DETECTION_TYPE.NONE;
    }

    addDetectedState(command) {
        this.statesDetected.push(command);
    }

    addPowerCommand(command) {
        this.powerCommand.push(command);
    }

    addChangeInputCommand(command) {
        this.changeInputCommand.push(command);
    }

    getDeviceInfo() {
        return "Make  :  " + this.make + '\n\n' + "Model  :  " + this.model + '\n\n' + 'Device Type  :  ' +
            this.type + '\n\n' + "Device Id  :  " + this.id + '\n\n' + "Detected  :  " + this.detection + '\n\n';
    }

    getCompleteReport() {
        return this.getDeviceInfo() + "statesDetected  :  " + this.statesDetected +
            "powerCommand  :  " + this.powerCommand + "changeInputCommand   :  " + this.changeInputCommand;
    }
}
