# Server 

**Description** : Local server of a gomoku game 
**URL**: http://localhost:8080



# Home

**Description**: HTTP API requests for all types of users, authenticated or not, to retrieve system information and statistic information.

### Get System Info
Retrieve information about the Gomoku game system.

**Endpoint:** `/api/system`

 **Method:** GET

**Response Example:**

```json
	{

		"systemInfo": "Gomoku Game",

		"systemAuthors": "Diogo Guerra, Gonçalo Frutuoso, Daniel Carvalho",

		"systemVersion": "0.1.0"

	}
```
### Get Home Page (Unauthenticated User)

Retrieve the home page for unauthenticated users.

**Endpoint:** `/api` 

**Method:** GET

**Response Example:**
```json
{
	"message": "Welcome to Gomoku! Please log in to play."
}
```

# Users

**Description**: HTTP API requests to create authenticated users and perform others functionalities for the same type of users

### Create User

Create a new user to be able to play Gomoku.

**Endpoint:** `/api/users`

 **Method:** POST

- **Success**	
	- **First Example**:
	**Request Example:**
		```json
		{

			"email": "backfire@gmail.com",

			"username": "backfire",

			"password": "Abacat345"

		}
		```
	
		**Header location Example**: 
	/api/users/6
	- **Second Example**:
	 **Request Example:**
		```json
		{
			"email": "deluxe@gmail.com",

			"username": "deluxe",

			"password": "Inthemorning123"
		}
		```
		**Header location Example**: 
	/api/users/7

- **Failure**
		- Insecure password
		- User already exists
		- Insecure email

### Create Token

Create an authentication token for a user.

**Endpoint:** `/api/users/token`

 **Method:** POST

- **Success**
	- **First Example**:
		**Request Example:**
		```json
		{

			"username": "backfire",

			"password": "Abacat345"

		}
		```

		**Response Example**: 
		```json
		{

			"token": "-SRA222bPYTSs8yohzTG3Tq24IJ43NgBHIBEXxj948k="

		}
		```

	- **Second Example**:
	**Request Example:**
		```json
		{

			"username": "deluxe",

			"password": "Inthemorning123"

		}
		```

		**Response Example**:  
		```json
		{

			"token": "zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s="

		}
		```
- **Failure**
	- User or password are invalid 

### Logout 
Log out a user and invalidate their token.

**Endpoint:** `/api/users/logout` 

**Method:** POST

**Authorization Bearer token Example:**
zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

### Get User by ID

Retrieve user information by their ID.

**Endpoint:** `/api/users/{userId}` 

**Method:** GET

**Parameters:**
-   `userId` (integer, path) - The ID of the user 
e.g: {userId} = 4.

- **Success**
**Response Example:**
	```json
	{

		"id": 7,

		"email": "deluxe@gmail.com",

		"username": "deluxe",

	}
	```
- **Failure**
	- User does not exist
	- Invalid token
	- Token expired
	-  User is not authenticated

#### Get Home Page with User Login

Retrieve the home page with user login for authenticated users.

**Endpoint:** `/api/me` 

**Method:** GET

**Authorization Bearer token Example:**
zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

**Response Example:**
```json
{

	"id": 7,

	"username": "deluxe"

}
```

#### Get Stats Page of User by Id

Retrieve the stats page of a user by his Id.

**Endpoint:** `/api/stats/{userId}` 

**Method:** GET

**Parameter** 
-   `userId` (integer, path) - The ID of the user 
e.g: {userId} = 7.

**Authorization Bearer token Example:**
zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

**Response Example:**
```json
{

	"id": 7,

	"username": "deluxe",

	"gamesPlayed": 0,

	"wins": 0,

	"losses": 0,

	"rank": 0,

	"points": 0

}
```

#### Get Ranking Info Page of all Users

Retrieve the ranking page of all users.

**Endpoint:** `/api/ranking` 

**Method:** GET

