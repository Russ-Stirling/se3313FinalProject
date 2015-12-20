#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <chrono>
#include <thread>
#include <ctime>
#include <cstdlib>

#include "thread.h"
#include "socketserver.h"
#include "socket.h"
#include "Blockable.h"

using namespace Communication;

class Card
{
	public:
		int value;
		std::string suit;
		std::string name;
		bool trump;
	public: Card(int v, std::string s){
		value=v;
		suit=s;
		name = s;
		name+=std::to_string(v);
	}
	public: int getValue(){return value;}
	public: std::string getSuit(){return suit;}
	public: std::string getName(){return name;}
};

class monitorThread: public Thread
{
	private: SocketServer* mySocketServer;
	private: bool* exit;
	public: monitorThread(SocketServer*k, bool*e)
	:Thread()
	{
		mySocketServer=k;
		exit=e;
	}
	
	virtual long ThreadMain()
	{
		std::string command;
		std::cout<<std::endl<<"Enter (kill) to close the server"<<std::endl;
		bool condition=true;
		while(condition)
		{
			std::getline(std::cin,command);
			if(command=="kill"){
				*exit=false;
				condition=false;
			}
		}
		mySocketServer->Shutdown();
		return 0;
	}
};

class myThread: public Thread
{
	private: std::vector<Socket*> mySocket;
	private: bool* exit;
	private: std::vector<Card*> cards;
	public: myThread(std::vector<Socket*> s, bool*e)
	:Thread()
	{
		for(Socket* i : s){
			mySocket.push_back(i);
		}

		exit=e;
		for(int i=9; i<15; i++){
			Card *newCard = new Card(i,"H");
			cards.push_back(newCard);
		}
		for(int i=9; i<15; i++){
			Card *newCard = new Card(i,"D");
			cards.push_back(newCard);
		}
		for(int i=9; i<15; i++){
			Card *newCard = new Card(i,"C");
			cards.push_back(newCard);
		}
		for(int i=9; i<15; i++){
			Card *newCard = new Card(i,"S");
			cards.push_back(newCard);
		}
	}

	public: ~myThread()
	{
		//std::cout<<"In the thread destructor 2.0"<<std::endl;
		std::this_thread::sleep_for(std::chrono::milliseconds(500));//just to ensure the thread finds a return before terminating
		for(Card* i : cards){
			delete i;
		}
	}
	public: void closeSocket()
	{
		ByteArray userInput;
		for(Socket* i : mySocket){
			std::this_thread::sleep_for(std::chrono::milliseconds(500));
			if(i->Read(userInput)!=0)
			{
				i->Close();
			}
		}
		//send close code to all users
		//close the sockets
	}
	
	public: bool checkKill(std::string r)
	{
		if(r=="kill")//checks for kill signal from client
		{
			std::string iDied="11,";
			for (int i=0; i<4; i++)
			{
				mySocket.at(i)->Write(iDied);
			}

			closeSocket();

			return true;
		}

		else
		{
			return false;
		}
	}
	
