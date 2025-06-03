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
package skills.examples.data.model;

public class Skill {
    private String id;
    private String name;
    private String description;
    // optional - valid values are "Approval" or "HonorSystem"
    private String selfReportingType;

    public String getSelfReportingType() {
        return selfReportingType;
    }

    public void setSelfReportingType(String selfReportingType) {
        this.selfReportingType = selfReportingType;
    }

    public Boolean isSelfReporting() {
        return selfReportingType != null && (selfReportingType.equals("Approval") || selfReportingType.equals("HonorSystem"));
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getHelpUrl() {
        return helpUrl;
    }

    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
    }

    private String helpUrl;
}
