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
import { Component, OnInit } from '@angular/core';
import { SkillsReporter } from '@skilltree/skills-client-ng';

@Component({
  selector: 'app-hello-world-global-event-handler',
  templateUrl: './hello-world-global-event-handler.component.html',
  styleUrls: ['./hello-world-global-event-handler.component.css']
})
export class HelloWorldGlobalEventHandlerComponent implements OnInit {

  result: string = '';

  constructor() { }

  ngOnInit(): void {
    // just so we can always see the response
    SkillsReporter.configure({
        notifyIfSkillNotApplied: true,
    });

    SkillsReporter.addSuccessHandler((result: any) => {
      this.result = JSON.stringify(result, null, ' ')
    });
  }
}
