# Gomoku Royale - Frontend Documentation:

## Table of Contents

- [Gomoku Royale - Frontend Documentation:](#gomoku-royale---frontend-documentation)
    1. [Introduction](#introduction)
    2. [File Structure](#file-structure)
    3. [Components](#components)
    4. [Services](#services)
    5. [Routing](#routing)
    6. [Authentication](#authentication)
    7. [Testing](#testing)
    8. [Conclusion](#conclusion)

## Introduction

This document provides a comprehensive guide to the frontend of the Gomoku Royale application. The frontend is a single
page application built using React, a popular JavaScript library for building user interfaces.
This client application makes use of a Gomoku Royale API.

## File Structure

The frontend is organized into the following directories:

- `js`
    - `public`: Contains the static assets for the application, such as the `index.html` file.
    - `src`: Contains the source code for the application.
        - `Assets`: Contains the images used in the application.
        - `Components`: Contains the React components used in the application.
        - `Services`: Contains the services used in the application that communicate with the external API.
        - `Domain`: Contains the domain objects used in the application.
        - `Layout`: Contains the layout components used in the application.
        - `Utils`: Contains utility functions used in the application.
        - `App.tsx`: The main React component for the application.
        - `index.tsx`: The entry point for the application.
    - `tests`: Contains the tests for the application.

## Components

The components directory contains the React components used in the application. The components are organized into
subdirectories based on their function. For example, the `Game` directory contains all the components used to render
the game board and the `Lobby` directory contains all the components used to render the lobby.

Each component is designed with the principles of React in mind. They are modular, reusable, and encapsulate their own
state and logic. The state of a component is managed using React's useState hook, which allows the component to maintain
and update its own state in response to user actions or other events. 

For example, a GamePresentation component might maintain
a state representing the current state of the game board. This state would be updated in response to user actions, such
as making a move. The component would also define a set of actions that can be performed on its state, such as makeMove
or resetBoard. The components also make use of React's useEffect hook to perform side effects, such as fetching data
from the server or setting up event listeners. These side effects are performed in response to changes in the
component's props or state.

## Services

The services directory contains the services used in the application to communicate with the external API. These
services are responsible for sending HTTP requests to the server and handling the responses. For example, the
UserServices.ts file contains functions for registering a new user, logging in a user, logging out a user, fetching a
user's data, fetching a user's home data, fetching ranking data, and fetching a user's stats.

## Routing

The application uses React Router for routing. Routes are defined in the App.tsx file. When a route is accessed, the
application renders the component associated with that route. For example, the /me route renders the Me component. The
application also uses protected routes to restrict access to certain pages based on whether the user is logged in or
not.

## Authentication

Authentication in the application is handled using cookies. When a user logs in, a session cookie is created. This
cookie is then used to authenticate the user on subsequent requests. The getCookie function is used to retrieve the
session cookie. The RequireAuthn component protects the routes that should only be accessible to authenticated
users. If an unauthenticated user tries to access a protected route, they are redirected to the login page

## Testing

The application uses Jest for unit testing and Playwright for end-to-end testing. Jest is a JavaScript testing framework
that is used to write unit and integration tests. It provides a set of APIs for structuring tests, making assertions,
and mocking dependencies. Playwright is a Node.js library for automating browser tasks. It provides a high-level API for
creating, interacting with, and evaluating the state of web pages. In the context of this application, Playwright is
used to automate user interactions with the application and verify that it behaves correctly. To run the tests, you can
use the npm test command. The tests are located in the test's directory.

## Conclusion

This document provides a comprehensive guide to the frontend of the Gomoku Royale application. It covers the file
structure, components, services, routing, authentication, and testing. This should serve as a useful resource for
developers working on the project.

Main challenges:
- Managing state in a large application
- Handle the time that was given to conclude the project

Future improvements:
- Add more tests, especially for the end-to-end tests and tests with jest
- Add more features to the game
- Improve the UI/UX of the application, using libraries like TailwindCSS
- Improve the performance of the application
- Improve the security of the application
- Improve the accessibility of the application, like google login
- Add server side events to the application to the polling on Game