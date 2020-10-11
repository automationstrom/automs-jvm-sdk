package app.automs.sdk.helper;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;


/***
 * Helper used for disabling SSL verification for testing reasons
 *
 * Below a sample how to use it, with a GCS emulator running locally
 *         val builder = StorageOptions.newBuilder();
 *         val fakeGcs = "https://fake-gcs-server:4443";
 *
 *         disableSslVerification();
 *             Storage storage;
 *             storage = builder
 *                     .setHost(fakeGcs)
 *                     .setProjectId("test")
 *                     .build()
 *                     .getService();
 */

@SuppressWarnings("unused")
public class SSLTestHelper {
    public static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
