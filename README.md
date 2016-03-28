# Game-Server Project
Project about network communication between a game server and multiple game clients. Written in Java.

## Server
Server assigns each client a unique id on connection  
Server runs on port 9898

## Client
Client has a unique Id, assigned by the server.  
Client chooses a name. The name has to be unique, for the purpose of recognition. The server will validate this.

## Protocol

### Server/Client Communication Protocol
**Connect** - 000|brugernavn  
**Leave game** - 001|modstanderId  
**Won** - 002|  
**Lost** - 003|  
**Turn** - 004|body  
**Invite** - 005|modtagerId,spiltype,afsenderId  
**List connected players** - 006|id.navn,id.navn  
**Username taken** - 007|  
**Connection succesful** - 008|id,navn  
**accept invite** - 009|invitationsAfsenderId,invitationsModtagerId,spilType  
**deny invite** - 010|invitationsAfsenderId  
**game state** - 011|body
**tie** - 012|  

### Game Protocol
**Game state** - 011|body  
Sent after each step made by a client, to all recipients, including the client that made the step. The body is dependent on the played game.  
*Tic Tac Toe example*:  
011|playerIDturn,position0.position1.position2.position3.position4.position5.position6.position7.position8

**Træk** - 004|body
Client sends this code, every time he/she takes a turn. The body is dependent on the played game.
