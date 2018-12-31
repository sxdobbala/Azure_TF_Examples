package com.optum.acc.fileupload;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class SSLSetup {

    private SSLSetup() {
    }

    public static void setup() throws GeneralSecurityException, IOException {
        final char[] keyStorePassword = "securepassword".toCharArray();

        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(SSLSetup.class.getResourceAsStream("/optumkeystore.jks"), keyStorePassword);

        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(keyStore, null);

        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(keyStore);

        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustFactory.getTrustManagers(), new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}
