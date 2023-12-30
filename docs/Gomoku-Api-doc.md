# Gomoku Royale API Documentation

- The following documentation describes the HTTP API of the Gomoku Royale game.
- The API is implemented in Kotlin using the Spring Boot framework, and is a REST API,
designed to be consumed by a front-end application.

# Table of Contents

- [Server](#server)
- [Home](#home)
    - [Get System Info](#get-system-info)
    - [Get Home Page (Unauthenticated User)](#get-home-page-unauthenticated-user)
- [Users](#users)
    - [Create User](#create-user)
    - [Create Token](#create-token)
    - [Logout](#logout)
    - [Get User by ID](#get-user-by-id)
    - [Get Home Page with User Login](#get-home-page-with-user-login)
    - [Get Stats Page of User by id](#get-stats-page-of-user-by-id)
    - [Get Ranking Info Page of all Users](#get-ranking-info-page-of-all-users)
    - [Get Ranking Info Page by username](#get-ranking-info-page-by-username)
    - [Update User](#update-user)
    - [Get User Stats by Username](#get-user-stats-by-username)
- [Game](#game)
    - [Get Game Info by id](#get-game-info-by-id)
    - [Play a Round](#play-a-round)
    - [Get All Games By User Page](#get-all-games-by-user-page)
    - [Get All Games](#get-all-games)
    - [Leave Game](#leave-game)
    - [Matchmaking](#matchmaking)
    - [Exit Matchmaking](#exit-matchmaking)
    - [Matchmaking Status](#matchmaking-status)
    - [Get All Variants](#get-all-variants)

# Server

**Description** :

- The server application runs local on **URL**: http://localhost:8080
- All the responses have _`application/vnd.siren+json`_ as content-type
  for more information about the Siren format go to: https://github.com/kevinswiber/siren
- All the problematic responses have _`application/problem+json`_ as content-type
  for more information about the Problem Details for HTTP APIs go to: https://datatracker.ietf.org/doc/html/rfc7807
- Important reminder: The Content-Type header of the request must be set to _`application/json`_ for all the POST and
  PUT requests.

# Home

**Description**: HTTP API requests for all types of users, authenticated or not, to retrieve system information and
statistic information.

#

# Get System Info

    Retrieve information about the Gomoku game system.

**Endpoint:** `/api/system`

**Method:** GET

**Success Response Example:**

```json
{
  "class": [
    "system-info"
  ],
  "properties": {
    "systemInfo": "Gomoku Royale",
    "systemAuthors": "Gonçalo Frutuoso and Daniel Carvalho",
    "systemVersion": "0.1.3"
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/system"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/home"
      ],
      "href": "/api"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/auth-home"
      ],
      "href": "/api/me"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    false
  ]
}
```

#

# Get Home Page (Unauthenticated User)

    Retrieve the home page for unauthenticated users and the link
    recipe to all the important URIs.

**Endpoint:** `/api`

**Method:** GET

**Success Response Example:**

```json
{
  "class": [
    "home"
  ],
  "properties": {
    "message": "Welcome to Gomoku Royale! Please log in to play."
  },
  "links": [],
  "recipeLinks": [
    {
      "rel": [
        "self"
      ],
      "href": "/api"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/home"
      ],
      "href": "/api"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/system-info"
      ],
      "href": "/api/system"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/ranking-info"
      ],
      "href": "/api/ranking?page=1"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/auth-home"
      ],
      "href": "/api/me"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/matchmaking"
      ],
      "href": "/api/games/matchmaking"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-matchmaking-status"
      ],
      "href": "/api/games/matchmaking/{mid}/status"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/exit-matchmaking-queue"
      ],
      "href": "/api/games/matchmaking/{mid}/exit"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-all-games"
      ],
      "href": "/api/games?page=1"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-all-games-by-user"
      ],
      "href": "/api/games/user/{uid}?page=1"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-user-by-id"
      ],
      "href": "/api/users/{uid}"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-user-stats"
      ],
      "href": "/api/stats/{uid}"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-user-stats-by-username"
      ],
      "href": "/api/stats/username/{name}"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/update-user"
      ],
      "href": "/api/users/update"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/logout"
      ],
      "href": "/api/logout"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
      ],
      "href": "/api/games/{gid}"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/play"
      ],
      "href": "/api/games/{gid}/play"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/leave"
      ],
      "href": "/api/games/{gid}/leave"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/login"
      ],
      "href": "/api/users/token"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/create-a-user"
      ],
      "href": "/api/users"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-all-variants"
      ],
      "href": "/api/games/variants"
    }
  ],
  "entities": [],
  "actions": [
    {
      "name": "register",
      "href": "/api/users",
      "method": "POST",
      "type": "application/x-www-form-urlencoded",
      "fields": [
        {
          "name": "username",
          "type": "text",
          "value": null
        },
        {
          "name": "email",
          "type": "text",
          "value": null
        },
        {
          "name": "password",
          "type": "text",
          "value": null
        }
      ],
      "requireAuth": [
        false
      ]
    },
    {
      "name": "login",
      "href": "/api/users/token",
      "method": "POST",
      "type": "application/x-www-form-urlencoded",
      "fields": [
        {
          "name": "username",
          "type": "text",
          "value": null
        },
        {
          "name": "password",
          "type": "text",
          "value": null
        }
      ],
      "requireAuth": [
        false
      ]
    }
  ],
  "requireAuth": [
    false
  ]
}
```

#

# Users

**Description**: HTTP API requests to create authenticated users and perform others functionalities for the same type of
users

#

# Create User

    Create a new user to be able to play Gomoku.

**Endpoint:** `/api/users`

**Method:** POST

**Request Example:**

```json
{
  "email": "backfire@gmail.com",
  "username": "backfire",
  "password": "Abacat345"
}
```

**Success Response Example:**

```json
{
  "class": [
    "register"
  ],
  "properties": {
    "uid": 133
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/users"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    false
  ]
}
```

**Response Header location**: /api/users/133

**Failure**

- Insecure password
- User already exists
- Insecure email

#

# Create Token

    Create an authentication token for a user.
    If request is made by a browser, the token is returned
    as a cookie as well the username.

**Endpoint:** `/api/users/token`

**Method:** POST

**Request Example:**

```json
{
  "username": "backfire",
  "password": "Abacat345"
}
```

**SuccessResponse Example**:

```json
{
  "class": [
    "login"
  ],
  "properties": {
    "token": "dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o="
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/users/token"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    false
  ]
}
```

**Failure**

- User or password are invalid

#

# Logout

    Log out a user and invalidate their token.

**Endpoint:** `/api/users/logout`

**Method:** POST

**Request Example with authorization Bearer token:** dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o=

**Success Response Example:**

```json
{
  "class": [
    "logout"
  ],
  "properties": {
    "message": "Token dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o= revoked. Logout succeeded"
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/logout"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failure**

- Token not revoked
- Authorization token is invalid

#

## Get User by ID

    Retrieve user information by their ID.

**Endpoint:** `/api/users/{uid}`
**Method:** GET

**Parameters:**

- `uid` (integer, path) - The ID of the user
- if not passed, the user id will be the one of the authenticated user
- e.g: {uid} = 1.

**Success Response Example:**

```json
{
  "class": [
    "user"
  ],
  "properties": {
    "id": 1,
    "username": "alice",
    "email": "alicepereira@gmail.com"
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/users/1"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [
    {
      "name": "update-user",
      "href": "/api/users/update",
      "method": "PUT",
      "type": "application/x-www-form-urlencoded",
      "fields": [
        {
          "name": "username",
          "type": "text",
          "value": null
        },
        {
          "name": "email",
          "type": "text",
          "value": null
        },
        {
          "name": "password",
          "type": "text",
          "value": null
        }
      ]
    }
  ],
  "requireAuth": [
    true
  ]
}
```

**Failure**

- User does not exist
- Authorization token is invalid

#

## Get Home Page with User Login

    Retrieve the home page with user login for authenticated users.

**Endpoint:** `/api/me`

**Method:** GET

**Authorization Bearer token Example:**
dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o=

**Success Response Example:**

```json
{
  "class": [
    "user-home"
  ],
  "properties": {
    "id": 133,
    "username": "backfire",
    "message": "Welcome Player! Lets play."
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/me"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/system-info"
      ],
      "href": "/api/system"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/ranking-info"
      ],
      "href": "/api/ranking?page=0"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [
    {
      "name": "matchmaking",
      "href": "/api/games/matchmaking",
      "method": "POST",
      "type": "application/x-www-form-urlencoded",
      "fields": [
        {
          "name": "uid",
          "type": "hidden",
          "value": "133"
        },
        {
          "name": "variant",
          "type": "text",
          "value": null
        }
      ],
      "requireAuth": [
        true
      ]
    },
    {
      "name": "logout",
      "href": "/api/logout",
      "method": "POST",
      "type": "application/json",
      "fields": [],
      "requireAuth": [
        true
      ]
    }
  ],
  "requireAuth": [
    true
  ]
}
```

**Failure**

- Authorization token is invalid

#

### Get Stats Page of User by id

    Retrieve the stats page of a user by his Id.

**Endpoint:** `/api/stats/{uid}`

**Method:** GET
**Parameter**

- `uid` (integer, path) - The ID of the user
- if not passed, the user id will be the one of the authenticated user
- e.g: {uid} = 7.

**Authorization Bearer token Example:**
dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o=

**Success Response Example:**

```json
{
  "class": [
    "user-statistics"
  ],
  "properties": {
    "id": 7,
    "username": "user-1412827057457771723",
    "gamesPlayed": 0,
    "wins": 0,
    "losses": 0,
    "rank": 7,
    "points": 0
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/stats/7"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-all-games-by-user"
      ],
      "href": "/api/games/user/7?page=1"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    false
  ]
}
```

**Failure**

- User does not exist
- Stats of user does not exist

#

# Get Ranking Info Page of all Users

    Retrieve the ranking page of all users. Each page contains 20 users.

**Endpoint:** `/api/ranking?page={page}`

**Method:** GET

**Success Response Example:**

```json
{
  "class": [
    "ranking-info"
  ],
  "properties": {
    "page": 0,
    "pageSize": 20
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/ranking?page=0"
    },
    {
      "rel": [
        "last"
      ],
      "href": "/api/ranking?page=0"
    }
  ],
  "recipeLinks": [],
  "entities": [
    {
      "clazz": [
        "user-statistics"
      ],
      "properties": {
        "id": 13,
        "username": "user-1489691534341247057",
        "gamesPlayed": 1,
        "wins": 1,
        "losses": 0,
        "rank": 1,
        "points": 110
      },
      "links": [
        {
          "rel": [
            "self"
          ],
          "href": "/api/stats/13"
        }
      ],
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-user-stats"
      ],
      "requireAuth": [
        false
      ]
    },
    {
      "clazz": [
        "user-statistics"
      ],
      "properties": {
        "id": 1,
        "username": "alice",
        "gamesPlayed": 1,
        "wins": 0,
        "losses": 0,
        "rank": 2,
        "points": 0
      },
      "links": [
        {
          "rel": [
            "self"
          ],
          "href": "/api/stats/1"
        }
      ],
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-user-stats"
      ],
      "requireAuth": [
        false
      ]
    },
    {
      "clazz": [
        "user-statistics"
      ],
      "properties": {
        "id": 2,
        "username": "bob",
        "gamesPlayed": 1,
        "wins": 0,
        "losses": 0,
        "rank": 3,
        "points": 0
      },
      "links": [
        {
          "rel": [
            "self"
          ],
          "href": "/api/stats/2"
        }
      ],
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-user-stats"
      ],
      "requireAuth": [
        false
      ]
    },
    "..."
  ],
  "actions": [],
  "requireAuth": [
    false
  ]
}
```

**Failure**

- Ranking does not exist
- Invalid page

#

# Get Ranking Info Page by username

    Retrieve the ranking page of all users. Each page contains 20 users.

**Endpoint:** `/api/ranking/{name}?page={page}`
**Method:** GET

**Parameters:**

- `name` (string, path) - The username of the user
- e.g: {name} = alice
- `page` (integer, query) - The page number
- e.g: {page} = 1
- if not passed, the page will be 1
- if passed, the page must be greater than 1
- if passed, the page must be less than the total number of pages

**Success Response Example:**

```json
{
  "class": [
    "ranking-info"
  ],
  "properties": {
    "page": 1,
    "pageSize": 20
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/ranking/{name}?page=1"
    },
    {
      "rel": [
        "next"
      ],
      "href": "/api/ranking/{name}?page=2"
    },
    {
      "rel": [
        "previous"
      ],
      "href": "/api/ranking/{name}?page=0"
    }
  ],
  "entities": [
    {
      "class": [
        "user-statistics"
      ],
      "properties": {
        "username": "alice",
        "gamesPlayed": 10,
        "wins": 5,
        "losses": 3,
        "draws": 2,
        "rank": 1,
        "points": 100
      },
      "links": [
        {
          "rel": [
            "self"
          ],
          "href": "/api/users/alice"
        }
      ]
    },
    // More user statistics entities...
  ]
}
```

**Failure**

- Invalid page number

```json
{
"status": 400,
"title": "Invalid page number",
"detail": "The page number must be greater than 0",
"instance": "/api/ranking/alice?page=-1"
}
```

#   Update User

    Update the information of a user.
    Can update the username, email and password.

**Endpoint:** `/api/users/update`

**Method:** PUT

**Request Example:**

```json
{
  "username": "backfire",
  "email": "backfire@gmail.com",
  "password": "Abnasmdka345"
}
```

**Authorization Bearer token Example:** dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o=

**Success Response Example:**

```json
{
  "class": [
    "update-user"
  ],
  "properties": {
    "message": "User with id 134 updated successfully"
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/users/update"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failure**

- User does not exist
- Authorization token is invalid
- Insecure email
- Insecure password

#

# Get User Stats by Username

    Retrieve the stats page of a user by his username.

**Endpoint:** `/api/stats/username/{name}`

**Method:** GET

**Parameters:**

- `name` (string, path) - The username of the user
- e.g: {name} = alice

**Success Response Example:**

```json
{
  "class": [
    "user-statistics"
  ],
  "properties": {
    "uid": 1,
    "username": "alice",
    "gamesPlayed": 1,
    "wins": 0,
    "losses": 0,
    "rank": 78,
    "points": 0
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/stats/username/alice"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-all-games-by-user"
      ],
      "href": "/api/games/user/1?page=1"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    false
  ]
}
```

**Failure**

- User does not exist
- Stats of user does not exist

#

# Game

**Description**: HTTP API requests to create games, enter matchmaking ,play rounds and get information about the games.

### Get Game Info by id

    Retrieve information about a specific Gomoku game by its ID.

**Endpoint:** `/api/games/{gid}`

**Method:** GET

**Parameters:**

- `gid` (integer, path) - The ID of the game.
- e.g: {gid} = 2

**Authorization Bearer token Example:** dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o=

**Success Response Example:**

```json
{
  "class": [
    "game"
  ],
  "properties": {
    "game": {
      "id": 2,
      "board": {
        "moves": {},
        "turn": "BLACK"
      },
      "userBlack": {
        "id": {
          "value": 8
        },
        "username": "user-3273650057670013291",
        "email": {
          "value": "email-3765094180298157455@test.com"
        },
        "passwordValidation": {
          "validationInfo": "$2a$10$TMGrBD5aP6pUfUIu5BHq.umRCRFZWx.y.n7U5k0MaZzf3RgB2x5a."
        }
      },
      "userWhite": {
        "id": {
          "value": 9
        },
        "username": "user-3773754256422436144",
        "email": {
          "value": "email-2852595943512092003@test.com"
        },
        "passwordValidation": {
          "validationInfo": "$2a$10$rqubWPTlzEDRRzOGhg3UTuWutwyAbySmF39F87cOqHvf7sZsxPGI6"
        }
      },
      "state": "NEXT_PLAYER_BLACK",
      "variant": {
        "name": "STANDARD",
        "boardDim": 15,
        "playRule": "STANDARD",
        "openingRule": "STANDARD",
        "points": 110
      },
      "created": "2023-12-08T20:30:41Z"
    },
    "pollingTimOut": 3000
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/2"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [
    {
      "name": "play",
      "href": "/api/games/2/play",
      "method": "POST",
      "type": "application/x-www-form-urlencoded",
      "fields": [
        {
          "name": "row",
          "type": "number",
          "value": null
        },
        {
          "name": "column",
          "type": "number",
          "value": null
        }
      ],
      "requireAuth": [
        true
      ]
    },
    {
      "name": "leave-game",
      "href": "/api/games/2/leave",
      "method": "PUT",
      "type": "application/json",
      "fields": [],
      "requireAuth": [
        true
      ]
    }
  ],
  "requireAuth": [
    true
  ]
}
```

**Failure**

- Game does not exist
- Authorization token is invalid

#

# Play a Round

    Make a move in the Gomoku game.

**Endpoint:** `/api/games/{gid}/play`

**Method:** PUT

**Parameters:**

- `gid` (integer, path) - The ID of the game.
- e.g: {gid}: 51

**First Request Example**

```json
{
  "column": 3,
  "row": 3
}
```

**Authorization Bearer token Example:** dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o=

**Success Response example**

```json
{
  "class": [
    "play"
  ],
  "properties": {
    "game": {
      "id": 51,
      "board": {
        "moves": {
          "3D": "BLACK"
        },
        "turn": "WHITE"
      },
      "userBlack": {
        "id": {
          "value": 134
        },
        "username": "world",
        "email": {
          "value": "hello@gmail.com"
        },
        "passwordValidation": {
          "validationInfo": "$2a$10$3rdLevKFqK.gpGxoVgDG/.zZho7cv15M7obSjgB4VrH9f5pJhofg6"
        }
      },
      "userWhite": {
        "id": {
          "value": 133
        },
        "username": "backfire",
        "email": {
          "value": "backfire@gmail.com"
        },
        "passwordValidation": {
          "validationInfo": "$2a$10$hnwigk1HMn5fGcqUGRaEr.UHrIDpBa2IVZX863L.ZBbPsqWLJ0r3a"
        }
      },
      "state": "NEXT_PLAYER_WHITE",
      "variant": {
        "name": "STANDARD",
        "boardDim": 15,
        "playRule": "STANDARD",
        "openingRule": "STANDARD",
        "points": 110
      },
      "created": "2023-12-09T23:13:06Z"
    }
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/51/play"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
      ],
      "href": "/api/games/51"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Second Request Example**

```json
{
  "column": 2,
  "row": 1
}
```

**Authorization Bearer token Example:**
zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

**Success Response example**

```json
{
  "class": [
    "play"
  ],
  "properties": {
    "game": {
      "id": 51,
      "board": {
        "moves": {
          "3D": "BLACK",
          "1C": "WHITE"
        },
        "turn": "BLACK"
      },
      "userBlack": {
        "id": {
          "value": 134
        },
        "username": "world",
        "email": {
          "value": "hello@gmail.com"
        },
        "passwordValidation": {
          "validationInfo": "$2a$10$3rdLevKFqK.gpGxoVgDG/.zZho7cv15M7obSjgB4VrH9f5pJhofg6"
        }
      },
      "userWhite": {
        "id": {
          "value": 133
        },
        "username": "backfire",
        "email": {
          "value": "backfire@gmail.com"
        },
        "passwordValidation": {
          "validationInfo": "$2a$10$hnwigk1HMn5fGcqUGRaEr.UHrIDpBa2IVZX863L.ZBbPsqWLJ0r3a"
        }
      },
      "state": "NEXT_PLAYER_BLACK",
      "variant": {
        "name": "STANDARD",
        "boardDim": 15,
        "playRule": "STANDARD",
        "openingRule": "STANDARD",
        "points": 110
      },
      "created": "2023-12-09T23:13:06Z"
    }
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/51/play"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
      ],
      "href": "/api/games/51"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failures**

- Game does not exist
- Invalid User
- Invalid State
- Invalid Time
- Invalid Turn
- Invalid Position
- Authorization token is invalid

### Get All Games By User Page

    Get a page with all the games of a user. Each page contains 20 games.

**Endpoint:** `/api/games/user/{uid}?page={page}`
**Method:** GET

**Parameters:**

- `uid` (integer, path) - The ID of the user.
- if not passed, the user id will be the one of the authenticated user
- e.g: {uid} = 13

**Authorization Bearer token Example:** zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

**Success Response example**

```json
{
  "class": [
    "game-list-of-user"
  ],
  "properties": {
    "uid": 13,
    "page": 1,
    "pageSize": 20
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/user/13?page=1"
    }
  ],
  "recipeLinks": [],
  "entities": [
    {
      "clazz": [
        "game"
      ],
      "properties": {
        "id": 4,
        "board": {
          "moves": {
            "1B": "BLACK"
          },
          "winner": "WHITE"
        },
        "userBlack": {
          "id": {
            "value": 12
          },
          "username": "user-3902351303747100599",
          "email": {
            "value": "email-1124265068213431992@test.com"
          },
          "passwordValidation": {
            "validationInfo": "$2a$10$uLTovN1ou3Vr84R9FcHg1OBRtZGvTNzY91UecTqIzagw1cNs7fdVi"
          }
        },
        "userWhite": {
          "id": {
            "value": 13
          },
          "username": "user-1489691534341247057",
          "email": {
            "value": "email-2586148888159197119@test.com"
          },
          "passwordValidation": {
            "validationInfo": "$2a$10$/ewkZsnuqin/L/W5C.n6Funa/s71MmMJA/lJ.1I7FHDNWgaAwdobS"
          }
        },
        "state": "PLAYER_WHITE_WON",
        "variant": {
          "name": "STANDARD",
          "boardDim": 15,
          "playRule": "STANDARD",
          "openingRule": "STANDARD",
          "points": 110
        },
        "created": "2023-12-08T20:30:43Z"
      },
      "links": [
        {
          "rel": [
            "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
          ],
          "href": "/api/games/4"
        }
      ],
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
      ],
      "requireAuth": [
        true
      ]
    }
  ],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failures**

- User does not exists
- Game not found
- Authorization token is invalid

#

# Get All Games

    Get a page with all games ever made in gomoku. Each page contains 20 games.

**Endpoint:** `/api/games?page={page}`

**Method:** GET

**Parameters:**

- `page` (integer, query, optional) - The page number to retrieve. Defaults to 1 if not provided.

**Authorization Bearer token Example:** zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

**SuccessResponse example**

```json
{
  "class": [
    "game-list"
  ],
  "properties": {
    "page": 1,
    "pageSize": 20
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games?page=1"
    },
    {
      "rel": [
        "next"
      ],
      "href": "/api/games?page=2"
    },
    {
      "rel": [
        "last"
      ],
      "href": "/api/games?page=2"
    }
  ],
  "recipeLinks": [],
  "entities": [
    {
      "clazz": [
        "game"
      ],
      "properties": {
        "id": 50,
        "board": {
          "moves": {
            "1B": "BLACK"
          },
          "turn": "WHITE"
        },
        "userBlack": {
          "id": {
            "value": 113
          },
          "username": "user-8308787776620469888",
          "email": {
            "value": "email-795825740530440591@test.com"
          },
          "passwordValidation": {
            "validationInfo": "$2a$10$PuiBAR4FdJlXEXWWuikpR.T6P9j8UawLIz/7QVzqEWBpL48K1EXny"
          }
        },
        "userWhite": {
          "id": {
            "value": 112
          },
          "username": "user-1099048709064103996",
          "email": {
            "value": "email-1642238726093648339@test.com"
          },
          "passwordValidation": {
            "validationInfo": "$2a$10$4LkDbEJPv/I/GwBhOIh9buriNiYQz6/jZ4o56FolW.IlTTg9jqTq2"
          }
        },
        "state": "NEXT_PLAYER_WHITE",
        "variant": {
          "name": "STANDARD",
          "boardDim": 15,
          "playRule": "STANDARD",
          "openingRule": "STANDARD",
          "points": 110
        },
        "created": "2023-12-08T23:25:09Z"
      },
      "links": [
        {
          "rel": [
            "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
          ],
          "href": "/api/games/50"
        }
      ],
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
      ],
      "requireAuth": [
        true
      ]
    },
    {
      "clazz": [
        "game"
      ],
      "properties": {
        "id": 49,
        "board": {
          "moves": {
            "1B": "BLACK"
          },
          "turn": "WHITE"
        },
        "userBlack": {
          "id": {
            "value": 111
          },
          "username": "user-248295855920547322",
          "email": {
            "value": "email-2386282122637424199@test.com"
          },
          "passwordValidation": {
            "validationInfo": "$2a$10$OfHYbzue4mIZFXKDLhz/QOOdN.Zn06LZdrD0OIh3LTHBU6qGCm3nm"
          }
        },
        "userWhite": {
          "id": {
            "value": 110
          },
          "username": "user-6838309380964837008",
          "email": {
            "value": "email-6391751316540922375@test.com"
          },
          "passwordValidation": {
            "validationInfo": "$2a$10$3Ho/MRITWKP35wgREnnIjeXh0fXyKhZtZBAfILdU.CJAdHMlOrcfe"
          }
        },
        "state": "NEXT_PLAYER_WHITE",
        "variant": {
          "name": "STANDARD",
          "boardDim": 15,
          "playRule": "STANDARD",
          "openingRule": "STANDARD",
          "points": 110
        },
        "created": "2023-12-08T23:25:07Z"
      },
      "links": [
        {
          "rel": [
            "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
          ],
          "href": "/api/games/49"
        }
      ],
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
      ],
      "requireAuth": [
        true
      ]
    },
    {
      "clazz": [
        "game"
      ],
      "properties": {
        "id": 48,
        "board": {
          "moves": {
            "1B": "BLACK"
          },
          "turn": "WHITE"
        },
        "userBlack": {
          "id": {
            "value": 109
          },
          "username": "user-8810124594880709527",
          "email": {
            "value": "email-6615536679771616680@test.com"
          },
          "passwordValidation": {
            "validationInfo": "$2a$10$GaqSArpecoRAXCN.6qX4L.uoX401xQHn9abHFpPFAy05ec6snLbSO"
          }
        },
        "userWhite": {
          "id": {
            "value": 108
          },
          "username": "user-6905392365555344835",
          "email": {
            "value": "email-5272107337195305591@test.com"
          },
          "passwordValidation": {
            "validationInfo": "$2a$10$XS.ohTQohZ3PUfaVNGOk4emOgBYL.b2wsKM95Hr2q8aBpFngU3Br2"
          }
        },
        "state": "NEXT_PLAYER_WHITE",
        "variant": {
          "name": "STANDARD",
          "boardDim": 15,
          "playRule": "STANDARD",
          "openingRule": "STANDARD",
          "points": 110
        },
        "created": "2023-12-08T23:25:05Z"
      },
      "links": [
        {
          "rel": [
            "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
          ],
          "href": "/api/games/48"
        }
      ],
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
      ],
      "requireAuth": [
        true
      ]
    },
    "..."
  ],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failures**

- Game does not exist
- Invalid page
- Authorization token is invalid

#

# Leave Game

    Leave the game by it's Id .

**Endpoint:** `/api/games/{gid}/leave`

**Method:** PUT

**Parameters:**

- `gid` (integer, path) - The ID of the game.
- e.g: {gid} = 51

**Authorization Bearer token Example:** zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

**Success Response example**

```json
{
  "class": [
    "leave-game"
  ],
  "properties": {
    "message": "User world left the game"
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/52/leave"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failures**

- Game already ended
- Game does not exist
- Invalid user
- Authorization token is invalid

#

# Matchmaking

    Enter or create a lobby of a game.

**Endpoint:** `/api/games/matchmaking`

**Method:** POST

**First Request Example**

```json
{
  "variant": "STANDARD"
}
```

**Authorization Bearer token Example:** dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o=

**Success Response Example**

```json
{
  "class": [
    "matchmaking"
  ],
  "properties": {
    "message": "User on waiting queue",
    "idType": "mid",
    "mid": 47
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/matchmaking"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [
    {
      "name": "leave-matchmaking",
      "href": "/api/games/matchmaking/47/exit",
      "method": "DELETE",
      "type": "application/json",
      "fields": [],
      "requireAuth": [
        true
      ]
    }
  ],
  "requireAuth": [
    true
  ]
}
```

**Second Request Example**

```json
{
  "variant": "STANDARD"
}
```

**Authorization Bearer token Example:** zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

**Success Response Example**

```json
{
  "class": [
    "matchmaking"
  ],
  "properties": {
    "message": "Match found",
    "idType": "gid",
    "id": 51
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/51"
    },
    {
      "rel": [
        "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/get-game-by-id"
      ],
      "href": "/api/games/51"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failures**

- Invalid User
- Variant does not exists
- User already in matchmaking
- Authorization token is invalid

#

# Exit Matchmaking

    Exit a matchmaking.

**Endpoint:** `/api/games/matchmaking/{mid}/exit`
**Method:** DEL

**Parameters:**

- `mid` (integer, path) - The ID of the matchmaking.
- e.g: {mid} = 48

**Authorization Bearer token Example:**
dSMo4PIL3LYbjwiiy8XYtKe4nyQIAusWRg7kCPJHr_o=

**Success Response Example**

```json
{
  "class": [
    "leave-matchmaking"
  ],
  "properties": {
    "message": "User world left matchmaking queue"
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/matchmaking/48/exit"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failures**

- Invalid User
- Match not found
- Authorization token is invalid

#

# Matchmaking Status

    Get the status of a matchmaking.

**Endpoint:** `/api/games/matchmaking/{mid}/status`

**Method:** GET

**Parameters:**

- `mid` (integer, path) - The ID of the matchmaking.
- e.g: {mid} = 48

**Authorization Bearer token Example:**
zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

**Response Example**

```json
{
  "class": [
    "matchmaking-status"
  ],
  "properties": {
    "mid": 47,
    "uid": 133,
    "gid": 51,
    "state": "MATCHED",
    "variant": "STANDARD",
    "created": "2023-12-09T23:10:55Z",
    "pollingTimeOut": 3000
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/matchmaking/47/status"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failures**

- Invalid User
- Match not found
- Authorization token is invalid

#

# Get All Variants

    Get all the variants of the game.

**Endpoint:** `/api/games/variants`

**Method:** GET

**Success Response Example**

```json
{
  "class": [
    "variant-list"
  ],
  "properties": {
    "variants": [
      {
        "name": "STANDARD",
        "boardDim": 15,
        "playRule": "STANDARD",
        "openingRule": "STANDARD",
        "points": 110
      },
      {
        "name": "SWAP",
        "boardDim": 15,
        "playRule": "STANDARD",
        "openingRule": "SWAP",
        "points": 140
      },
      {
        "name": "RENJU",
        "boardDim": 15,
        "playRule": "THREE_AND_THREE",
        "openingRule": "STANDARD",
        "points": 150
      },
      {
        "name": "CARO",
        "boardDim": 15,
        "playRule": "STANDARD",
        "openingRule": "STANDARD",
        "points": 120
      },
      {
        "name": "PENTE",
        "boardDim": 19,
        "playRule": "STANDARD",
        "openingRule": "STANDARD",
        "points": 130
      },
      {
        "name": "OMOK",
        "boardDim": 19,
        "playRule": "THREE_AND_THREE",
        "openingRule": "STANDARD",
        "points": 170
      },
      {
        "name": "NINUKI_RENJU",
        "boardDim": 15,
        "playRule": "THREE_AND_THREE",
        "openingRule": "STANDARD",
        "points": 160
      }
    ]
  },
  "links": [
    {
      "rel": [
        "self"
      ],
      "href": "/api/games/variants"
    }
  ],
  "recipeLinks": [],
  "entities": [],
  "actions": [],
  "requireAuth": [
    true
  ]
}
```

**Failures**

- Variants not found
- Authorization token is invalid
