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
 * Interface, Which is used to log different level of message. Clients  must implement to log
 * @see LipConfiguration
 *
 */
public interface ILogger {

    String LIP_E001 ="LIP_E001";  /*Data Parse Error*/
    String LIP_E002 ="LIP_E002";  /*Social Account related errors*/

    String LIP_E003 ="LIP_E003";  /*Encryption/Decryption errors*/
    String LIP_UNDEFINED ="LIP_UNDEFINED";

    void Info(String className, String methodName, String msg);
    void debug(String className, String methodName, String msg);
    void error(String errorCode, String className, String methodName, String msg, Exception exception);
}
