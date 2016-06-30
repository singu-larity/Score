package com.example.john.score;

import android.webkit.JavascriptInterface;

public class RSA {
    volatile public static String crypt = "";
    RSA() {}

    @JavascriptInterface
    public void showToast(String temp) {
        System.out.println("showToast");
        crypt = temp;
    }
}
