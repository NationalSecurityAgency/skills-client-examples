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
package skills.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import skills.examples.data.SampleDatasetLoader;
import skills.examples.utils.SecretHelper;
import skills.examples.utils.SkillsConfig;
import skills.examples.utils.StatefulRestTemplateInterceptor;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class ControllerExample {

    @Autowired
    SkillsConfig skillsConfig;

    @Autowired
    SecretHelper secretHelper;

    @Autowired
    SampleDatasetLoader sampleDatasetLoader;

    @CrossOrigin()
    @GetMapping("/users/{user}/token")
    public String getUserAuthToken(@PathVariable String user) {
        String clientId = "movies"; // project Id
        String serviceTokenUrl = skillsConfig.getServiceUrl() + "/oauth/token";
        String clientSecret = secretHelper.getSecret(clientId);

        RestTemplate oAuthRestTemplate = new RestTemplate();
        oAuthRestTemplate.setInterceptors(Arrays.asList(new BasicAuthenticationInterceptor(clientId, clientSecret), new StatefulRestTemplateInterceptor()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("proxy_user", user);

        ResponseEntity<String> responseEntity = oAuthRestTemplate.postForEntity(serviceTokenUrl, new HttpEntity<>(body, headers), String.class);

        return responseEntity.getBody();
    }
}
