import React from 'react';
import './App.css';
import HelloWorldSkillsDisplay from './skilltree/HelloWorldSkillsDisplay';
import HelloWorldSkillsEventReporting from './skilltree/HelloWorldSkillsEventReporting';
import HelloWorldGlobalEventHandler from './skilltree/HelloWorldGlobalEventHandler';

function App() {
  return (
    <div className="App">
        <h1>Current User's Skills Display</h1>
        <HelloWorldSkillsDisplay />

        <h1>Report Skill Events</h1>
        <HelloWorldSkillsEventReporting />
        <h3>Result:</h3>
        <HelloWorldGlobalEventHandler />
    </div>
  );
}

export default App;
