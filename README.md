# CS494 - Lab01: SOCKET PROGRAMMING

# I. Demo
<img src="https://github.com/DuongHoangHuy/CS494_Lab01_Socket_Programming/blob/main/CS494%20-%20Lab01%20SOCKET%20PROGRAMMING%20ab20a969bc78425084face53778e096c/gif-2.gif" >

- Link youtube demo: https://www.youtube.com/watch?v=JEtrX2Wu-mY&t=156s&ab_channel=E.Galois
# II.	Game story & Packet transfer flow

- In our game system, client and server let the other know what to do totally based on the event messages. Our **event handler** is just a function that gets the **“event”** in the Json string and the **if/else** system to classify what is the next instruction.
- We divide this lab’s game story into 3 main phases:

## 1. Start game phase

- The number of players (MAX_PLAYER) and the length of the race (MAX_POINT) are predefined in the GameManager.

<p align="center">
  <img src="https://github.com/DuongHoangHuy/CS494_Lab01_Socket_Programming/raw/main/CS494%20-%20Lab01%20SOCKET%20PROGRAMMING%20ab20a969bc78425084face53778e096c/Untitled.png" alt="Image" width="500">
</p>

*Register*

- tcpServer is open and listening to any requests from tcpClient.
- tcpClient connects to tcpServer right after being initialized.
- The status of registration is firstly checked by the regex, then based on the response from the tcpServer.
- If users successfully register, they will move to the waiting room.

*Waiting*

- tcpClient is listening in the waiting room.
- When tcpServer gets enough registrations, it will set the gameRoom to be full.
- Immediately, gameManager realizes that and sends a request to the client to start the game.

## 2. In game phase

- An automatic mechanism is established for the game system in this phase, which contains 3 sub-phases:.

<p align="center">
  <img src="https://github.com/DuongHoangHuy/CS494_Lab01_Socket_Programming/raw/main/CS494%20-%20Lab01%20SOCKET%20PROGRAMMING%20ab20a969bc78425084face53778e096c/Untitled%201.png" alt="Image" width="500">
</p>

- tcpClient will send answer data for tcpServer to import into the game system.
- gameManager will process those data and send the request to the tcpClient and view in the UI screen.
- The whole game play is implemented correctly and almost the same as the game story except the **UI for race.** However, we have a leaderboard and the notification table to show the ranking instead.
- Every player’s starting points are set to 0. The mechanism of the verification system:
    - The incorrect/ overtime answer: -1 point
    - The correct answer: +1 point
    - The fastest answer: +(total incorrect answers)

## 3. Game ended phases

<p align="center">
  <img src="https://github.com/DuongHoangHuy/CS494_Lab01_Socket_Programming/raw/main/CS494%20-%20Lab01%20SOCKET%20PROGRAMMING%20ab20a969bc78425084face53778e096c/Untitled%202.png" alt="Image" width="500">
</p>
- The game will end only when there is a player who wins the game (greater or equal the length of the race) or all the players are eliminated.
- Right after the ending round, gameManager will send the signal (packet) to the tcpClient to stop the game and show the end game UI screen. Player can replay the game by clicking on the replay button, then the player will go back to the start game phase.

# III.	Implementation

## 1. Main components

- At the server side, we decided to design two main components. Each of them will run separately in different threads to handle separate tasks:
    - **TCPServer**: Control the socket connection and handle all types of requests between server and client.
    - **GameManager**: Control the game flow and process the logic of the game.
- Besides that, there are some other classes to support the game such as: **GameRoom**, **GameExpression**, **Player**.
- At the client side, TCPClient acts as the interface to communicate with the server.

## 2. Handle requests and responses

- All the messages are based on the **Json** and converted to **String,**  before transferring to the other side.
- The mechanism of sending requests and receiving responses is quite same in both client and server which based on the **read** and **write** the **ByteBuffer:**

```java
public void send(JSONObject reqJson) throws IOException {
		ByteBuffer reqBuffer = ByteBuffer.allocate(1024);
		reqBuffer.put(reqJson.toString().getBytes());
		reqBuffer.flip();
		this.client.write(reqBuffer);
   }
  
   public JSONObject receive() throws IOException {
	    ByteBuffer resBuffer = ByteBuffer.allocate(1024);
	    int bytesCount = client.read(resBuffer);
	    if (bytesCount > 0) {
	    	resBuffer.flip();
	    	return new JSONObject (new String(resBuffer.array()));
	    }	
	    return null;
  }
```

## 3. Non-blocking socket

- Our team has researched and decided to use the **Selector** of the **java.nio** to control multiple requests from clients.
- The Selector lets a single thread be allowed to check all events on multiple channels, so the selector can check if a certain channel is available for reading and writing data without blocking other channels.
- The TCPServer can realize and handle multiple type of requests of the TCPClient through the **status of the key**:

*TCPClient requests for a connection:*

```java

if (key.isAcceptable()) { // Client's request is ready
   SocketChannel client = this.serverSocketChannel.accept();
   client.configureBlocking(false); // Non-blocking
   SelectionKey selectionKey = client.register(selector, SelectionKey.OP_READ);
   Player player = new Player(selectionKey, null, 0);
   this.gameRoom.addPlayer(selectionKey, player);
}
```

*TCPClient’s game requests:*

```java
if (key.isReadable()) { // Handle all request
   String req = gameRoom.hashmapPlayers.get(key).read();
   if(req != null) {
   	JSONObject reqJson = new JSONObject(req);
   	String eventType = reqJson.getString("event");
   	if(eventType != null) { // Valid request
   		if(eventType.equals("SERVER_REGISTER")) {
           	this.logger.info(eventType);
   			this.handleRegister(key, reqJson);
   		} else if(eventType.equals("SERVER_CLOSE")) {
   			this.handleClose(key);
   		} else if(eventType.equals("SERVER_ANSWER")) {
   			this.handleAnswer(key, reqJson);
   		}
   	}
   } else {
   	JSONObject resObj = new JSONObject();
   	resObj.put("error_msg", "Invalid request");
   	this.gameRoom.hashmapPlayers.get(key).write(resObj.toString());
   }
}
```

# IV.	References

[Java Network Tutorial - Java Non-Blocking Socket (java2s.com)](http://www.java2s.com/Tutorials/Java/Java_Network/0070__Java_Network_Non-Blocking_Socket.htm)

[Blocking IO và Non Blocking IO Client Server Socket (viblo.asia)](https://viblo.asia/p/blocking-io-va-non-blocking-io-client-server-socket-1VgZvX415Aw)

[How to Create a Non-Blocking Server in Java? - GeeksforGeeks](https://www.geeksforgeeks.org/how-to-create-a-non-blocking-server-in-java/)
