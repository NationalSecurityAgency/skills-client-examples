/**
 * Copyright 2026 SkillTree
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

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import skills.examples.data.model.Project;
import skills.examples.data.model.Quiz;
import skills.examples.data.model.Survey;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class SampleDatasetLoader {

    @Value("classpath:projects.json")
    Resource projectsResourceFile;

    @Value("classpath:quiz.json")
    Resource quizResourceFile;

    @Value("classpath:survey.json")
    Resource surveyResourceFile;

    public List<Project> getProjects() {
        ObjectMapper jsonMapper = new ObjectMapper();
        try (InputStream is = projectsResourceFile.getInputStream()) {
            List<Project> projects = jsonMapper.readValue(is, new TypeReference<List<Project>>() {});
            return projects;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample project data", e);
        }
    }

    public List<Quiz> getQuizzes() {
        ObjectMapper jsonMapper = new ObjectMapper();
        try (InputStream is = quizResourceFile.getInputStream()) {
            List<Quiz> quizzes = jsonMapper.readValue(is, new TypeReference<List<Quiz>>() {});
            return quizzes;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load quiz data", e);
        }
    }

    public List<Survey> getSurveys() {
        ObjectMapper jsonMapper = new ObjectMapper();
        try (InputStream is = surveyResourceFile.getInputStream()) {
            List<Survey> surveys = jsonMapper.readValue(is, new TypeReference<List<Survey>>() {});
            return surveys;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load survey data", e);
        }
    }

}