	public: int calculateValue(std::string c)
	{
		if(c.length()==3)//bit of a workaround for getting card values from strings that are a combo of one letter and 1-2 numbers
		{
			std::string temp = {c[1],c[2]};
			return std::atoi(temp.c_str());
		}
		else if(c.length()==2)
		{
			std::string temp = {c[1]};
			return std::atoi(temp.c_str());
		}
		else
		{
			return 0;
		}
	}
	public: std::string winningCard(std::string c1, std::string c2, std::string trump, std::string miniT)
	{
		std::string jackT;

		if(trump=="H"){jackT="D";}
		else if(trump=="D"){jackT="H";}
		else if(trump=="C"){jackT="S";}
		else{jackT="C";}

		std::string suit1={c1[0]};
		std::string suit2={c2[0]};

		int value1=calculateValue(c1);
		int value2=calculateValue(c2);

		if(suit1==trump){
			value1+=50;
			if(value1==61){
				value1+=10;
			}
		}
		else if((suit1==jackT)&&(value1==11))
		{
			value1+=55;
		}
		else if(suit1==miniT){
			value1+=10;
		}
		
		if(suit2==trump){
			value2+=50;
			if(value2==61){
				value2+=10;
			}
		}
		else if((suit2==jackT)&&(value2==11))
		{
			value2+=55;
		}
		else if(suit2==miniT){
			value2+=10;
		}		
		
		if(value1>value2){
			//std::cout<<c1<<" beats "<<c2<<std::endl;
			return c1;
		}
		else{
			//std::cout<<c2<<" beats "<<c1<<std::endl;
			return c2;
		}
	}

	
	virtual long ThreadMain()
	{
		ByteArray userInput;
		std::string received;
		int team1=0; //players 0 and 2
		int team2=0; //players 1 and 3
		std::string players[4];
		int dealer=3;
		int pStart=0;
		int alive=1;
		int pSetter;

		while((team1<10)&&(team2<10))
		{
			std::string trump="notSet";
			//std::cout<<"trump is: "<<trump<<" at the start of the round"<<std::endl;
			//shuffle the deck
			std::srand(unsigned(std::time(0)));
			
			std::random_shuffle(cards.begin(), cards.end());//std::rand()%24);
			//deal and send cards
			for (int i=0; i<4; i++)
			{
				players[i]="01,";
			}

			for (int i=0; i<20; i++) //order doesnt matter here it will be a random shuffle
			{
				players[0]+=cards.at(i)->getName()+":";
				i++;
				players[1]+=cards.at(i)->getName()+":";
				i++;
				players[2]+=cards.at(i)->getName()+":";
				i++;
				players[3]+=cards.at(i)->getName()+":";
			}

			//ex write 01,H12:D13:H10:C9:C11:,
			for (int i=0; i<4; i++) //order also doesnt matter here just appending the trump face up card
			{
				players[i]+=","+cards.at(20)->getName()+",";
				mySocket.at(i)->Write(players[i]);
			}

			std::this_thread::sleep_for(std::chrono::milliseconds(5000));//to let p1 see cards before moving on to ordering trump
			//ex write 02,
			//for the first round of trump
			int pToGo=dealer;
			for (int i=0; i<4; i++) //order matters here person going should be to the left of dealer first
			{
				pToGo++;
				if(pToGo>3)
				{
					pToGo=0;
				}
				std::string mess="02,";
				mySocket.at(pToGo)->Write(mess);
				alive=mySocket.at(pToGo)->Read(userInput);
				received=userInput.ToString();
				if((checkKill(received))||(alive==0)){return 0;}
				
				if(received=="1")
				{
					//mark who set the suit here, later
					pSetter=pToGo;
					for (int j=0; j<4; j++)
						{
							players[j]="07,"+std::to_string(dealer)+","+cards.at(20)->getSuit()+",";
							mySocket.at(j)->Write(players[j]);
						}
					trump=cards.at(20)->getSuit();
					//std::cout<<"Trump is: "<<trump<<std::endl;
					i=5;

				}
				else
				{
					mess="06,";
					mySocket.at(pToGo)->Write(mess);
					trump="notSet";
				}
			}

			std::this_thread::sleep_for(std::chrono::milliseconds(100));
			
			while(trump=="notSet")//same order as above
			{
				pToGo=dealer;
				for(int i=0; i<4; i++)
				{
					pToGo++;
					if(pToGo>3){
						pToGo=0;
					}
					std::string mess="03,"+cards.at(20)->getSuit()+",";
					//ex: 03,H,

					mySocket.at(pToGo)->Write(mess);
					alive=mySocket.at(pToGo)->Read(userInput);
					received=userInput.ToString();
					if((checkKill(received))||(alive==0)){return 0;}
					if(received=="0")
					{
						mess="06,";
						mySocket.at(pToGo)->Write(mess);
					}
					else
					{
						pSetter=pToGo;						
						for (int j=0; j<4; j++)
							{
								//code num,player who set trump num, trump letter 08,H,
								players[j]="08,"+received+",";
								mySocket.at(j)->Write(players[j]);
							}
						trump=received;
						//std::cout<<"The trump is: "<<trump<<std::endl;
						i=5;
					}
				}
			}


			//check if state has been set, if not do a request state 03
			//if first 3 dont dealer must set their own trump
			//log who, as in which team, set the trump and what it is

			std::this_thread::sleep_for(std::chrono::milliseconds(500));

			//play first hand starting at player 1 
			//play hands 2-5 starting with winner of hand before
			int trick1=0;
			int trick2=0;
			int currentWinner=dealer+1;
			pStart;
			if(currentWinner>3)
			{
				currentWinner=0;
			}
			for (int i=0; i<5; i++)
			{
				std::string played="04,";
				std::string waiting="06,";
				std::string clear="10,";
				std::string send;
				std::string currentWin="notSet";
				std::string miniT="notSet";
				pStart=currentWinner;
				for(int j=0; j<4; j++)//order matters here, player left of dealer 1st round then last winner 2nd-5th round;
				{
					mySocket.at(pStart)->Write(played);

					alive=mySocket.at(pStart)->Read(userInput);

					received=userInput.ToString();
					if((checkKill(received))||(alive==0)){return 0;}
					if(j==0)
					{
						currentWin=received;
						currentWinner=pStart;
						miniT={received[0]};
					}
					else
					{
						//std::cout<<"Trump is : "<<trump<<std::endl<<"MiniT is : "<<miniT<<std::endl;
						currentWin=winningCard(currentWin, received, trump, miniT);
						//std::cout<<"The current winning card is : "<<currentWin<<std::endl;
						if(currentWin==received){
							currentWinner=pStart;
							//std::cout<<"The current winner was changed"<<std::endl;
						}
						//std::cout<<"The current winner is : "<<currentWinner<<std::endl;
					}
					
					send ="09,";
					send+=std::to_string(pStart)+","+received+",";
					pStart++;
					if(pStart>3)
					{
						pStart=0;
					}

					//ex: 09,2,H12,
					//save this somewhere
					std::this_thread::sleep_for(std::chrono::milliseconds(500));
					for (int k=0; k<4; k++)//order doesnt matter here
					{
						mySocket.at(k)->Write(send);
					}
					std::this_thread::sleep_for(std::chrono::milliseconds(500));
				}

				pStart=currentWinner;
				if(currentWinner==0||currentWinner==2){
					trick1++;
				}
				else{
					trick2++;
				}
				//clear board code ex: 10,
				
				for(int b=0; b<4; b++)//order matters here, player left of dealer 1st round then last winner 2nd-5th round;
				{
					mySocket.at(b)->Write(clear);
				}
				std::this_thread::sleep_for(std::chrono::milliseconds(500));
				//calculate hand winner
					//first card suit is best unless trump is played
					//calculate best card
			}

			if(trick1>trick2){
				if((pSetter==1)||(pSetter==3)){
					team1++;
					team1++;
				}
				else if(trick1==5){
					team1++;
					team1++;
				}
				else{
					team1++;
				}
			}
			else{
				if((pSetter==0)||(pSetter==2)){
					team2++;
					team2++;
				}
				else if(trick2==5){
					team2++;
					team2++;
				}
				else{
					team2++;
				}
			}
			//calculate round winner and increment points.
			//assign points to string ex: 05,1,4,2,5,
			std::this_thread::sleep_for(std::chrono::milliseconds(500));
			std::string points="05,"+std::to_string(team1)+","+std::to_string(team2)+",";
			for(int i=0; i<4; i++)//order doesnt matter here
			{
				mySocket.at(i)->Write(points);
			}

			std::this_thread::sleep_for(std::chrono::milliseconds(500));
			dealer++;
			if(dealer>3)
			{
				dealer=0;
			}
		}
		std::this_thread::sleep_for(std::chrono::milliseconds(10000));//let player see winner message for 10s before being disconnected
		std::string finalMess="11,";
		for(int i=0; i<4; i++)//order doesnt matter here
		{
			mySocket.at(i)->Write(finalMess);
		}
		closeSocket();
		return 0;
	}
};

