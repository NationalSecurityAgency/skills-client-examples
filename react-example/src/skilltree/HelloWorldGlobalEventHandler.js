import React, { useState } from 'react';
import {SkillsReporter} from '@skilltree/skills-client-react';

const HelloWorldGlobalEventHandler = () => {
    const [res, setRes] = useState('');

    React.useEffect(()=>{
        // just so we can always see the response
        SkillsReporter.configure({
            notifyIfSkillNotApplied: true,
        });
        SkillsReporter.addSuccessHandler((result) => {
            setRes(JSON.stringify(result, null, ' '));
        });
    }, []);

    return (
        <div>
            <pre><code>{res}</code></pre>
        </div>
    )
};

export default HelloWorldGlobalEventHandler;
