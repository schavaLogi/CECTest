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


import java.util.Map;

/**
 * Interface, Which is used to log Analytics events. Clients  must implement to log events
 * @see LipConfiguration
 *
 */
public interface IAnalytics {
    void logEvent(String eventId, Map<String, String> eventParams);
    void logError(String eventId, Throwable error);
    void logError(String eventId, String errorMsg);

}
