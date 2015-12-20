Client: Client.o socket.o Blockable.o
	g++ -o Client Client.o socket.o Blockable.o -pthread -l rt

Client.o : Client.cpp socket.h
	g++ -c Client.cpp -std=c++11

Server : Server.o thread.o socket.o socketserver.o Blockable.o
	g++ -o Server Server.o thread.o socket.o socketserver.o Blockable.o -pthread -l rt

Server.o : Server.cpp socket.h socketserver.h thread.h
	g++ -c Server.cpp -std=c++11

thread.o : thread.cpp thread.h Blockable.h
	g++ -c thread.cpp -std=c++11

socket.o : socket.cpp socket.h
	g++ -c socket.cpp -std=c++11

socketserver.o : socketserver.cpp socket.h socketserver.h
	g++ -c socketserver.cpp

Blockable.o : Blockable.cpp Blockable.h
	g++ -c Blockable.cpp
