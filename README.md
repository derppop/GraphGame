# CS 441 HW 3 - Graph Game

### Joseph David - jdavid31@uic.edu

## Functionality
Graph Game utilizes [netgamesim](https://github.com/0x1DOCD00D/NetGameSim) for graph generation and perturbation. 
Once the server is hosted the api will listen for requests on port 8080.
Depending on the request, different endpoints will be responsible for handling
certain functionality. The first is the 'join' endpoint that works on a post request, when a user makes a 
post request on the join endpoint with a role query parameter the server will generate and perturb a graph if needed, 
then assign that player's role a specific node. The state endpoint accepts a get request and also the role query parameter,
when a user makes a get request on the state endpoint the server will respond with that player's position and all nodes adjacent to their node.
The third endpoint is move, when a user sends a post request to the move endpoint with the role and destination query parameters
the server will move the player to the node if valid, and calculate win/loss conditions in the case that the move causes the game to end.
The last endpoint is restart, when a user hits the restart endpoint with a post request and no parameters, all the game's state
values will be reset to their initial values, allowing the game to be played again on the same graph. There is a maximum of two players,
one cop and one thief


## Usage and Deployment
Use the following command to produce the project jar.
 ````bash
 sbt assembly
 ````
Once the jar is built, and transferred to an EC2 AWS instance that opened TCP on port 8080, it can be run with the following command within the instance
````
java -jar graph-game.jar
````

Scala 3.2.2, openjdk 1.8, Akka 2.8.0, and AkkaHTTP 10.5.0 were used to develop and test this project

## Configuration
The project configuration variables can be found in [application.conf](src/main/resources/application.conf) and are inherited from [netgamesim](https://github.com/0x1DOCD00D/NetGameSim)

# API
## join
POST request, generates and perturbs a graph if there isn't already one and assigns the role specified via query parameter
to a random node in the graph, if the chosen node is flawed in any way (placed players at the same node or causes someone to win/lose instantly)
the node will be chosen again,then the resulting node's id and all of its adjacent nodes will be returned as a result as a [GameStateResponse](/src/main/scala/model/ResponseFormats.scala) object 
### Query Parameters: 
- **role** - specifies the desired role within the game, valid values are either 'cop' or 'thief'  

## state
GET request, gets the player's current node and all adjacent nodes that are available to that player based on the role query parameter
### Query Parameters:
- **role** - specifies the desired role within the game, valid values are either 'cop' or 'thief'

## move
POST request, moves the player to the node specified in the destination query parameter if the move is valid,
if the move causes the player to win/lost the server will send the announcement as a response and set the game's state
to end, any further request after the game ends will get a response saying the game has ended until the restart function is called
### Query Parameters:
- **role** - specifies the desired role within the game, valid values are either 'cop' or 'thief'
- **destination** - specifies the desired node's id you wish to move to that is adjacent to your node

## restart
POST request, sets all the games state values back to their initial values, allowing the game to be played again
after a winner is chosen, the only thing that stays the same is the graph as it is not regenerated when the game restarts.

[Demo](https://youtu.be/mOnzSUxiwiA)