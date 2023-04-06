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
package skills.examples.data.serviceResponseModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizQuestionInfoResponse {
    private Integer id;
    private List<QuizAnswerOptionsInfoResponse> answers;
    private String questionType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<QuizAnswerOptionsInfoResponse> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswerOptionsInfoResponse> answers) {
        this.answers = answers;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    @Override
    public String toString() {
        return "QuizQuestionInfoResponse{" +
                "id=" + id +
                ", answers=" + answers +
                ", questionType='" + questionType + '\'' +
                '}';
    }
}
