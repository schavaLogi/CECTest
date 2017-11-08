/*
 * Copyright (c) 2015 Logitech, Inc. All Rights Reserved
 *
 * This program is a trade secret of LOGITECH, and it is not to be reproduced,
 * published, disclosed to others, copied, adapted, distributed or displayed
 * without the prior written authorization of LOGITECH.
 *
 * Licensee agrees to attach or embed this notice on all copies of the program
 * including partial copies or modified versions thereof.
 */
package com.logitech.lip;

/**
 * AppConfiguration enables the client apps to set the  static configuration of LIP SDK
 * such as font that is known initially
 */
public final class AppConfiguration {

    private String appName;
    private boolean keepScreenOn;

    private String[] fontSet;

    /**
     * Returns the font of the LIP UI
     *
     * @return FontSet List of fonts available in LIP
     */
    public String[] getCustomFont() {
        return fontSet;
    }

    public String getAppName() {
        return appName;
    }

    public boolean isKeepScreenOn() {
        return keepScreenOn;
    }
}

