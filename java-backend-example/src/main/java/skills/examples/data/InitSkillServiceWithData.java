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
package skills.examples.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import skills.examples.data.model.Badge;
import skills.examples.data.model.Project;
import skills.examples.data.model.Skill;
import skills.examples.data.model.Subject;
import skills.examples.data.serviceRequestModel.*;
import skills.examples.data.serviceResponseModel.PendingApprovalsResponse;
import skills.examples.data.serviceResponseModel.SkillDefResponse;
import skills.examples.utils.RestTemplateFactory;
import skills.examples.utils.SkillsConfig;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InitSkillServiceWithData {

    @Autowired
    private SkillsConfig skillsConfig;

    @Autowired
    private RestTemplateFactory restTemplateFactory;

    @Autowired
    private SampleDatasetLoader sampleDatasetLoader;

    private ObjectMapper jsonMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(InitSkillServiceWithData.class);

    @PostConstruct
    void load() throws Exception {
        log.info("SkillsConfig ["+skillsConfig+"]");
        if (skillsConfig.getCreateRootAccount()) {
            createRootAccount();
        } else if (!skillsConfig.isPkiMode()) {
            createUser(skillsConfig.getServiceUrl() + "/createAccount");
        }

        String serviceUrl = skillsConfig.getServiceUrl();
        RestTemplate rest = restTemplateFactory.getTemplateWithAuth();

        List<Project> projects = sampleDatasetLoader.getProjects();
        projectLoop: for (Project project : projects) {
            if (rest.getForEntity(serviceUrl + "/app/projects", String.class).getBody().contains(project.getName())) {
                log.info("Project [" + project.getName() + "] already exists!");
                break projectLoop;
            }
            String projectId = project.getId();
            post(rest, serviceUrl + "/app/projects/" + projectId, new ProjRequest(project.getName()));

            log.info("\nStarting to create schema for project [" + projectId + "]\n" +
                    "  [" + project.getSubjects().size() + "] subjects\n" +
                    "  [" + project.getBadges().size() + "] badges");
            String projectUrl = serviceUrl + "/admin/projects/" + projectId;
            addSubjects(project, rest, projectUrl);
            addBadges(project, rest, projectUrl);

            // pin the project on the root user's admin view and enable production mode
            if (skillsConfig.getCreateRootAccount()) {
                post(rest, serviceUrl + "/root/pin/" + projectId);
            }
            post(rest, serviceUrl + "/admin/projects/"+projectId+"/settings/production.mode.enabled", new SettingRequest(projectId, "production.mode.enabled", "true"));
            achieveBadges(project, rest, project.getBadges().stream().filter(badge -> badge.isShouldAdminAchieve()).collect(Collectors.toList()));
            reportSkills(rest, project);
            approveAndRejectSomePendingApprovals(project, rest);

            log.info("Project [" + projectId + "] was created!");
        }

        assignCrossProjectDependency(rest, "shows", "MarvelsAgentsofSHIELD", "movies", "TheAvengers");
        assignSeriesDependencies(rest, "movies", new ArrayList<>(Arrays.asList(
                "HarryPotterandthePhilosophersStone",
                "HarryPotterandtheChamberofSecrets",
                "HarryPotterandthePrisonerofAzkaban",
                "HarryPotterandtheGobletofFire",
                "HarryPotterandtheOrderofthePhoenix",
                "HarryPotterandtheHalfBloodPrince",
                "HarryPotterandtheDeathlyHallowsPart1",
                "HarryPotterandtheDeathlyHallowsPart2"))
        );
        if (skillsConfig.getCreateRootAccount()) {
            addGlobalBadge(rest, serviceUrl, 2);
        }
    }


    private void approveAndRejectSomePendingApprovals(Project project, RestTemplate rest) {
        PendingApprovalsResponse pendingApprovalsResponse = getPendingApprovals(project, rest);
        // approve/reject 1/2 of the pending self report approval request
        Integer numPendingApprovals = pendingApprovalsResponse.getCount();
        List<Integer> approveSkillIds = new ArrayList<>();
        List<Integer> rejectSkillIds = new ArrayList<>();
        for (int i = 0; i < numPendingApprovals/2; i++) {
            if (i % 2 == 0) {
                approveSkillIds.add(pendingApprovalsResponse.getData().get(i).getId());
            } else {
                rejectSkillIds.add(pendingApprovalsResponse.getData().get(i).getId());
            }
        }
        SkillApprovalRequest skillApprovalRequest = new SkillApprovalRequest();
        skillApprovalRequest.setSkillApprovalIds(approveSkillIds);
        String approveUrl = skillsConfig.getServiceUrl() + "/admin/projects/" + project.getId() +"/approvals/approve";
        post(rest, approveUrl, skillApprovalRequest);

        SkillRejectionRequest skillRejectionRequest = new SkillRejectionRequest();
        skillRejectionRequest.setSkillApprovalIds(rejectSkillIds);
        skillRejectionRequest.setRejectionMessage("Sorry, please try again.");
        String rejectUrl = skillsConfig.getServiceUrl() + "/admin/projects/" + project.getId() +"/approvals/reject";
        post(rest, rejectUrl, skillRejectionRequest);
    }

    private PendingApprovalsResponse getPendingApprovals(Project project, RestTemplate rest) {
        String pendingApprovalsUrl = skillsConfig.getServiceUrl() + "/admin/projects/{projectId}/approvals?limit={limit}&page={page}&orderBy={orderBy}&ascending={ascending}";
        return get(rest, pendingApprovalsUrl, PendingApprovalsResponse.class, project.getId(), 50, 1, "requestedOn", false);
    }
    private boolean doesUserExist(String username) {
        try {
            RestTemplate rest = restTemplateFactory.getTemplateWithAuth();
            ResponseEntity<Boolean> res = rest.getForEntity(skillsConfig.getServiceUrl() + "/userExists/" + username, Boolean.class);
            return res.getBody();
        } catch (HttpClientErrorException.Unauthorized unauthorizedE) {
            // swallow
        }

        return false;
    }

    private void addSubjects(Project project, RestTemplate rest, String projectUrl) {
        for (Subject subject : project.getSubjects()) {
            log.info("\nCreating [" + subject.getName() + "] subject with [" + subject.getSkills().size() + "] skills");
            String subjectUrl = projectUrl + "/subjects/" + subject.getId();
            post(rest, subjectUrl, new SubjRequest(subject.getName(), "", subject.getIconClass()));

            List<String> groupNames = Arrays.asList("Harry Potter", "Cars", "Guardians of the Galaxy", "Thor", "The Hangover", "Iron Man", "Terminator", "The Hunger Games", "X-Men");
            Map<String, GroupRequest> groupRequestMap = new HashMap<>();
            List<Skill> skills = subject.getSkills();
            GroupRequest groupRequest = null;
            for (int i = 0; i < skills.size(); i++) {
                Skill skill = skills.get(i);

                String skillName = skill.getName();
                String skillUrl = subjectUrl + "/skills/" + skill.getId();

                List<String> foundGroupNames = groupNames.stream().filter(gName -> skillName.startsWith(gName)).collect(Collectors.toList());
                String groupName = foundGroupNames != null && foundGroupNames.size() > 0 ? foundGroupNames.get(0) : null;
                if (groupName != null) {
                    groupRequest = new GroupRequest();
                    groupRequest.setSkillId(groupName.replaceAll(" ", "").replaceAll("-", "") + "GroupId");
                    groupRequest.setName(groupName);
                    groupRequest.setSubjectId(subject.getId());
                    groupRequest.setDescription(groupName + "Movies");
                    String groupUrl = subjectUrl + "/skills/" + groupRequest.getSkillId();

                    if (!groupRequestMap.containsKey(groupName)) {
                        post(rest, groupUrl, groupRequest);
                        groupRequestMap.put(groupName, groupRequest);
                    }
                    skillUrl = subjectUrl + "/groups/" + groupRequest.getSkillId() + "/skills/" + skill.getId();
                }

                SkillRequest skillRequest = new SkillRequest();
                skillRequest.setName(skill.getName());
                skillRequest.setDescription(skill.getDescription());
                skillRequest.setHelpUrl(skill.getHelpUrl());
                if (skill.isSelfReporting()) {
                    skillRequest.setSelfReportingType(skill.getSelfReportingType());
                }
                if (groupName != null || i % 3 == 0) {
                    skillRequest.setNumPerformToCompletion(3);
                } else if (i % 2 == 0) {
                    skillRequest.setNumPerformToCompletion(2);
                }
                post(rest, skillUrl, skillRequest);
            }

            for (GroupRequest gR : groupRequestMap.values()) {
                gR.setEnabled("true");
                if (gR.getName().startsWith("Harry Potter")) {
                    gR.setNumSkillsRequired(4);
                }
                String groupUrl = subjectUrl + "/skills/" + gR.getSkillId();
                post(rest, groupUrl, gR);
            }

            log.info("\nCompleted [" + subject.getName() + "] subject");
        }
    }

    private void addGlobalBadge(RestTemplate rest, String serviceUrl, Integer level) {
        String badgeId = "MoviesandShowsExpertBadge";
        String name = "Movie and Show Expert";
        String description = "The \"Movies and Shows Expert\" must achieve at least Level "+level+" in both the Movies and Shows projects.";
        String iconClass = "mi mi-live-tv";
        String badgeUrl = serviceUrl + "/supervisor/badges/" + badgeId;
        post(rest, badgeUrl, new BadgeRequest(name, description, iconClass, true));

        post(rest, serviceUrl + "/supervisor/badges/" + badgeId + "/projects/movies/level/"+level);
        post(rest, serviceUrl + "/supervisor/badges/" + badgeId + "/projects/shows/level/"+level);
        log.info("\nCreating Global Badge [" + name + "] that requires users to achieve at least level [" + level + "] for both projects");
    }

    private void addBadges(Project project, RestTemplate rest, String projectUrl) {
        for (Badge badge : project.getBadges()) {
            log.info("\nCreating [" + badge.getName() + "] badge with [" + badge.getSkillIds().size() + "] skills");
            String badgeUrl = projectUrl + "/badges/" + badge.getId();
            BadgeRequest badgeRequest = new BadgeRequest(badge.getName(), badge.getDescription(), badge.getIconClass());
            if (badge.isGem()) {
                badgeRequest.setStartDate(new Date(getTimestamp(30 * 24 * 60)));  // 30 days ago
                badgeRequest.setEndDate(new Date());
            }
            post(rest, badgeUrl, badgeRequest);

            for (String skillId : badge.getSkillIds()) {
                String assignUrl = projectUrl + "/badge/" + badge.getId() + "/skills/" + skillId;
                post(rest, assignUrl, null);
            }
        }
    }

    private void achieveBadges(Project project, RestTemplate rest, List<Badge> badges) {
        for (Badge badge : badges) {
            for (String skillId : badge.getSkillIds()) {
                String subjectId = findSubjectForSkillId(project, skillId).getId();
                String skillDefUrl = skillsConfig.getServiceUrl() + "/admin/projects/" + project.getId() + "/subjects/" + subjectId + "/skills/" + skillId;
                SkillDefResponse skillDefResponse = get(rest, skillDefUrl, SkillDefResponse.class);
                for (int i = 0; i < skillDefResponse.getNumPerformToCompletion(); i++) {
                    String reportUrl = skillsConfig.getServiceUrl() + "/api/projects/" + project.getId() + "/skills/" + skillId;
                    post(rest, reportUrl, new ReportSkillRequest(null, getTimestamp(i * (skillDefResponse.getPointIncrementInterval()+1))));
                }
            }
        }
    }

    private Subject findSubjectForSkillId(Project project, String skillId) {
        assert project != null && skillId != null;
        Subject foundSubject = null;
        for (int i = 0; i < project.getSubjects().size(); i++) {
            Subject subject = project.getSubjects().get(i);
            Optional<Skill> foundSkill = subject.getSkills().stream().filter(skill -> skill.getId().equals(skillId)).findFirst();
            if (foundSkill.isPresent()) {
                foundSubject = subject;
                break;
            }
        }
        if (foundSubject == null) {
            throw new RuntimeException("Unable to find skillId ["+skillId+"] in project ["+project.getId()+"]");
        }
        return foundSubject;
    }

    private void assignSeriesDependencies(RestTemplate rest, String projectId, List<String> sortedSkillIds) {
        String toSkillId = sortedSkillIds.remove(0);
        for (String fromSkillId : sortedSkillIds) {
            assignDependency(rest, projectId, fromSkillId, toSkillId);
            toSkillId = fromSkillId;
        }
    }

    private void assignDependency(RestTemplate rest, String projectId, String fromSkillId, String toSkillId) {
        String serviceUrl = skillsConfig.getServiceUrl();
        post(rest, serviceUrl + "/admin/projects/"+projectId+"/skills/"+fromSkillId+"/dependency/"+toSkillId);
        log.info("Assigned project ("+projectId+") dependency: "+fromSkillId+" -> "+toSkillId);
    }

    private void assignCrossProjectDependency(RestTemplate rest, String fromProjId, String fromSkillId, String toProjId, String toSkillId) {
        String serviceUrl = skillsConfig.getServiceUrl();
        // share skill with other project and then assign cross project dependency
        post(rest, serviceUrl + "/admin/projects/"+fromProjId+"/skills/"+fromSkillId+"/shared/projects/"+toProjId);
        post(rest, serviceUrl + "/admin/projects/"+toProjId+"/skills/"+toSkillId+"/dependency/projects/"+fromProjId+"/skills/"+fromSkillId);
        log.info("Assigned cross-project dependency: "+fromProjId+":"+fromSkillId+" -> "+toProjId+":"+toSkillId);
    }

    private void reportSkills(RestTemplate rest, Project project) {
        int numEvents = skillsConfig.getNumEvents();
        int numUsers = skillsConfig.getNumUsers();
        int numDays = skillsConfig.getNumDays();
        Random random = new Random();
        List<String> skillIds = new ArrayList<>();
        for (Subject subject : project.getSubjects()) {
            for (Skill skill : subject.getSkills()) {
                skillIds.add(skill.getId());
            }
        }
        List<String> userIds = new ArrayList<>();
        for (int i = 0; i < numUsers; i++) {
            userIds.add("User" + i);
        }
        userIds.add(getCurrentUserId());
        userIds.addAll(skillsConfig.getAdditionalRootUsers());
        List<String> highOutliers = Arrays.asList(userIds.get(random.nextInt(3)), userIds.get(random.nextInt(3)), userIds.get(random.nextInt(3)));
        List<String> lowOutliers = Arrays.asList(userIds.remove(random.nextInt(2)), userIds.remove(random.nextInt(2)));
        log.info("\nReporting skills for [" + numUsers + "] users\n" +
                "   number of events to send = [" + numEvents + "]\n" +
                "   May take a minute or so.... Please hold!");
        for (int i = 0; i < numEvents; i++) {
            String userId = userIds.get(random.nextInt(userIds.size()));
            if (i % 200 == 0) {
                userId = lowOutliers.get(random.nextInt(lowOutliers.size()));
            } else if (i % 5 == 0) {
                userId = highOutliers.get(random.nextInt(highOutliers.size()));
            }
            String skillId = skillIds.get(random.nextInt(skillIds.size()));
            String reportUrl = skillsConfig.getServiceUrl() + "/api/projects/" + project.getId() + "/skills/" + skillId;
            post(rest, reportUrl, new ReportSkillRequest(userId, getRandomTimestamp(numDays, random)));
        }

        for (String userId : highOutliers) {
            // attempt to report a few invalid skillId's
            String reportUrl = skillsConfig.getServiceUrl() + "/api/projects/" + project.getId() + "/skills/";
            try {
                post(rest, reportUrl+"invalidSkillId", new ReportSkillRequest(userId, getRandomTimestamp(numDays, random)));
            } catch (Exception e) {
                // ignore
            }

            // overachieve on a few skills
            for (int i = 0; i < 3; i++) {
                String skillId = skillIds.get(i);
                for (int j = 0; j < 10; j++) {
                    post(rest, reportUrl+skillId, new ReportSkillRequest(userId, getRandomTimestamp(numDays, random)));
                }
            }
        }
    }

    private Long getRandomTimestamp(Integer numDays, Random random) {
        int daysAgo = random.nextInt(numDays);
        long days = (long) daysAgo * 1000l * 60l * 60l * 24l;
        return System.currentTimeMillis() - days;
    }
    private Long getTimestamp(Integer numMinutesAgo) {
        long minutes = (long) numMinutesAgo * 1000l * 60l;
        return System.currentTimeMillis() - minutes;
    }

    private void post(RestTemplate restTemplate, String url) {
        post(restTemplate, url, null);
    }

    private String post(RestTemplate restTemplate, String url, Object data) {
        if (log.isDebugEnabled()) {
            log.debug("POST: " + url + " with [" + data + "]");
        }
        String retValue = null;
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, data, String.class);
        if (responseEntity != null) {
            retValue = responseEntity.getBody();
        }
        return retValue;
    }

    public <T> T get(RestTemplate restTemplate, String url, Class<T> valueType, Object... uriVariables) {
        try {
            String resultJson = get(restTemplate, url, uriVariables);
            return jsonMapper.readValue(resultJson, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String get(RestTemplate restTemplate, String url, Object... uriVariables) {
        if (log.isDebugEnabled()) {
            log.debug("GET: " + url);
        }
        String retValue = null;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, uriVariables);
        if (responseEntity != null) {
            retValue = responseEntity.getBody();
        }
        return retValue;
    }

    private void createUser(String url) {
        createUser(url, skillsConfig.getUsername());
    }

    private void createUser(String url, String username) {
        if (!doesUserExist(username)) {
            RestTemplate restTemplate = new RestTemplate();
            UserInfoRequest userInfoRequest = new UserInfoRequest("Bill", "Gosling", username, skillsConfig.getPassword());
            HttpEntity request = new HttpEntity<>(userInfoRequest, new HttpHeaders());
            restTemplate.put(url, request);
            log.info("\n-----------------\nCreated User:\n  email=[" + username + "]\n  password=[" + userInfoRequest.getPassword() + "]\n----------------");
        } else {
            log.info("User [" + username + "] already exists");
        }
    }

    private void createRootAccount() {
        String url = skillsConfig.getServiceUrl();
        RestTemplate restTemplate = new RestTemplate();
        if (skillsConfig.isPkiMode()) {
            restTemplate.put(url + "/grantFirstRoot", null);
            log.info("\n-----------------\nCreated Root User:\n  DN=[" + getDn() + "]\n----------------");
        } else {

            // create (optional) additional root users
            for (String additionalUser : skillsConfig.getAdditionalRootUsers()) {
                createUser(skillsConfig.getServiceUrl() + "/createAccount", additionalUser);
            }

            restTemplate.getForObject(url + "/logout", String.class);
            UserInfoRequest userInfoRequest = new UserInfoRequest("Bill", "Gosling", skillsConfig.getUsername(), skillsConfig.getPassword());
            HttpEntity request = new HttpEntity<>(userInfoRequest, new HttpHeaders());
            restTemplate.put(url + "/createRootAccount", request);
            log.info("\n-----------------\nCreated Root User:\n  email=[" + userInfoRequest.getEmail() + "]\n  password=[" + userInfoRequest.getPassword() + "]\n----------------");

            // grand root to additional root users
            restTemplate = restTemplateFactory.getTemplateWithAuth();
            for (String additionalUser : skillsConfig.getAdditionalRootUsers()) {
                restTemplate.put(url + "/root/addRoot/"+additionalUser, null);
                log.info("\n-----------------\nCreated Additional Root User:\n  email=[" + additionalUser + "]\n  password=[" + userInfoRequest.getPassword() + "]\n----------------");
            }
        }
    }

    private String getCurrentUserId() {
        if (skillsConfig.isPkiMode()) {
            return getDn();
        } else {
            return skillsConfig.getUsername();
        }
    }

    private String getDn() {
        String dn = null;
        try {
            String keystore = System.getProperty("javax.net.ssl.keyStore");
            String keystoreType = System.getProperty("javax.net.ssl.keyStoreType");
            char[] pwdArray = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
            KeyStore ks = KeyStore.getInstance(keystoreType);
            ks.load(new FileInputStream(keystore), pwdArray);
            dn = ((X509Certificate) ks.getCertificate(ks.aliases().nextElement())).getSubjectX500Principal().getName();
        } catch (Exception e) {
            log.error("Unable to extract DN from certificate", e);
        }

        return dn;
    }
}