**Response Example:**
```json
{
	"rankingTable": [

{

	"rank": 1,

	"username": "jcbackfire",

	"points": 48

	},

	{

	"rank": 2,

	"username": "mrdeluxe",

	"points": 12

	},

	{

	"rank": 3,

	"username": "alice",

	"points": 0

	},

	{

	"rank": 4,

	"username": "bob",

	"points": 0

	},

	{

	"rank": 5,

	"username": "charlie",

	"points": 0

	}

	]
}
```

# Game

### Create Game 

Create a gomoku game between two users.

**Endpoint:** `/api/games` 

**Method:** POST

**Authorization Bearer token Example:**
zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

**Request Example:**
```json
{	
	"userBlack": 6,

	"userWhite": 7,

	"variant": "STANDARD"

}
```

**Success**:
**Header location example:**
/api/games/2

**Failure**
- Game already exists
- User does not exists
- Variant does not exists

### Get Game Info by Id

Retrieve information about a specific Gomoku game by its ID.

**Endpoint:** `/api/games/{gameId}` 

**Method:** GET

**Parameters:**
-   `gameId` (integer, path) - The ID of the game. = 2

**Authorization Bearer token Example:**
zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=
**Success**:
**Response Example:**
```json
{

	"game": {

		"id": 2,

		"board": {

			"moves": {},

			"turn": "BLACK",

			"variant": "STANDARD",

			"maxMoves": 225

		},

		"userBlack": {

			"id": {

				"value": 6

			},

			"username": "backfire",

				"email": {

				"value": "backfire@gmail.com"

			},

			"passwordValidation": {

				"validationInfo": "$2a$10$TZ1h3WMi4fhixpn3haexIu9vbx.1OH2k7l5QkwkLaSttEZrwRQBNS"

			}

		},

		"userWhite": {

			"id": {

				"value": 7

			},

				"username": "deluxe",

				"email": {

				"value": "deluxe@gmail.com"

			},

			"passwordValidation": {

				"validationInfo": "$2a$10$BU6.Qjm8DkrvbnNKxG34KOdT.wsKSECfiId3lyCfaCUwHRECrZmG."

			}

		}
	}

}
```

**Failure**
- Game does not exist

### Play a Round

Make a move in the Gomoku game.

**Endpoint:** `/api/games/{gameId}/play` 

**Method:** PUT

**Parameters:**

-   `gameId` (integer, path) - The ID of the game.
e.g: {gameId}: 2

- **Success**
	- **First Request Example**
		**Request example**
		```json
		{

				"userId": 6,

				"column": 3,

				"row": 3

		}
		```
	
		**Authorization Bearer token Example:**
		-SRA222bPYTSs8yohzTG3Tq24IJ43NgBHIBEXxj948k=

		**Response example**
		```json
		{
			"game": {

			"id": 2,

			"board": {

			"moves": {

			"3D": "BLACK"

			},

			"turn": "WHITE",

			"variant": "STANDARD",

			"maxMoves": 225

			},

			"userBlack": {

			"id": {

			"value": 6

			},

			"username": "backfire",

			"email": {

			"value": "backfire@gmail.com"

			},

			"passwordValidation": {

			"validationInfo": "$2a$10$TZ1h3WMi4fhixpn3haexIu9vbx.1OH2k7l5QkwkLaSttEZrwRQBNS"

			}

			},

			"userWhite": {

			"id": {

			"value": 7

			},

			"username": "deluxe",

			"email": {

			"value": "deluxe@gmail.com"

			},

			"passwordValidation": {

			"validationInfo": "$2a$10$BU6.Qjm8DkrvbnNKxG34KOdT.wsKSECfiId3lyCfaCUwHRECrZmG."

			}

			}

			},

			"state": "NEXT_PLAYER_WHITE"
		}
		```
	- **First Request Example**
		**Request example**
		```json
		{

				"userId": 7,

				"column": 2,

				"row": 1

		}
		```
	
		**Authorization Bearer token Example:**
		zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

		**Response example**
		```json
		{
			"game": {

			"id": 2,

			"board": {

			"moves": {

			"2B": "WHITE"

			},

			"turn": "BLACK",

			"variant": "STANDARD",

			"maxMoves": 225

			},

			"userBlack": {

			"id": {

			"value": 6

			},

			"username": "backfire",

			"email": {

			"value": "backfire@gmail.com"

			},

			"passwordValidation": {

			"validationInfo": "$2a$10$TZ1h3WMi4fhixpn3haexIu9vbx.1OH2k7l5QkwkLaSttEZrwRQBNS"

			}

			},

			"userWhite": {

			"id": {

			"value": 7

			},

			"username": "deluxe",

			"email": {

			"value": "deluxe@gmail.com"

			},

			"passwordValidation": {

			"validationInfo": "$2a$10$BU6.Qjm8DkrvbnNKxG34KOdT.wsKSECfiId3lyCfaCUwHRECrZmG."

			}

			}

			},

			"state": "NEXT_PLAYER_BLACK"
		}
		```
		
