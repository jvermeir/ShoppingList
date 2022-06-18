import * as ReactDOM from 'react-dom/client';

import App from './app/app';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <App/>
);

/* TODO:
    surrounding <App /> with <React.StrictMode> cause query to be executed twice.
    this is intended behavior (https://stackoverflow.com/questions/60618844/react-hooks-useeffect-is-called-twice-even-if-an-empty-array-is-used-as-an-ar)
    Why is this? should i use strict mode?
 */