int main (int argc, char **argv)
{
	try{
		int i=2000;
		std::vector<myThread*> myThreads;
		bool exit=true;
		std::string terminate="n";
		std::vector<Socket*> allSockets;
		SocketServer mySocketServer(i);
		ByteArray userInput;
		std::string iDied="11,";
		
		//put monitor thread here
		//put this in a while loop for multiple games
		monitorThread monitor(&mySocketServer, &exit); //dont bother sending 11 codes here

		while(exit)
		{
			std::vector<Socket*> mySocket;
			try{
				for(int i=0; i<4; i++){
				
					Socket *newSocket = new Socket (mySocketServer.Accept());
					mySocket.push_back(newSocket);
					allSockets.push_back(newSocket);

					std::string num="00,"+std::to_string(i)+",";
					mySocket.at(i)->Write(num);
				}
				myThread * newThread = new myThread(mySocket, &exit);
				myThreads.push_back(newThread);
			}
			catch(int e){
				std::cout<<"KILL IT WITH FIRE - Bailey"<<std::endl<<"(The server is being shut down - Russell)"<<std::endl;
				for(Socket* i : mySocket)
				{
					i->Write(iDied);
					std::this_thread::sleep_for(std::chrono::milliseconds(500));
					if(i->Read(userInput)!=0)
					{
						i->Close();
					}
				}
			}
		}

		//send 11 codes
		for(Socket* i : allSockets){
			i->Write(iDied);
			std::this_thread::sleep_for(std::chrono::milliseconds(500));
			if(i->Read(userInput)!=0)
			{
				i->Close();
			}
		}
		std::this_thread::sleep_for(std::chrono::milliseconds(2000));
		for(myThread* i : myThreads){
			std::cout<<"thread dead"<<std::endl;
			delete i;
		}
		std::this_thread::sleep_for(std::chrono::milliseconds(500));
		for(Socket* i : allSockets){
			delete i;
		}
		

	}
	catch (std::string e){
		std::cout<<"The socket hasnt been closed properly"<<std::endl;
	}
	return 0; 
}
