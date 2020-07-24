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
        <button v-skills="'IronMan'">Report Skill Using Directive</button>
        OR
        <button v-on:click="report">Report Skill Using JS Util</button>
    </div>
</template>

<script>
    import { SkillsReporter } from '@skilltree/skills-client-vue';
    export default {
        name: "HelloWorldSkillsEventReporting",
        methods: {
            report() {
                SkillsReporter.reportSkill('DespicableMe')
            }
        },
    }
</script>

<style scoped>
    button {
        margin: 2rem;
    }
</style>
