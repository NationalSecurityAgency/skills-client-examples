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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import skills.examples.data.model.Project;

import java.io.IOException;
import java.util.List;

@Component
public class SampleDatasetLoader {

    @Value("classpath:projects.json")
    Resource resourceFile;

    public List<Project> getProjects() {
        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            List<Project> projects = jsonMapper.readValue(resourceFile.getURL(), new TypeReference<List<Project>>() {});
            return projects;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample data", e);
        }
    }
}
