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
<template>
    <div>
        <h1>Current User's Skills Display</h1>
        <hello-world-skills-display/>
        <h1>Report Skill Events</h1>
        <hello-world-skills-event-reporting/>
        <h3>Result:</h3>
        <hello-world-global-event-handler/>
    </div>
</template>

<script>
    import HelloWorldSkillsDisplay from "./components/HelloWorldSkillsDisplay";
    import HelloWorldSkillsEventReporting from "./components/HelloWorldSkillsEventReporting";
    import HelloWorldGlobalEventHandler from "./components/HelloWorldGlobalEventHandler";

    export default {
        name: 'App',
        components: {
            HelloWorldGlobalEventHandler,
            HelloWorldSkillsEventReporting,
            HelloWorldSkillsDisplay,
        }
    }
</script>

<style>
    body {
        margin: 0px;
        padding-bottom: 3rem;
        text-align: center;
    }
</style>
