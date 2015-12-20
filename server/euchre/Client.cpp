#include <iostream>
#include <string>
#include <thread>

#include "socket.h"

int main (int argc, char **argv)
{

	const std::string ip("127.0.0.1");
	std::string userInput="once";
	std::string resp;
	unsigned int port(2000);
  	Communication::ByteArray response;
	
	Communication::Socket mySocket(ip,port);
	mySocket.Open();
  	//std::thread newthread();
	mySocket.Read(response);
	resp=response.ToString();
	std::cout<<resp<<std::endl;
	while(userInput!="done")
	{
		std::cout<<"Please enter a message: ";
		std::getline(std::cin, userInput);
		std::cout<<std::endl;
		mySocket.Write(userInput);

		if(mySocket.Read(response)<=0)
		{
			std::cout<<"Its not your turn"<<std::endl; 

		}
		resp=response.ToString();
		std::cout<<resp<<std::endl;
	}
	mySocket.Close();
	return 0; 
}
