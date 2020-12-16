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
import skills.examples.utils.RestTemplateFactory;
import skills.examples.utils.SkillsConfig;
import skills.examples.data.serviceRequestModel.UserInfoRequest;
import skills.examples.data.model.Badge;
import skills.examples.data.model.Movie;
import skills.examples.data.model.Project;
import skills.examples.data.model.Subject;
import skills.examples.data.serviceRequestModel.*;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private int numOfUsers = 10;

    @PostConstruct
    void load() throws Exception {
        Project project = sampleDatasetLoader.getProject();
        createUser(skillsConfig.getServiceUrl() + "/createAccount");

        String serviceUrl = skillsConfig.getServiceUrl();
        RestTemplate rest = restTemplateFactory.getTemplateWithAuth();

        if (rest.getForEntity(serviceUrl + "/app/projects", String.class).getBody().contains(project.getName())){
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
        addDep(project, rest, projectUrl);
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

            List<Movie> movies = subject.getMovies();
            Set<String> ids = new HashSet<>();
            for (int i = 0; i < movies.size(); i++) {
                Movie movie = movies.get(i);

                // ignore dups
                if (ids.contains(movie.getId())) {
                    continue;
                }
                ids.add(movie.getId());

                String skillUrl = subjectUrl + "/skills/" + movie.getId();
                SkillRequest skillRequest = new SkillRequest();
                if (i % 3 == 0) {
                    skillRequest.setNumPerformToCompletion(3);
                } else if (i % 2 == 0) {
                    skillRequest.setNumPerformToCompletion(2);
                }

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

    private void addDep(Project project, RestTemplate rest, String projectUrl) {
        post(rest, projectUrl + "/skills/TheAvengers/dependency/GuardiansoftheGalaxyVol2", null);
        post(rest, projectUrl + "/skills/GuardiansoftheGalaxyVol2/dependency/GuardiansoftheGalaxy", null);
        post(rest, projectUrl + "/skills/TheAvengers/dependency/DoctorStrange", null);
        post(rest, projectUrl + "/skills/TheAvengers/dependency/ThorRagnarok", null);
        post(rest, projectUrl + "/skills/AvengersAgeofUltron/dependency/Deadpool", null);
        post(rest, projectUrl + "/skills/TheAvengers/dependency/CaptainAmericaCivilWar", null);
        post(rest, projectUrl + "/skills/AvengersAgeofUltron/dependency/TheAvengers", null);
        post(rest, projectUrl + "/skills/AvengersAgeofUltron/dependency/WonderWoman", null);
    }

    private void reportSkills(RestTemplate rest, Project project) {
        Random random = new Random();

        double[] usersAndAchievementPercent = new double[numOfUsers];
        for (int i = 0; i < numOfUsers; i++) {
            usersAndAchievementPercent[i] = random.nextDouble();
        }

        int numUsers = usersAndAchievementPercent.length;
        List<String> skillIds = new ArrayList<>();
        for (Subject subject : project.getSubjects()) {
            for (Movie movie : subject.getMovies()) {
                skillIds.add(movie.getId());
            }
        }


        for (int i = 0; i < numUsers; i++) {
            String userId = "user" + i + "@email.com";
            double percentToAchieve = usersAndAchievementPercent[i];
            int eventsToSend = (int) (skillIds.size() * percentToAchieve) * 3;
            int numOfDays = percentToAchieve > .5 ? 46 : 12;
            log.info("\nReporting skills for user [" + userId + "]\n" +
                    "   number of events to send = [" + eventsToSend + "]\n" +
                    "   May take a minute.... Please hold!");

            int numPerDay = (int) (eventsToSend / numOfDays);
            long oneDay = 1000l * 60l * 60l * 24l;
            for (int dayCounter = 0; dayCounter < numOfDays; dayCounter++) {
                long days = (long)dayCounter * oneDay;
                long timestamp =  System.currentTimeMillis() - days;
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
        if (log.isDebugEnabled()){
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

}
