import React from 'react';
import { SkillsReporter } from '@skilltree/skills-client-react';

const HelloWorldSkillsEventReporting = () => {
    const reportSkill = () => {
        SkillsReporter.reportSkill('IronMan')
    };

    return (
        <div>
            <button onClick={() => reportSkill()}>
                Report Skill
            </button>
        </div>
    );
};

export default HelloWorldSkillsEventReporting;
