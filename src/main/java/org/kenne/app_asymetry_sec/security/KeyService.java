package org.kenne.app_asymetry_sec.security;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

//@Profile("prod")
@Component
@Getter
@Slf4j
public class KeyService {

    @Value("${app.security.keys.private-key-path}")
    private String privateKeyPath;

    @Value("${app.security.keys.public-key-path}")
    private String publicKeyPath;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            this.privateKey = loadPrivateKey(privateKeyPath);
            this.publicKey = loadPublicKey(publicKeyPath);
        } catch (Exception e) {
            log.error("Erreur chargement des clés", e);
            throw new IllegalStateException(e);
        }
    }

    private @NonNull PrivateKey loadPrivateKey(@NonNull String pemPath) throws Exception {
        try (PEMParser pemParser = new PEMParser(
                new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(pemPath))))) {
            Object object = pemParser.readObject();
            PrivateKeyInfo keyInfo = (PrivateKeyInfo) object;

            return new JcaPEMKeyConverter().getPrivateKey(keyInfo);
        }
    }

    private @NonNull PublicKey loadPublicKey(@NonNull String pemPath) throws Exception {
        try (PEMParser pemParser = new PEMParser(
                new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(pemPath))))) {
            Object object = pemParser.readObject();
            SubjectPublicKeyInfo keyInfo = (SubjectPublicKeyInfo) object;

            return new JcaPEMKeyConverter().getPublicKey(keyInfo);
        }
    }
}
