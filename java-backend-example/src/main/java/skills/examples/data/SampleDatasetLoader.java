package skills.examples.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import skills.examples.data.model.Project;

import java.io.IOException;

@Component
public class SampleDatasetLoader {

    @Value("classpath:movies.json")
    Resource resourceFile;

    public Project getProject() {
        ObjectMapper jsonMapper = new ObjectMapper();
        Project project = null;
        try {
            project = jsonMapper.readValue(resourceFile.getURL(), Project.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample datea", e);
        }
        project.setName("MoviesA");
        project.setId(project.getName().toLowerCase());

        return project;
    }
}
