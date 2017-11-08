import LocalizedStrings from 'react-native-localization';


let locStrings = new LocalizedStrings({
    en: {
        welcome_desc: "We will help you to setup your CEC test device and run test." +
        "\n\n\n" +
        "To start, connect this device to WIFI using setting app.",
        next: "Next",
        welcome: 'Welcome',
        your_name: 'Your Name',
        me_desc: 'Please enter your name below.',
        first_name: "First name",
        last_name: "Last name",
        name_placeHolder: 'Minimum 4 characters',
        harmony_remote: "Harmony Remote",
        harmony_remote_desc: 'Do you have Harmony remote and Harmony account?',
        harmony_account_yes: 'Yes, Have Harmony',
        harmony_account_no: 'No, Don\'t have Harmony',
        hdmi_inputs: 'HDMI Inputs',
        hdmi_inputs_desc: 'Do you have a free HDMI input on the back of your TV or AVR receiver?',
        hdmi: 'HDMI',
        free_input_yes: 'Yes, Have Free input',
        free_input_no: 'No Free input',
        device_desc: 'Please provide details of each HDMI connected device.',
        make: 'Make',
        model: 'Model',
        no_more_devices: 'No More Devices',
        pair_test_device: 'Pair Cec Test Device',
        pair_test_desc: 'Press the pair button on your CEC test device so it goes in to pairing mode. The light on the front should blink blue',
        settings: 'Settings',
        raspberry_pi: 'Raspberry Pi',
        dongle: 'Dongle',
        view_cec_devices: 'View CEC Devices',
        devices : 'Devices',
        device : 'Device',
        no_of_devices: 'Number of Devices',
        devices_desc: 'Excluding the CEC device, How many devices do you have connected to HDMI inputs?',

        choose_device_type: 'Choose Device Type',
        tv : 'TV',
        avr : 'AVR',
        playback : 'Playback'
    },
    it: {
        welcome_desc: "We will help you to setup your CEC test device and run test." +
        "\n\n\n" +
        "To start, connect this device to WIFI using setting app.",
        next: "next"
    }
});

export default locStrings;