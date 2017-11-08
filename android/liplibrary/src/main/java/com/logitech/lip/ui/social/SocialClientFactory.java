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

package com.logitech.lip.ui.social;

import com.logitech.lip.ui.SocialClient;

public final class SocialClientFactory {

    private static SocialClientFactory INSTANCE;

    public static SocialClientFactory getInstance() {
        if( INSTANCE == null ) {
            synchronized (SocialClientFactory.class) {
                if( INSTANCE == null ) {
                    INSTANCE = new SocialClientFactory();
                }
            }
        }
        return INSTANCE;
    }

    public SocialClient getClient(String provider) {
        switch (provider) {
            case SocialClient.Provider.PROVIDER_GOOGLE:
                return new GoogleClient();
            case SocialClient.Provider.PROVIDER_FACEBOOK:
                return new FacebookClient();
            default:
                return new NullClient();
        }
    }
}
