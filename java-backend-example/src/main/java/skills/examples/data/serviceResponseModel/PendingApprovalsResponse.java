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

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingApprovalsResponse {
    List<PendingSkillApproval> data;
    Integer count;
    Integer totalCount;

    public List<PendingSkillApproval> getData() {
        return data;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PendingSkillApproval {
        private Integer id;
        private String projectId;
        private String skillId;
        private String skillName;
        private String subjectId;
        private String userId;
        private String userIdForDisplay;
        private String requestMsg;
        private Long requestedOn;

        public Integer getId() {
            return id;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getSkillId() {
            return skillId;
        }

        public String getSkillName() {
            return skillName;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserIdForDisplay() {
            return userIdForDisplay;
        }

        public String getRequestMsg() {
            return requestMsg;
        }

        public Long getRequestedOn() {
            return requestedOn;
        }
    }
}
