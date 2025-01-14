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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StatefulRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(StatefulRestTemplateInterceptor.class);
    private List<String> cookies;
    private String xsrfToken;
    private String xsrfCookie;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders requstHeaders = request.getHeaders();
        if (cookies != null) {
            requstHeaders.addAll(HttpHeaders.COOKIE, cookies);
        }
        if (xsrfToken != null) {
            requstHeaders.add("X-XSRF-TOKEN" , xsrfToken);
        }
        log.debug("REQUEST: [{}], headers [{}]", request.getURI(), request.getHeaders());
        ClientHttpResponse response = execution.execute(request, body);

        HttpHeaders headers = response.getHeaders();

        List<String> returnedCookies = headers.getOrEmpty(HttpHeaders.SET_COOKIE);
        if (!returnedCookies.isEmpty()) {
            if (cookies == null) {
                cookies = new ArrayList<>();
            }
            cookies.addAll(returnedCookies);
            log.info("Setting cookies to {}", returnedCookies);
        }
        if (!returnedCookies.isEmpty() && xsrfToken == null) {
            response.getHeaders().get(HttpHeaders.SET_COOKIE).stream().filter(cookie -> cookie.startsWith("XSRF-TOKEN")).findAny().ifPresent(cookie -> xsrfCookie = cookie);
            if (xsrfCookie != null) {
                xsrfToken = xsrfCookie.substring(xsrfCookie.indexOf('=') + 1, xsrfCookie.indexOf(';'));
                log.debug("Response: [{}], set xsrfToken to [{}]", request.getURI(), xsrfToken);
            }
        }
        return response;
    }
}