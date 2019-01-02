BDroid
======

**PANT** (*PostgreSQL* - *Angular* - *NestJS* - *TypeScript*) Stack

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) using [Nrwl Nx](https://nrwl.io/nx).

## Quick Start & Documentation

[Watch a 5-minute video on how to get started with Nx.](http://nrwl.io/nx)

## Install

```bash
git clone https://github.optum.com/ees-osfi/bdroid.git bdroid
cd bdroid
npm install
```

## Generate your first application

Run `ng generate app webapp` to generate an application. When using Nx, you can create multiple applications and libraries in the same CLI workspace. Read more [here](http://nrwl.io/nx).

## Development server

Run `ng serve --project=webapp` or `npm run start` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

Run `npm run start:mock` for a mock server.

Run `npm run start -- --proxy-config proxy.conf.js` to start with proxy

## Code scaffolding

Run `ng generate component component-name --project=webapp` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build --project=webapp` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Just](https://jestjs.io/).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Cypress](https://www.cypress.io/).

## Docs

Run `npm run doc:build` to build docs.
Run `npm run doc:serve` to serve docs.

## Deploy

Run `npm run deploy:mock` to deploy demo app to gp-pages.
 
After deployment, App should be available at [Hosted on GitHub](https://github.optum.com/pages/ees-osfi/bdroid/index.html)

Analyzing and Visualizing the Dependency Graph (affected modules by uncommitted changes)

`npm run affected:dep-graph -- --uncommitted`

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
