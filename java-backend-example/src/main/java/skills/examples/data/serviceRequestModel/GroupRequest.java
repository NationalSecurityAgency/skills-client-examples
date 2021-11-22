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

public class GroupRequest {
    private String name;
    private String description;
    private String skillId;
    private String subjectId;
    private String type = "SkillsGroup";
    private String enabled = "false";
    private Integer numSkillsRequired = -1;

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

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public Integer getNumSkillsRequired() {
        return numSkillsRequired;
    }

    public void setNumSkillsRequired(Integer numSkillsRequired) {
        this.numSkillsRequired = numSkillsRequired;
    }

    @Override
    public String toString() {
        return "GroupRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", skillId='" + skillId + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", type='" + type + '\'' +
                ", numSkillsRequired='" + numSkillsRequired + '\'' +
                '}';
    }
}


