package org.kenne.app_asymetry_sec.security;

import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

    private KeyUtils() {

    }

    public static @NonNull PrivateKey loadPrivateKey(final @NonNull String pemPath) throws Exception {
        final String key = readKeyFromResource(pemPath)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        final byte[] keyBytes = Base64.getDecoder().decode(key);
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public static @NonNull PublicKey loadPublicKey(final @NonNull String pemPath) throws Exception {
        final String key = readKeyFromResource(pemPath)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        final byte[] keyBytes = Base64.getDecoder().decode(key);
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);

        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private static @NonNull String readKeyFromResource(@NonNull String pemPath) throws Exception {
        try (final InputStream is = KeyUtils.class.getClassLoader().getResourceAsStream(pemPath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + pemPath);
            }

            return new String(is.readAllBytes());
        }
    }
}
