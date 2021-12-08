package com.gitee.drinkjava2.frog.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @Description: 简版控制台日志打印
 * @Date: 2021/12/07
 */
public class Logger {
    private static final String LEV_EL = "debug";
    private static final int LEVEL_INT;
    private static final BlockingQueue<String> LOG_LIST = new ArrayBlockingQueue<>(256);
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    private static final OutputStream OUTPUT_STREAM = System.out;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DELIM_STR = "{}";
    private static final String TAB = "\tat";
    private static final Map<String, Integer> LEVEL_MAP = new HashMap<>();

    static {
        LEVEL_MAP.put("DEBUG", 1);
        LEVEL_MAP.put("INFO", 2);
        LEVEL_MAP.put("WARN", 2);
        LEVEL_MAP.put("ERROR", 2);
        LEVEL_INT = LEVEL_MAP.get(LEV_EL.toUpperCase());
        new Thread(() -> {
            while (true) {
                try {
                    outPutConsole(LOG_LIST.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public static void debug(String msg) {
        printLog(LEVEL_MAP.get("DEBUG"), msg);
    }

    public static void debug(String msg, Object params) {
        printLog(LEVEL_MAP.get("DEBUG"), msg, params);
    }

    public static void debug(String msg, Object... params) {
        printLog(LEVEL_MAP.get("DEBUG"), msg, params);
    }

    public static void info(String msg) {
        printLog(LEVEL_MAP.get("INFO"), msg);
    }

    public static void info(String msg, Object params) {
        printLog(LEVEL_MAP.get("INFO"), msg, params);
    }

    public static void info(String msg, Object... params) {
        printLog(LEVEL_MAP.get("INFO"), msg, params);
    }

    public static void warn(String msg) {
        printLog(LEVEL_MAP.get("WARN"), msg);
    }

    public static void warn(String msg, Object params) {
        printLog(LEVEL_MAP.get("WARN"), msg, params);
    }

    public static void warn(String msg, Object... params) {
        printLog(LEVEL_MAP.get("WARN"), msg, params);
    }

    public static void error(String msg) {
        printLog(LEVEL_MAP.get("ERROR"), msg);
    }

    public static void error(String msg, Object params) {
        printLog(LEVEL_MAP.get("ERROR"), msg, params);
    }

    public static void error(String msg, Object... params) {
        printLog(LEVEL_MAP.get("ERROR"), msg, params);
    }

    public static void error(Object param) {
        printLog(LEVEL_MAP.get("ERROR"), "", param);
    }

    private static void printLog(int levelInt, String msg, Object... params) {
        try {
            if (levelInt >= LEVEL_INT) {
                LOG_LIST.put(generateMsg(getLevelStr(levelInt), msg, params));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String generateMsg(String levelStr, String msg, Object... params) {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
        String s = "{} [{}][{}#{} {}] - " + msg + LINE_SEPARATOR;
        Object[] args = new Object[5 + params.length];
        args[0] = FORMAT.format(System.currentTimeMillis());
        args[1] = levelStr;
        args[2] = stack.getClassName();
        args[3] = stack.getMethodName();
        args[4] = stack.getLineNumber();

        Throwable throwable = null;
        if (params.length > 0) {
            final Object lastEntry = params[params.length - 1];
            if (lastEntry instanceof Throwable) {
                throwable = (Throwable) lastEntry;
                System.arraycopy(params, 0, args, 5, params.length - 1);
            } else {
                System.arraycopy(params, 0, args, 5, params.length);
            }
        }
        return formatMsg(s, throwable, args);
    }

    private static String formatMsg(String msg, Throwable throwable, Object... params) {
        StringBuilder sb = new StringBuilder();
        int s;
        int i = 0;
        for (Object o : params) {
            s = msg.indexOf(DELIM_STR, i);
            if (s > -1) {
                sb.append(msg, i, s).append(o);
                i = s + 2;
            }
        }
        sb.append(msg, i, msg.length());
        if (null != throwable) {
            sb.append(throwable).append(LINE_SEPARATOR);
            StackTraceElement[] stack = throwable.getStackTrace();
            for (StackTraceElement element : stack) {
                sb.append(TAB).append(element).append(LINE_SEPARATOR);
            }
        }
        return sb.toString();
    }

    private static void outPutConsole(String msg) {
        try {
            OUTPUT_STREAM.write(msg.getBytes(StandardCharsets.UTF_8));
            OUTPUT_STREAM.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getLevelStr(int levelInt) {
        switch (levelInt) {
            case 1:
                return "DEBUG";
            case 2:
                return "INFO";
            case 3:
                return "WARN";
            case 4:
                return "ERROR";
            default:
                throw new IllegalStateException("Level " + levelInt + " is unknown.");
        }
    }
}
