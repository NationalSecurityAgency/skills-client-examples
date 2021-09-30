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
