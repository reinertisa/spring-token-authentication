package com.reinertisa.sta.utils;

public class EmailUtils {

    public static String getEmailMessage(String name, String host, String token) {
        return "Hello " + name + "\n\nYour new account has been created. Please click on the link below to " +
                "verify your account.\n\n" + getVerificationUrl(host, token) + "\n\nThe Support team";
    }

    public static String getResetPasswordMessage(String name, String host, String token) {
        return "Hello " + name + "\n\nYour new account has been created. Please click on the link below to " +
                "verify your account.\n\n" + getResetPasswordUrl(host, token) + "\n\nThe Support team";
    }

    private static String getResetPasswordUrl(String host, String token) {
        return host + "/verify/password?token=" + token;
    }

    private static String getVerificationUrl(String host, String token) {
        return host + "/verify/account?token=" + token;
    }
}
