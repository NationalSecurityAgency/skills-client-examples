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
package skills.examples.data.serviceRequestModel;

public class AdminGroupRequest {
    private String adminGroupId;
    private String name;
    private Boolean enableProtectedUserCommunity = false;

    public AdminGroupRequest(String groupId, String name) {
        this.name = name;
        this.adminGroupId = groupId;
    }

    public String getAdminGroupId() {
        return adminGroupId;
    }

    public void setAdminGroupId(String adminGroupId) {
        this.adminGroupId = adminGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnableProtectedUserCommunity() {
        return enableProtectedUserCommunity;
    }

    public void setEnableProtectedUserCommunity(Boolean enableProtectedUserCommunity) {
        this.enableProtectedUserCommunity = enableProtectedUserCommunity;
    }
}
