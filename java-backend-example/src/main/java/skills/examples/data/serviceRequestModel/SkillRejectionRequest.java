package skills.examples.data.serviceRequestModel;

import java.util.List;

public class SkillRejectionRequest {
    List<Integer> skillApprovalIds;
    String rejectionMessage;

    public List<Integer> getSkillApprovalIds() {
        return skillApprovalIds;
    }
    public void setSkillApprovalIds(List<Integer> skillApprovalIds) {
        this.skillApprovalIds = skillApprovalIds;
    }

    public String getRejectionMessage() { return rejectionMessage; }
    public void setRejectionMessage(String rejectionMessage) { this.rejectionMessage = rejectionMessage; }
}
