package com.arrietty.config;


import com.arrietty.service.AuthServiceImpl;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;


@Configuration
public class RestTemplateConfig {
    @Value("${server.ssl.sso-cert-path}")
    private String CERTIFICATION_PATH;

    @Value("${server.ssl.sso-cert-pwd}")
    private String CERTIFICATION_PWD;


    @Bean(name = "httpsRestTemplate")
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpsFactory = new HttpComponentsClientHttpRequestFactory();
        httpsFactory.setConnectionRequestTimeout(3000);
        httpsFactory.setConnectTimeout(3000);
        httpsFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(httpsFactory);
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) {
                try{
                    return !clientHttpResponse.getStatusCode().equals(HttpStatus.OK);
                }
                catch (IOException e){
                    return true;
                }
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) {
                //handle non 200 status
                try{
                    AuthServiceImpl.logger.error(String.format("[request to keycloak failed] response status code: %d", clientHttpResponse.getRawStatusCode()));
                }
                catch (IOException e){
                    AuthServiceImpl.logger.error("[request to keycloak failed]", e);
                }
            }
        });
        return restTemplate;
    }

    @Bean(name = "cerHttpsFactory")
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() throws Exception {
        CloseableHttpClient httpClient = createCloseableHttpClient();
        HttpComponentsClientHttpRequestFactory httpsFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        httpsFactory.setReadTimeout(2000);
        httpsFactory.setConnectTimeout(2000);
        return httpsFactory;
    }


    private CloseableHttpClient createCloseableHttpClient() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(resourceLoader(CERTIFICATION_PATH), CERTIFICATION_PWD.toCharArray());
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy()).build();

        SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.setSSLSocketFactory(sslFactory).build();
        return closeableHttpClient;
    }


    private InputStream resourceLoader(String fileFullPath) throws IOException {
        ResourceLoader resourceLoader = new FileSystemResourceLoader();
        return resourceLoader.getResource(fileFullPath).getURI().toURL().openStream();
    }

}
