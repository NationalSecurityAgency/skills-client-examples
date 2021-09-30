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

import java.util.Date;

public class BadgeRequest {
    private String name;
    private String description;
    private String iconClass;
    private Boolean enabled;  // applies only to global badges

    // applies only to "gem" badges
    Date startDate;
    Date endDate;

    public BadgeRequest() {
    }

    public BadgeRequest(String name, String description, String iconClass) {
        this.name = name;
        this.description = description;
        this.iconClass = iconClass;
    }

    public BadgeRequest(String name, String description, String iconClass, Boolean enabled) {
        this.name = name;
        this.description = description;
        this.iconClass = iconClass;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Date getStartDate() { return startDate; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }

    public void setEndDate(Date endDate) { this.endDate = endDate; }
}
