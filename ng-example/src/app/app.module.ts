/*
Copyright 2020 SkillTree

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { SkillsLevelModule, SkillsDisplayModule, SkilltreeModule } from '@skilltree/skills-client-ng'

import { AppComponent } from './app.component';
import { HelloWorldGlobalEventHandlerComponent } from './components/hello-world-global-event-handler/hello-world-global-event-handler.component';
import { HelloWorldSkillsDisplayComponent } from './components/hello-world-skills-display/hello-world-skills-display.component';
import { HelloWorldEventReportingComponent } from './components/hello-world-event-reporting/hello-world-event-reporting.component';

@NgModule({
  declarations: [
    AppComponent,
    HelloWorldGlobalEventHandlerComponent,
    HelloWorldSkillsDisplayComponent,
    HelloWorldEventReportingComponent
  ],
  imports: [
    BrowserModule,
    SkillsLevelModule,
    SkilltreeModule,
    SkillsDisplayModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
