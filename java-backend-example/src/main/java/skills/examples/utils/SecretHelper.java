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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SecretHelper {

    @Autowired
    SkillsConfig skillsConfig;

    @Autowired
    RestTemplateFactory restTemplateFactory;

    public String getSecret(String projectId) {
        String secretUrl = skillsConfig.getServiceUrl() + "/admin/projects/" + projectId + "/clientSecret";
        RestTemplate restTemplate = restTemplateFactory.getTemplateWithAuth();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(secretUrl, String.class);
        return responseEntity.getBody();
    }

}
