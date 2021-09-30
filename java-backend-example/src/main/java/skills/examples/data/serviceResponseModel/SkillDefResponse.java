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
package skills.examples.data.serviceResponseModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillDefResponse {
    private String skillId;
    private String projectId;
    private String selfReportingType;
    private Integer numPerformToCompletion;
    private Integer pointIncrement;
    private Integer pointIncrementInterval;
    private Integer numMaxOccurrencesIncrementInterval;
    private Integer totalPoints;

    public String getSkillId() {
        return skillId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getSelfReportingType() {
        return selfReportingType;
    }

    public Integer getNumPerformToCompletion() {
        return numPerformToCompletion;
    }

    public Integer getPointIncrement() {
        return pointIncrement;
    }

    public Integer getPointIncrementInterval() {
        return pointIncrementInterval;
    }

    public Integer getNumMaxOccurrencesIncrementInterval() {
        return numMaxOccurrencesIncrementInterval;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }
}
