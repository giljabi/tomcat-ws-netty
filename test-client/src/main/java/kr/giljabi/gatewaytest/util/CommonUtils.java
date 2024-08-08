package kr.giljabi.gatewaytest.util;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author : eahn.park@gmail.com
 */
public class CommonUtils {
    public static String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static String getCurrentTime(String format) {
        if (format == null || format.isEmpty()) {
            format = DEFAULT_TIMESTAMP_FORMAT;
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    public static String getLocalIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
