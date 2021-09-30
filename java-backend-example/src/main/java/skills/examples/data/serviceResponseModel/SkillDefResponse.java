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
