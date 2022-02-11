package com.arrietty.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author
 * @data 2019/4/14 0014 -下午 2:54
 */
public class SSLSocketFactory extends org.apache.http.conn.ssl.SSLSocketFactory {

    SSLContext sslContext = SSLContext.getInstance("TLS");

    public SSLSocketFactory(KeyStore truststore)
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sslContext.init(null, new TrustManager[] { tm }, null);
    }

    /* (non-Javadoc)
     * <p>Title: createSocket</p>
     * <p>Description: </p>
     * @param socket
     * @param host
     * @param port
     * @param autoClose
     * @return
     * @throws IOException
     * @throws UnknownHostException
     * @see org.apache.http.conn.ssl.SSLSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
     */

    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port,
                autoClose);
    }

    /* (non-Javadoc)
     * <p>Title: createSocket</p>
     * <p>Description: </p>
     * @return
     * @throws IOException
     * @see org.apache.http.conn.ssl.SSLSocketFactory#createSocket()
     */

    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

}
