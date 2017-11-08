'use strict';

import {Device, DETECTION_TYPE} from './Device';

export class HDevice extends Device {
    constructor(make, model, type, id) {
        super(make, model, type, id);
        this.detection = DETECTION_TYPE.HARMONY;
    }
}
