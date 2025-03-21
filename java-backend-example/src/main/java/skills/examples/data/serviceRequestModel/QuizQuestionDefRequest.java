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
package skills.examples.data.serviceRequestModel;

import java.util.List;

public class QuizQuestionDefRequest {
    private String question;
    private String questionType;
    private String answerHint;
    private List<QuizAnswerDefRequest> answers;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getAnswerHint() {
        return answerHint;
    }

    public void setAnswerHint(String answerHint) {
        this.answerHint = answerHint;
    }

    public List<QuizAnswerDefRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswerDefRequest> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuizQuestionDefRequest{" +
                "question='" + question + '\'' +
                ", questionType='" + questionType + '\'' +
                ", answerHint='" + answerHint + '\'' +
                ", answers=" + answers +
                '}';
    }
}
