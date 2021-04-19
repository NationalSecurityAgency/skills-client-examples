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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("skills.service")
public class SkillsConfig {
    String serviceUrl;
    String authenticator;
    @JsonIgnore
    String username;
    @JsonIgnore
    String password;
    @JsonIgnore
    String authMode = "token";
    Boolean createRootAccount = false;

    public String getServiceUrl() { return serviceUrl; }
    public String getAuthenticator() { return authenticator; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getAuthMode() { return authMode; }
    public Boolean getCreateRootAccount() { return createRootAccount; }
    public Boolean isPkiMode() {
        return authMode.equalsIgnoreCase("pki");
    }

    public void setServiceUrl(String serviceUrl) { this.serviceUrl = serviceUrl; }
    public void setAuthenticator(String authenticator) { this.authenticator = authenticator; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setAuthMode(String authMode) { this.authMode = authMode; }
    public void setCreateRootAccount(Boolean createRootAccount) { this.createRootAccount = createRootAccount; }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("serviceUrl", serviceUrl)
                .append("authenticator", authenticator)
                .append("username", username)
                .append("password", password)
                .append("authMode", authMode)
                .append("createRootAccount", createRootAccount)
                .toString();
    }
}
