import '@testing-library/jest-dom';
import {createMemoryHistory} from 'history';
import {act, render, screen} from '@testing-library/react';
import {App} from '../app';
import {CategoriesPage} from "./categories";
import Hello from "./hello";
import {rest} from 'msw';
import {setupServer} from "msw/node";
import {logger} from "nx/src/utils/logger";

const renderApp = () => {
  render(<App/>);
};

const handlers = [
  rest.get('/api/categorie', (req, res, ctx) => {
    return res(ctx.json([{
        id: "8b67d1ab-07ff-4a66-8438-571dff7fdc09",
        name: "c1",
        shopOrder: 111111
      }, {
        id: "173857df-955e-4434-a025-4b2e794a78bf",
        name: "1",
        shopOrder: 0
      }]),
    )
  }),
  rest.post('*', (req, res) => {
    logger.error(`[MSW] Warning: Unmocked GraphQL request: ${req.url}`);
    logger.error(req.body);
    return res();
  }),
  rest.get('*', (req, res) => {
    logger.error(`[MSW] Warning: Unmocked GraphQL request: ${req.url}`);
    logger.error(req.body);
    return res();
  }),
];

const server = setupServer(...handlers);

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())

describe('Category maintenance page', () => {
  it('say hello', async () => {
    const history = createMemoryHistory();
    history.push('/hello');
    renderApp();

    await screen.getByText('Hello', {exact: false});
  });

  it('a list of categories should be shown', async () => {
    // history.push('/categories');
    // renderApp(history);
    // await act(async () => {
    //   render(<CategoriesPage/>);
    //   screen.getByText('Bezig met laden...', {exact: false});
    //   // await screen.getByText('c1', {exact: false});}
    // });
    render(<CategoriesPage/>);
    screen.getByText('Bezig met laden...', {exact: false});
  });
});
