/**
 * Copyright 2026 SkillTree
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
    private final List<String> cookies = new ArrayList<>();
    private String xsrfToken;

    public boolean hasXsrfToken() {
        return xsrfToken != null && !xsrfToken.isEmpty();
    }

    public void clearXsrfToken() {
        xsrfToken = null;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // add cookies and XSRF token headers if present
        HttpHeaders requestHeaders = request.getHeaders();
        if (!cookies.isEmpty()) {
            requestHeaders.set(HttpHeaders.COOKIE, String.join("; ", cookies));
        }
        if (hasXsrfToken()) {
            requestHeaders.set("X-XSRF-TOKEN", xsrfToken);
        }
        log.debug("REQUEST: [{}], headers [{}]", request.getURI(), request.getHeaders());
        ClientHttpResponse response = execution.execute(request, body);

        // update cookies and XSRF token for future requests
        HttpHeaders headers = response.getHeaders();
        List<String> returnedCookies = headers.getOrEmpty(HttpHeaders.SET_COOKIE);
        if (!returnedCookies.isEmpty()) {
            for (String cookie : returnedCookies) {
                String cookiePair = cookie.split(";", 2)[0];
                String cookieName = getCookieName(cookiePair);
                cookies.removeIf(str -> getCookieName(str).equals(cookieName));
                cookies.add(cookiePair);
            }
            log.debug("Updated session cookies from response to [{}]", request.getURI());

            returnedCookies.stream()
                    .filter(cookie -> getCookieName(cookie).equals("XSRF-TOKEN"))
                    .findFirst()
                    .ifPresent(cookie -> {
                        String cookieValue = cookie.split(";", 2)[0];
                        xsrfToken = cookieValue.substring(cookieValue.indexOf('=') + 1);
                        log.debug("Response: [{}], received XSRF-TOKEN cookie", request.getURI());
                    });
        }
        return response;
    }

    public static String getCookieName(String str) {
        int equalsIndex = str.indexOf('=');
        if (equalsIndex != -1) {
            return str.substring(0, equalsIndex);
        } else {
            return str;
        }
    }
}