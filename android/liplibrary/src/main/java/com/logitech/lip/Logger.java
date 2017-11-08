package com.logitech.lip;

import android.util.Log;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * This class is used for printing the logs
 *
 * Created by nkumar3 on 3/28/2016.
 */
public class Logger {

    /**
     * If APP is production do not print logs to console
     */
    private static boolean isPrintLog = false;

    public static void info(String className, String methodName, String msg) {
        if (isPrintLog) Log.i(className + ":" + methodName, "" + msg);

        LIPSdk.getConfiguration().getLoggerCallback().Info(className,methodName,msg);
    }

    public static void debug(String className, String methodName, String msg) {
        if (isPrintLog) Log.d(className + ":" + methodName, "" + msg);

        LIPSdk.getConfiguration().getLoggerCallback().debug(className, methodName, msg);
    }

    public static void error(String errorCode, String className, String methodName, String msg,  Exception exception) {
        if (isPrintLog) Log.e("errorCode=" + errorCode + className + ":" + methodName, "" + msg, exception);

        LIPSdk.getConfiguration().getLoggerCallback().error(errorCode, className,methodName,msg, exception);
    }

    public static ILogger getDefaultLogger() {
        return new Logger.NullLogger();
    }

    public static IAnalytics getDefaultAnalytics() {
        return new Logger.NullAnalytics();
    }


    private static class NullLogger implements ILogger {
        @Override
        public void Info(String className, String methodName, String msg) {}

        @Override
        public void debug(String className, String methodName, String msg) {}

        @Override
        public void error(String errorCode, String className, String methodName, String msg,
                          Exception exception) {

        }

    }

    private static class NullAnalytics implements IAnalytics {

        @Override
        public void logEvent(String eventId, Map<String, String> eventParams) {}

        @Override
        public void logError(String eventId, Throwable error) {}

        @Override
        public void logError(String eventId, String errorMsg) {}
    }
}
