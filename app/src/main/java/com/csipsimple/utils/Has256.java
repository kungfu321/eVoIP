package com.csipsimple.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SignatureException;

/**
 * Created by Kien Shinichi on 9/28/2016.
 */

public class Has256 {
    public static String sha256Digest (String data) throws SignatureException {
        return getDigest("SHA-256", data, true);
    }

    private static String getDigest(String algorithm, String data, boolean toLower)
            throws SignatureException {
        try {
            MessageDigest mac = MessageDigest.getInstance(algorithm);
            mac.update(data.getBytes("UTF-8"));
            return toLower ?
                    new String(toHex(mac.digest())).toLowerCase() : new String(toHex(mac.digest()));
        } catch (Exception e) {
            throw new SignatureException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }
}