- **Failures**
	- Game does not exist
	- Invalid User
	- Invalid State
	- Invalid Time
	- Invalid Turn
	- Invalid Position


### Get All Games By User
Get all games by a user Id.

**Endpoint:** `/api/games/user` 

**Method:** GET

- **Success**
	
	**Authorization Bearer token Example:**
		zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=

	**Response example**
		
	```json
		{
			"games": [

			{

			"id": 2,

			"board": {

			"moves": {

			"3D": "BLACK"

			},

			"turn": "WHITE",

			"variant": "STANDARD",

			"maxMoves": 225

			},

			"userBlack": {

			"id": {

			"value": 6

			},

			"username": "backfire",

			"email": {

			"value": "backfire@gmail.com"

			},

			"passwordValidation": {

			"validationInfo": "$2a$10$TZ1h3WMi4fhixpn3haexIu9vbx.1OH2k7l5QkwkLaSttEZrwRQBNS"

			}

			},

			"userWhite": {

			"id": {

			"value": 7

			},

			"username": "deluxe",

			"email": {

			"value": "deluxe@gmail.com"

			},

			"passwordValidation": {

			"validationInfo": "$2a$10$BU6.Qjm8DkrvbnNKxG34KOdT.wsKSECfiId3lyCfaCUwHRECrZmG."

			}

			}

			}

			]
		}
	```
		
- **Failures**
	- User does not exists

### Get All Games 
Get all gomoku games.

**Endpoint:** `/api/games` 

**Method:** GET

- **Success**
		**Response example**
	```json
		{
			"game": {

			"id": 2,

			"board": {

			"moves": {

			"3D": "BLACK"

			},

			"turn": "WHITE",

			"variant": "STANDARD",

			"maxMoves": 225

			},

			"userBlack": {

			"id": {

			"value": 6

			},

			"username": "backfire",

			"email": {

			"value": "backfire@gmail.com"

			},

			"passwordValidation": {

			"validationInfo": "$2a$10$TZ1h3WMi4fhixpn3haexIu9vbx.1OH2k7l5QkwkLaSttEZrwRQBNS"

			}

			},

			"userWhite": {

			"id": {

			"value": 7

			},

			"username": "deluxe",

			"email": {

			"value": "deluxe@gmail.com"

			},

			"passwordValidation": {

			"validationInfo": "$2a$10$BU6.Qjm8DkrvbnNKxG34KOdT.wsKSECfiId3lyCfaCUwHRECrZmG."

			}

			}

			},

			"state": "NEXT_PLAYER_WHITE"
		}
	```

### Leave Game
Leave the game by it's Id .

**Endpoint:** `/api/games/{gameId}/leave` 

**Method:** PUT

**Authorization Bearer token Example:**
		zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=



### Matchmaking
Enter on a matchmaking to enter a game.

**Endpoint:** `/api/games/matchmaking` 

**Method:** POST

**Authorization Bearer token Example:**
		zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=
		
**Request Example**
	```json
	{
	}
	```

### Exit Matchmaking
Exit a matchmaking.

**Endpoint:** `/api/games/matchmaking/exit` 

**Method:** DEL

**Authorization Bearer token Example:**
		zLNjh8mfRHzqCYsw0S8EXkTVmdLrczsNKvO6qnYoe8s=