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

public class SkillRequest {
    private String name;
    private Integer pointIncrement = 20;
    private Integer numPerformToCompletion = 3;
    private Integer pointIncrementInterval = 12;
    private Integer numMaxOccurrencesIncrementInterval = 1;
    private String description;
    private String helpUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPointIncrement() {
        return pointIncrement;
    }

    public void setPointIncrement(Integer pointIncrement) {
        this.pointIncrement = pointIncrement;
    }

    public Integer getNumPerformToCompletion() {
        return numPerformToCompletion;
    }

    public void setNumPerformToCompletion(Integer numPerformToCompletion) {
        this.numPerformToCompletion = numPerformToCompletion;
    }

    public Integer getPointIncrementInterval() {
        return pointIncrementInterval;
    }

    public void setPointIncrementInterval(Integer pointIncrementInterval) {
        this.pointIncrementInterval = pointIncrementInterval;
    }

    public Integer getNumMaxOccurrencesIncrementInterval() {
        return numMaxOccurrencesIncrementInterval;
    }

    public void setNumMaxOccurrencesIncrementInterval(Integer numMaxOccurrencesIncrementInterval) {
        this.numMaxOccurrencesIncrementInterval = numMaxOccurrencesIncrementInterval;
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

    @Override
    public String toString() {
        return "SkillRequest{" +
                "name='" + name + '\'' +
                ", pointIncrement=" + pointIncrement +
                ", numPerformToCompletion=" + numPerformToCompletion +
                ", pointIncrementInterval=" + pointIncrementInterval +
                ", numMaxOccurrencesIncrementInterval=" + numMaxOccurrencesIncrementInterval +
                ", description='" + description + '\'' +
                ", helpUrl='" + helpUrl + '\'' +
                '}';
    }
}


