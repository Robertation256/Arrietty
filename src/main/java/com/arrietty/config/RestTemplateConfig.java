package com.arrietty.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Duration;


@Configuration
public class RestTemplateConfig {
    @Value("${server.ssl.key-store}")
    String clientPath;
    @Value("${server.ssl.key-store-password}")
    String clientPass;
    @Value("${server.ssl.key-store-type}")
    String clientKeyType;
    @Value("${server.ssl.trust-store}")
    String trustPath;
    @Value("${server.ssl.trust-store-password}")
    String trustPass;
    @Value("${server.ssl.trust-store-type}")
    String trustKeyType;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = null;
        try {
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            // 客户端证书类型
            KeyStore clientStore = KeyStore.getInstance(clientKeyType);
            // 加载客户端证书，即自己的私钥
            clientStore.load(new FileInputStream(ResourceUtils.getFile(clientPath)), clientPass.toCharArray());
            // 创建密钥管理工厂实例
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            // 初始化客户端密钥库
            keyManagerFactory.init(clientStore, clientPass.toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            // 创建信任库管理工厂实例
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore trustStore = KeyStore.getInstance(trustKeyType);
            trustStore.load(new FileInputStream(ResourceUtils.getFile(trustPath)), trustPass.toCharArray());

            // 初始化信任库
            trustManagerFactory.init(trustStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            // 建立TLS连接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 初始化SSLContext
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
            // INSTANCE 忽略域名检查
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            CloseableHttpClient httpclient = HttpClients
                    .custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .build();
            requestFactory.setHttpClient(httpclient);
            requestFactory.setConnectTimeout((int) Duration.ofSeconds(15).toMillis());
            restTemplate = new RestTemplate(requestFactory);
        } catch (KeyManagementException | FileNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException e) {
            e.printStackTrace();
        }
        return restTemplate;
    }
}
