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
import skills.examples.utils.RestTemplateFactory;
import skills.examples.utils.SkillsConfig;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class InitSkillServiceWithData {

    @Autowired
    private SkillsConfig skillsConfig;

    @Autowired
    private RestTemplateFactory restTemplateFactory;

    @Autowired
    private SampleDatasetLoader sampleDatasetLoader;

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
                log.info("Project [" + project.getName() + "] already exist!");
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
            post(rest, serviceUrl + "admin/projects/"+projectId+"/settings/production.mode.enabled", new SettingRequest(projectId, "production.mode.enabled", "true"));

            reportSkills(rest, project);

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
    }

    private boolean doesUserExist() {
        try {
            RestTemplate rest = restTemplateFactory.getTemplateWithAuth();
            ResponseEntity<String> res = rest.getForEntity(skillsConfig.getServiceUrl() + "/app/projects", String.class);
            return true;
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

            for (Skill skill : subject.getSkills()) {
                String skillUrl = subjectUrl + "/skills/" + skill.getId();
                SkillRequest skillRequest = new SkillRequest();
                skillRequest.setName(skill.getName());
                skillRequest.setDescription(skill.getDescription());
                skillRequest.setHelpUrl(skill.getHelpUrl());
                post(rest, skillUrl, skillRequest);
            }
            log.info("\nCompleted [" + subject.getName() + "] subject");
        }
    }

    private void addBadges(Project project, RestTemplate rest, String projectUrl) {
        for (Badge badge : project.getBadges()) {
            log.info("\nCreating [" + badge.getName() + "] badge with [" + badge.getSkillIds().size() + "] skills");
            String badgeUrl = projectUrl + "/badges/" + badge.getId();
            post(rest, badgeUrl, new BadgeRequest(badge.getName(), badge.getDescription(), badge.getIconClass()));

            for (String skillId : badge.getSkillIds()) {
                String assignUrl = projectUrl + "/badge/" + badge.getId() + "/skills/" + skillId;
                post(rest, assignUrl, null);
            }
        }
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
        post(rest, serviceUrl + "admin/projects/"+projectId+"/skills/"+fromSkillId+"/dependency/"+toSkillId);
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
        double[] usersAndAchievementPercent = {.1, .24, .5, .7, .8};
        int numUsers = usersAndAchievementPercent.length;
        List<String> skillIds = new ArrayList<>();
        for (Subject subject : project.getSubjects()) {
            for (Skill skill : subject.getSkills()) {
                skillIds.add(skill.getId());
            }
        }
        Random random = new Random();
        for (int i = 0; i < numUsers; i++) {
            String userId = "User" + i;
            double percentToAchieve = usersAndAchievementPercent[i];
            int eventsToSend = (int) (skillIds.size() * percentToAchieve) * 3;
            int numOfDays = percentToAchieve > .5 ? 46 : 12;
            log.info("\nReporting skills for user [" + userId + "]\n" +
                    "   number of events to send = [" + eventsToSend + "]\n" +
                    "   May take a minute.... Please hold!");

            int numPerDay = (int) (eventsToSend / numOfDays);
            for (int dayCounter = 0; dayCounter < numOfDays; dayCounter++) {
                long days = (long) dayCounter * 1000l * 60l * 60l * 24l;
                long timestamp = System.currentTimeMillis() - days;
                for (int countSkill = 0; countSkill < numPerDay; countSkill++) {
                    String skillId = skillIds.get(random.nextInt(skillIds.size()));
                    String reportUrl = skillsConfig.getServiceUrl() + "/api/projects/" + project.getId() + "/skills/" + skillId;
                    post(rest, reportUrl, new ReportSkillRequest(userId, timestamp));
                }
            }
        }
    }

    private void post(RestTemplate restTemplate, String url) {
        post(restTemplate, url, null);
    }

    private void post(RestTemplate restTemplate, String url, Object data) {
        if (log.isDebugEnabled()) {
            log.debug("POST: " + url + " with [" + data + "]");
        }
        restTemplate.postForEntity(url, data, String.class);
    }

    private void createUser(String url) {
        if (!doesUserExist()) {
            RestTemplate restTemplate = new RestTemplate();
            UserInfoRequest userInfoRequest = new UserInfoRequest("Bill", "Gosling", skillsConfig.getUsername(), skillsConfig.getPassword());
            HttpEntity request = new HttpEntity<>(userInfoRequest, new HttpHeaders());
            restTemplate.put(url, request);
            log.info("\n-----------------\nCreated User:\n  email=[" + userInfoRequest.getEmail() + "]\n  password=[" + userInfoRequest.getPassword() + "]\n----------------");
        } else {
            log.info("User [" + skillsConfig.getUsername() + "] already exist");
        }
    }

    private void createRootAccount() {
        String url = skillsConfig.getServiceUrl();
        RestTemplate restTemplate = new RestTemplate();
        if (skillsConfig.isPkiMode()) {
            restTemplate.put(url + "/grantFirstRoot", null);
            log.info("\n-----------------\nCreated Root User:\n  DN=[" + getDn() + "]\n----------------");
        } else {
            UserInfoRequest userInfoRequest = new UserInfoRequest("Bill", "Gosling", skillsConfig.getUsername(), skillsConfig.getPassword());
            HttpEntity request = new HttpEntity<>(userInfoRequest, new HttpHeaders());
            restTemplate.put(url + "/createRootAccount", request);
            log.info("\n-----------------\nCreated Root User:\n  email=[" + userInfoRequest.getEmail() + "]\n  password=[" + userInfoRequest.getPassword() + "]\n----------------");
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
