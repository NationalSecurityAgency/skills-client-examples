/*
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

context('Examples Tests', () => {

  beforeEach(() => {
    cy.server().route('/api/users/user4@email.com/token').as('getToken')
  })

  it('test vue.js example', () => {
    cy.visit('http://localhost:8085/')
    cy.wait('@getToken')
    cy.wrapIframe().contains('Overall Points');

    cy.contains('Report Skill Using Directive').click()
    cy.contains('"skillId": "IronMan"')

    cy.contains('Report Skill Using JS Util').click()
    cy.contains('"skillId": "DespicableMe"')
  })


  it('test react example', () => {
    cy.visit('http://localhost:3000/')
    cy.wait('@getToken')
    cy.wrapIframe().contains('Overall Points');

    cy.get('button').contains('Report Skill').click()
    cy.contains('"skillId": "IronMan"')
  })


  it('test pure js example', () => {
    cy.visit('http://localhost:8092/')
    cy.wait('@getToken')
    cy.wrapIframe().contains('Overall Points');

    cy.get('button').contains('Report Skill').click()
    cy.contains('"skillId": "IronMan"')
  })
})
