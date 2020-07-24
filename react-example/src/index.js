import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import { SkillsConfiguration } from '@skilltree/skills-client-react';

SkillsConfiguration.configure({
  serviceUrl: 'http://localhost:8080',
  projectId: 'movies',
  authenticator: 'http://localhost:8090/api/users/user4@email.com/token',
});

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
