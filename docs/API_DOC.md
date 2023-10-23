# Server 

**Description** : Local server of a gomoku game 
**URL**: http://localhost:8080



# Unauthenticated Users

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

### Get Statistic Info

Retrieve the statistic information for all Gomoku players.

**Endpoint:** `/api/statistic`
 **Method:** GET

**Response Example:**:

```json
{

	"username": "Mario Matos",

	"userRank": "Bronze",

	"wins": 3,

	"losses": 8,

	"nofPlayedGames": 8

}
```

### Get Home Page (Unauthenticated User)

Retrieve the home page for unauthenticated users.

**Endpoint:** `/api/home` 
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
			"email": "jcbackfire@gmail.com",

			"username": "jcbackfire",

			"password": "Abacate345"
		}
		```
	
		**Header location Example**: 
	/api/users/20
	- **Second Example**:
	 **Request Example:**
		```json
		{
			"email": "mrdeluxe@gmail.com",

			"username": "mrdeluxe",

			"password": "Topinthemorning123"

		}
		```
		**Header location Example**: 
	/api/users/21

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

			"username": "jcbackfire",

			"password": "Abacate345"

		}
		```

		**Response Example**: 
		```json
		{

			"token": "BjnKFNheLckuGFfEfoitK6ab0MFSIa3UTgBTQNu8iJU="

		}
		```

	- **Second Example**:
	**Request Example:**
		```json
		{

			"username": "mrdeluxe",

			"password": "Topinthemorning123"

		}
		```

		**Response Example**:  
		```json
		{

			"token": "G4KinKS2Lg5hcxY_077Xqv5xsYQgmElo3sh2f_ZKT3I="

		}
		```
- **Failure**
	- User or password are invalid 

### Logout 
Log out a user and invalidate their token.

**Endpoint:** `/api/users/logout` 
**Method:** POST

**Authorization Bearer token Example:**
G4KinKS2Lg5hcxY_077Xqv5xsYQgmElo3sh2f_ZKT3I=

### Get User by ID

Retrieve user information by their ID.

**Endpoint:** `/api/users/{userId}` 
**Method:** GET

**Parameters:**

-   `userId` (integer, path) - The ID of the user 
e.g: {userId} = 21.
- **Success**
**Response Example:**
	```json
	{

		"id": 23,

		"email": "mrdeluxe@gmail.com",

		"username": "mrdeluxe",

	}
	```
- **Failure**
	- User does not exist
	- Invalid token
	- Token expired
	-  User is not authenticated

#### Get Home Page with User Login

Retrieve the home page with user login for authenticated users.

**Endpoint:** `/api/users/home` 
**Method:** GET

**Authorization Bearer token Example:**
BjnKFNheLckuGFfEfoitK6ab0MFSIa3UTgBTQNu8iJU=

**Response Example:**
```json
{

	"id": 22,

	"username": "jcbackfire"

}
```

# Game

### Get Game Info by ID

Retrieve information about a specific Gomoku game by its ID.

**Endpoint:** `/api/games/{gameId}` **Method:** GET

**Parameters:**

-   `gameId` (integer, path) - The ID of the game.

**Success**:
**Response Example:**
	```json
	{

		"userBlack": 20,

		"userWhite": 21,

		"variant": "STANDARD"

	}
	```

**Header location example:**
/api/games/fc72a831-eec5-45da-a77a-b7b39b5ba8d4

**Failure**
- Game does not exist

### Play a Round

Make a move in the Gomoku game.

**Endpoint:** `/api/games/{gameId}/play` 
**Method:** PUT

**Parameters:**

-   `gameId` (integer, path) - The ID of the game.
e.g: {gameId}: fc72a831-eec5-45da-a77a-b7b39b5ba8d4


