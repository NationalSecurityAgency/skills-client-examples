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

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizInfoResponse {
    private String quizType;

    private List<QuizQuestionInfoResponse> questions;

    public String getQuizType() {
        return quizType;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public List<QuizQuestionInfoResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionInfoResponse> questions) {
        this.questions = questions;
    }


    @Override
    public String toString() {
        return "QuizInfoResponse{" +
                "quizType='" + quizType + '\'' +
                ", questions=" + questions +
                '}';
    }
}
