/**
 * Copyright 2020 SkillTree
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package skills.examples.utils;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.util.Collections;

@Component
public class RestTemplateFactory {
    private SkillsConfig skillsConfig;

    @Autowired
    public RestTemplateFactory(SkillsConfig skillsConfig) {
        this.skillsConfig = skillsConfig;
    }

    public RestTemplate getTemplateWithAuth() {
        return this.getTemplateWithAuth(skillsConfig.getUsername());
    }
    public RestTemplate getTemplateWithAuth(String username) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new StatefulRestTemplateInterceptor()));
        if (!skillsConfig.getAuthMode().equalsIgnoreCase("pki")) {
            // must configure HttpComponentsClientHttpRequestFactory as SpringTemplate does
            // not by default keeps track of session
            restTemplate.setRequestFactory(getHttpRequestFactory());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("username", username);
            params.add("password", skillsConfig.getPassword());

            restTemplate.setInterceptors(Collections.singletonList(new StatefulRestTemplateInterceptor()));
            restTemplate.getForEntity(skillsConfig.getServiceUrl() + "/", String.class);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(skillsConfig.getServiceUrl() + "/performLogin", request, String.class);
            assert response.getStatusCode() == HttpStatus.OK;
        } else {
            SSLContext sslContext = SSLContexts.createSystemDefault();
            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    new String[]{"TLSv1.2"},
            null,
                    allowAllHosts);


            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
                    new PoolingHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", sslConnectionSocketFactory).build());

            HttpClient httpClient = HttpClients.custom()
//                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .useSystemProperties()
//                    .setSSLContext(sslContext)
                    .setConnectionManager(poolingHttpClientConnectionManager)
                    .build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            restTemplate = new RestTemplate(requestFactory);
            restTemplate.setInterceptors(Collections.singletonList(new StatefulRestTemplateInterceptor()));

        }
        return restTemplate;
    }

    private ClientHttpRequestFactory getHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create().build(); //.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        clientHttpRequestFactory.setHttpClient(httpClient);
        return clientHttpRequestFactory;
    }
}
