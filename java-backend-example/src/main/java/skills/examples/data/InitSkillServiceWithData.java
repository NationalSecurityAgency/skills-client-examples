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
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import skills.examples.data.model.Badge;
import skills.examples.data.model.Movie;
import skills.examples.data.model.Project;
import skills.examples.data.model.Subject;
import skills.examples.data.serviceRequestModel.*;
import skills.examples.utils.RestTemplateFactory;
import skills.examples.utils.SkillsConfig;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMMM d, yyyy");


    @PostConstruct
    void load() throws Exception {
        log.info("SkillsConfig ["+skillsConfig+"]");
        Project project = sampleDatasetLoader.getProject();
        if (skillsConfig.getCreateRootAccount()) {
            createRootAccount();
        } else if (!skillsConfig.isPkiMode()) {
            createUser(skillsConfig.getServiceUrl() + "/createAccount");
        }

        String serviceUrl = skillsConfig.getServiceUrl();
        RestTemplate rest = restTemplateFactory.getTemplateWithAuth();

        if (rest.getForEntity(serviceUrl + "/app/projects", String.class).getBody().contains(project.getName())) {
            log.info("Project [" + project.getName() + "] already exist!");
            return;
        }
        String projectId = project.getId();
        post(rest, serviceUrl + "/app/projects/" + projectId, new ProjRequest(project.getName()));

        log.info("\nStarting to create schema for project [" + projectId + "]\n" +
                "  [" + project.getSubjects().size() + "] subjects\n" +
                "  [" + project.getBadges().size() + "] badges");
        String projectUrl = serviceUrl + "/admin/projects/" + projectId;
        addSubjects(project, rest, projectUrl);
        addBadges(project, rest, projectUrl);
        reportSkills(rest, project);

        log.info("Project [" + projectId + "] was created!");
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
            log.info("\nCreating [" + subject.getName() + "] subject with [" + subject.getMovies().size() + "] skills");
            String subjectUrl = projectUrl + "/subjects/" + subject.getId();
            post(rest, subjectUrl, new SubjRequest(subject.getName(), "", subject.getIconClass()));

            for (Movie movie : subject.getMovies()) {
                String skillUrl = subjectUrl + "/skills/" + movie.getId();
                SkillRequest skillRequest = new SkillRequest();
                skillRequest.setName(movie.getTitle());
                skillRequest.setDescription(buildDescription(movie));
                skillRequest.setHelpUrl(movie.getHomePage());
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

    private void reportSkills(RestTemplate rest, Project project) {
        double[] usersAndAchievementPercent = {.1, .24, .5, .7, .8};
        int numUsers = usersAndAchievementPercent.length;
        List<String> skillIds = new ArrayList<>();
        for (Subject subject : project.getSubjects()) {
            for (Movie movie : subject.getMovies()) {
                skillIds.add(movie.getId());
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

    private String buildDescription(Movie movie) {
        StringBuilder builder = new StringBuilder();
        builder.append(movie.getOverview());
        if (!StringUtils.isEmpty(movie.getTagline())) {
            builder.append("\n\n> " + movie.getTagline());
        }

        if (movie.getReleaseDate() != null) {
            builder.append("\n\n**Release Date:** ");
            builder.append(simpleDateFormat.format(movie.getReleaseDate()));
        }

        if (movie.getRuntime() != null) {
            int hours = (int) (movie.getRuntime() / 60);
            int minutes = (int) (movie.getRuntime() - (hours * 60));
            builder.append("\n\n**Length:** ");
            if (hours > 0) {
                builder.append(hours + " hour" + (hours > 1 ? "s" : "") + " ");
            }
            if (minutes > 0) {
                builder.append(minutes + " minute" + (minutes > 1 ? "s" : ""));
            }
        }

        if (movie.getRevenue() != null) {
            builder.append("\n\n**Revenue:** ");
            String res;
            if (movie.getRevenue() > 1000000) {
                String amount = new DecimalFormat("#.##").format(movie.getRevenue() / 1000000);
                builder.append("$" + amount + " million");
            } else if (movie.getRevenue() > 1000) {
                String amount = new DecimalFormat("#.##").format(movie.getRevenue() / 1000);
                builder.append("$" + amount + "k");
            } else {
                builder.append("$" + movie.getRevenue());
            }

        }
        return builder.toString();
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
            dn = ((X509Certificate) ks.getCertificate(ks.aliases().nextElement())).getIssuerX500Principal().getName();
        } catch (Exception e) {
            log.error("Unable to extract DN from certificate", e);
        }

        return dn;
    }
}
