package com.bdas;

import org.apache.commons.codec.binary.Base64;

public  class Utils{
    public static String encodeCredentials(String username, String password) {
        byte[] credentials = (username + ':' + password).getBytes();
        return new String(Base64.encodeBase64(credentials));
    }
}
