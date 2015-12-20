/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package euchre;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.lang.Thread;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;

//import statements

public class Euchre extends JFrame {

	JLabel jlbHelloWorld;
        JButton connectButton = new JButton("Connect");
        JLabel left = new JLabel("left", JLabel.LEFT);
        JLabel leftText = new JLabel("left", JLabel.LEFT);
        JLabel right = new JLabel("right", JLabel.LEFT);
        JLabel right2 = new JLabel("right", JLabel.LEFT);
        JLabel rightText = new JLabel("right", JLabel.LEFT);
        JLabel across = new JLabel("across", JLabel.LEFT);
        JLabel acrossText = new JLabel("across", JLabel.LEFT);
        JLabel own = new JLabel("own", JLabel.LEFT);
        JLabel ownText = new JLabel("own", JLabel.LEFT);
        JLabel deck = new JLabel("deck", JLabel.LEFT);
        JLabel spacer = new JLabel("spacer", JLabel.LEFT);
        JLabel information = new JLabel("information", JLabel.LEFT);
        JLabel scores = new JLabel("scores", JLabel.LEFT);
        String state = "";
        int playerNumber;
        int PL, PR, PA;
        //int playedCards = 0;
        String faceUpCard = "";
        JButton card1 = new JButton();
        JButton card2 = new JButton();
        JButton card3 = new JButton();
        JButton card4 = new JButton();
        JButton card5 = new JButton();
        public JPanel playPanel = new JPanel();
        public int whoCares = 1;
	private OutputStream socketOutput;
	private InputStream socketInput;
	private Socket theSocket;
        String hand[] = new String[5];
        
        
        public Image setImage(String a){
            Image img = null;
                    try {
                            img = ImageIO.read(new FileInputStream("res/"+a+".png"));
                    } catch (FileNotFoundException ex) {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                    } catch (IOException ex) {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                    }
                    return img;
        }
        public void drawPanel(){
            playPanel = new JPanel();
            playPanel.setBackground(Color.darkGray);
            playPanel.setBounds(100, 100, 600, 100);
            playPanel.setPreferredSize(new Dimension(475,275));
            playPanel.setLayout(null);
            Image cards = setImage("holder");
            playPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            ImageIcon icon = new ImageIcon(cards);

            playPanel.add(information = new JLabel(" Dealer: Player _ "));
            //spacer.setPreferredSize(new Dimension(60,80));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 5;
            information.setOpaque(true);
            information.setBackground(Color.LIGHT_GRAY);
            playPanel.add(information, c);
            
            playPanel.add(spacer = new JLabel("  "));
            spacer.setPreferredSize(new Dimension(60,80));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 4;
            playPanel.add(spacer, c);
            
            playPanel.add(scores = new JLabel("Team 1: 0 | Team 2: 0 "));
            //spacer.setPreferredSize(new Dimension(60,80));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            scores.setOpaque(true);
            scores.setBackground(Color.LIGHT_GRAY);
            playPanel.add(scores, c);
            
            playPanel.add(acrossText = new JLabel("Player "));
            acrossText.setOpaque(true);
            acrossText.setBackground(Color.LIGHT_GRAY);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 10;
            c.gridy = 0;
            playPanel.add(acrossText, c);
            playPanel.add(across = new JLabel(icon));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 10;
            c.gridy = 1;
            playPanel.add(across, c);
            
            playPanel.add(leftText = new JLabel("Player "));
            leftText.setOpaque(true);
            leftText.setBackground(Color.LIGHT_GRAY);
            leftText.setPreferredSize(new Dimension(60,15));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 2;
            playPanel.add(leftText, c);
            playPanel.add(left = new JLabel(icon));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 2;
            playPanel.add(left, c);
            
            playPanel.add(rightText = new JLabel("Player "));
            rightText.setOpaque(true);
            rightText.setBackground(Color.LIGHT_GRAY);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 12;
            c.gridy = 2;
            playPanel.add(rightText, c);
            playPanel.add(right = new JLabel(icon));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 11;
            c.gridy = 2;
            playPanel.add(right, c);
            
            playPanel.add(ownText = new JLabel("Player "));
            ownText.setOpaque(true);
            ownText.setBackground(Color.LIGHT_GRAY);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 10;
            c.gridy = 5;
            playPanel.add(ownText, c);
            playPanel.add(own = new JLabel(icon));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 10;
            c.gridy = 4;
            playPanel.add(own, c);
            
            
            playPanel.add(spacer = new JLabel("  "));
            spacer.setPreferredSize(new Dimension(60,80));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 12;
            c.gridy = 4;
            playPanel.add(spacer, c);
            
            Image deckImage = setImage("deck");
            ImageIcon deckIcon = new ImageIcon(deckImage);
            playPanel.add(deck = new JLabel(deckIcon));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 14;
            c.gridy = 4;
            playPanel.add(deck, c);
            
            playPanel.setLocation(100, 100);
            
            add(playPanel);
        }
        public void setHand(){
            Image c1 = setImage(hand[0]);
            card1.setIcon(new ImageIcon(c1));
            card1.setText(null);
            Image c2 = setImage(hand[1]);
            card2.setIcon(new ImageIcon(c2));
            card2.setText(null);
            Image c3 = setImage(hand[2]);
            card3.setIcon(new ImageIcon(c3));
            card3.setText(null);
            Image c4 = setImage(hand[3]);
            card4.setIcon(new ImageIcon(c4));
            card4.setText(null);
            Image c5 = setImage(hand[4]);
            card5.setIcon(new ImageIcon(c5));
            card5.setText(null);
            card1.setVisible(true);
            card2.setVisible(true);
            card3.setVisible(true);
            card4.setVisible(true);
            card5.setVisible(true);
        }
        public void showCard(String position, String image){
            Image card;
            card = setImage(image);
            ImageIcon icon = new ImageIcon(card);
            switch(position){
                case "own": own.setIcon(icon);
                            break;
                case "left": left.setIcon(icon);
                            break;
                case "right": right.setIcon(icon);
                            break;
                case "across": across.setIcon(icon);
                            break;
                case "deck": deck.setIcon(icon);
                            break;
            }
        }
        public void setTrumpButtons(String unplayable){
            Image c1 = setImage("H");
            card1.setIcon(new ImageIcon(c1));
            card1.setText(null);
            Image c2 = setImage("S");
            card2.setIcon(new ImageIcon(c2));
            card2.setText(null);
            Image c3 = setImage("D");
            card3.setIcon(new ImageIcon(c3));
            card3.setText(null);
            Image c4 = setImage("C");
            card4.setIcon(new ImageIcon(c4));
            card4.setText(null);
            card5.setText("Pass");
            card5.setIcon(null);
            switch(unplayable){
                case "H": card1.setEnabled(false);
                    break;
                case "S": card2.setEnabled(false);
                    break;
                case "D": card3.setEnabled(false);
                    break;
                case "C": card4.setEnabled(false);
                    break;
            }
        }
        public void setPlayers(int myNumber){
            switch(myNumber){
                case 0:
                    ownText.setText("Player 1");
                    PL = 1;
                    leftText.setText("Player 2");
                    PA = 2;
                    acrossText.setText("Player 3");
                    PR = 3;
                    rightText.setText("Player 4");
                    break;
                case 1:
                    ownText.setText("Player 2");
                    PL = 2;
                    leftText.setText("Player 3");
                    PA = 3;
                    acrossText.setText("Player 4");
                    PR = 0;
                    rightText.setText("Player 1");
                    break;
                case 2:
                    ownText.setText("Player 3");
                    PL = 3;
                    leftText.setText("Player 4");
                    PA = 0;
                    acrossText.setText("Player 1");
                    PR = 1;
                    rightText.setText("Player 2");
                    break;
                case 3:
                    ownText.setText("Player 4");
                    PL = 0;
                    leftText.setText("Player 1");
                    PA = 1;
                    acrossText.setText("Player 2");
                    PR = 2;
                    rightText.setText("Player 3");
                    break;
            }
        }
        public void orderUp(){
            card1.setVisible(false);
            card3.setVisible(false);
            card5.setVisible(false);
            card2.setText("Order Up");
            card2.setIcon(null);
            card4.setText("Pass");
            card4.setIcon(null);
        }
        public void setOtherCard(int player, String card){
            int PLtemp = PL;
            int PRtemp = PR;
            int PAtemp = PA;
            if(player==PLtemp){
                showCard("left", card);
            }
            else if(player==PRtemp){
                showCard("right", card);
            }
            else if(player==PAtemp){
                showCard("across", card);
            }
        }
	public void SendString(String s)
	{
		try
		{
			socketOutput.write(s.getBytes());
			System.out.println("Sent:"+s);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
        public void setTrump(int dealer, String suit){
            String theSuits = "";
            switch(suit){
                case "H":
                    theSuits = "Hearts";
                    showCard("deck", "H");
                    break;
                case "C":
                    theSuits = "Clubs";
                    showCard("deck", "C");
                    break;
                case "S":
                    theSuits = "Spades";
                    showCard("deck", "S");
                    break;
                case "D":
                    theSuits = "Diamonds";
                    showCard("deck", "D");
                    break;
            }
            information.setText("Trump is: " + theSuits);
            if(dealer==playerNumber){
                information.setText("Select throwaway card");
                setHand();
            }
            else{
                card1.setVisible(false);
                card2.setVisible(false);
                card3.setVisible(false);
                card4.setVisible(false);
                card5.setVisible(false);
            }
        }
        public class card1ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
                    if(state.equals("Setting"))
                    {
                        String temp = "H";
			SendString(temp);
                    }
                    else if(state.equals("Playing"))
                    {
                        showCard("own", hand[0]);
                        card1.setEnabled(false);
                        String temp = hand[0];
			SendString(temp);
                        information.setText("Now we wait....");
                    }
                    else if(state.equals("Called"))
                    {
                        hand[0]= faceUpCard;
                        setHand();
                        card1.setVisible(false);
                        card2.setVisible(false);
                        card3.setVisible(false);
                        card4.setVisible(false);
                        card5.setVisible(false);
                    }
		}
	}
	public class card2ButtonListener  implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
                    if(state.equals("Setting"))
                    {
                        String temp = "S";
			SendString(temp);
                    }
                    else if(state.equals("Playing"))
                    {
                        showCard("own", hand[1]);
                        card2.setEnabled(false);
			String temp = hand[1];
			SendString(temp);
                        information.setText("Now we wait....");

                    }
                    else if(state.equals("Ordering")){
                        String temp = "1";
			SendString(temp);
                        showCard("deck","deck");
                        information.setText("Trump is :");
                        
                    }
                    else if(state.equals("Called"))
                    {
                        hand[1]= faceUpCard;
                        setHand();
                        card1.setVisible(false);
                        card2.setVisible(false);
                        card3.setVisible(false);
                        card4.setVisible(false);
                        card5.setVisible(false);
                    }
		}
	}
        public class card3ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
                    if(state.equals("Setting"))
                    {
                        String temp = "D";
			SendString(temp);
                    }
                    else if(state.equals("Playing"))
                    {
			showCard("own", hand[2]);
                        card3.setEnabled(false);
                        String temp = hand[2];
			SendString(temp);
                        information.setText("Now we wait....");

                    }
                    else if(state.equals("Called"))
                    {
                        hand[2]= faceUpCard;
                        setHand();
                        card1.setVisible(false);
                        card2.setVisible(false);
                        card3.setVisible(false);
                        card4.setVisible(false);
                        card5.setVisible(false);
                    }
		}
	}
	public class card4ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
                    if(state.equals("Setting"))
                    {
                        String temp = "C";
			SendString(temp);
                    }
                    else if(state.equals("Playing"))
                    {
                        showCard("own", hand[3]);
                        card4.setEnabled(false);
                        String temp = hand[3];
			SendString(temp);
                        information.setText("Now we wait....");
                    }
                    else if(state.equals("Ordering")){
                        String temp = "2";
			SendString(temp);
                    }
                    else if(state.equals("Called"))
                    {
                        hand[3]= faceUpCard;
                        setHand();
                        card1.setVisible(false);
                        card2.setVisible(false);
                        card3.setVisible(false);
                        card4.setVisible(false);
                        card5.setVisible(false);
                    }
		}
        }
	public class card5ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
                    if(state.equals("Setting"))
                    {
                        String temp = "0";
			SendString(temp);
                    }
                    else if(state.equals("Playing"))
                    {
                        showCard("own", hand[4]);
                        card5.setEnabled(false);
                        String temp = hand[4];
			SendString(temp);
                        information.setText("Now we wait....");
                    }
                    else if(state.equals("Called"))
                    {
                        hand[4]= faceUpCard;
                        System.out.print(hand[4]);
                        setHand();
                        card1.setVisible(false);
                        card2.setVisible(false);
                        card3.setVisible(false);
                        card4.setVisible(false);
                        card5.setVisible(false);
                    }
		}
	}
	
	public class ConnectionThread extends Thread
	{
		public ConnectionThread()
		{
			
		}
		public void run()
		{
			try
			{
				byte[] readBuffer;
				while(true)
				{
					readBuffer = new byte[256];
					socketInput.read(readBuffer);
					String decoded = new String(readBuffer);
                                        String s = decoded;
                                        String delims = ",";
                                        String[] tokens = s.split(delims);
                                        switch(tokens[0]){
                                            case "00":
                                                playerNumber = Integer.parseInt(tokens[1]);
                                                state = "Connecting";
                                                System.out.print("You are player number " +playerNumber+1);
                                                setPlayers(playerNumber);
                                                break;
                                            case "01":
                                                state = "Dealing";
                                                String temp = tokens[1];
                                                String splitter = ":";
                                                String[] cards = temp.split(splitter);
                                                for(int i =0; i<5; i++){
                                                    hand[i]=cards[i];
                                                }
                                                setHand();
                                                card1.setEnabled(true);
                                                card2.setEnabled(true);
                                                card3.setEnabled(true);
                                                card4.setEnabled(true);
                                                card5.setEnabled(true);
                                                faceUpCard = tokens[2];
                                                showCard("deck",tokens[2]);
                                                break;
                                            case "02":
                                                state = "Ordering";
                                                orderUp();
                                                break;
                                            case "03":
                                                state = "Setting";
                                                String unplayable = tokens[1];
                                                setTrumpButtons(unplayable);
                                                card1.setVisible(true);
                                                card2.setVisible(true);
                                                card3.setVisible(true);
                                                card4.setVisible(true);
                                                card5.setVisible(true);
                                                break;
                                            case "04":
                                                state = "Playing";
                                                setHand();
                                                information.setText("Your turn to play!");
                                                card1.setVisible(true);
                                                card2.setVisible(true);
                                                card3.setVisible(true);
                                                card4.setVisible(true);
                                                card5.setVisible(true);
                                                break;
                                            case "05":
                                                state = "Scoring";
                                                int team1, team2;
                                                team1 = Integer.parseInt(tokens[1]);
                                                team2 = Integer.parseInt(tokens[2]);
                                                if ((team1<10)&&(team2<10)){
                                                    scores.setText("Team 1: " + tokens[1] +" | Team 2: " + tokens[2] + " ");
                                                }
                                                else if (team1>=10){
                                                    information.setText("TEAM 1 WON");
                                                }
                                                else if (team2>=10){
                                                    information.setText("TEAM 2 WON");
                                                }
                                                break;
                                            case "06":
                                                state = "Waiting";
                                                card1.setVisible(false);
                                                card2.setVisible(false);
                                                card3.setVisible(false);
                                                card4.setVisible(false);
                                                card5.setVisible(false);
                                                break;
                                            case "07":
                                                state = "Called";
                                                int dNo = Integer.parseInt(tokens[1]);
                                                showCard("deck","deck");
                                                setTrump(dNo, tokens[2]);
                                                
                                                break;
                                            case "08":
                                                state = "setTrump";
                                                showCard("deck","deck");
                                                switch(tokens[1]){
                                                    case "H": information.setText("Trump is Hearts");
                                                            break;
                                                    case "C": information.setText("Trump is Clubs");
                                                            break;
                                                    case "S": information.setText("Trump is Spades");
                                                            break;
                                                    case "D": information.setText("Trump is Diamonds");
                                                            break;
                                                }
                                                
                                                card1.setEnabled(true);
                                                card2.setEnabled(true);
                                                card3.setEnabled(true);
                                                card4.setEnabled(true);
                                                card5.setEnabled(true);
                                                break;
                                            case "09":
                                                state = "showCards";
                                                if(tokens[1]!="")
                                                {
                                                    int play = Integer.parseInt(tokens[1]);
                                                    setOtherCard(play, tokens[2]);
                                                }
                                                break;
                                            case "10":
                                                state = "Clearing";
                                                showCard("own", "holder");
                                                showCard("left", "holder");
                                                showCard("across", "holder");
                                                showCard("right", "holder");
                                                break;
                                            case "11":
                                                information.setText("Someone disconnected, BYE");
                                                try
                                                {
                                                        theSocket.close();
                                                }
                                                catch(Exception e)
                                                {
                                                    System.out.println("I caught an exception");
                                                        System.out.println(e.toString());
                                                }
                                                System.out.println("a player disconnect");
                                                break;
                                        }
					System.out.println("Got:" + decoded);
				}				
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
			}			
		}
	}
	ConnectionThread theConnection;
	public class ConnectButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String one = new String("hello");
			String two = new String("world");
			try
			{
				theSocket = new Socket(InetAddress.getByName("52.91.187.211"),2000);
				socketOutput = theSocket.getOutputStream();
				socketInput = theSocket.getInputStream();
				theConnection = new ConnectionThread();
				theConnection.start();
				System.out.println("connect");
                                connectButton.setEnabled(false);
			}
			catch(Exception ex)
			{
				System.out.println(ex.toString());
			}
		}
	}
	public class DisconnectButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
                            String temp = "kill";
                            SendString(temp);
                            theSocket.close();
			}
			catch(Exception ex)
			{
                            System.out.println("I caught an exception");
				System.out.println(ex.toString());
			}
			System.out.println("disconnect");
		}
	}
	public static void main(String args[]) 
	{
		new Euchre();
                new Euchre();
                new Euchre();
                new Euchre();
                 
	}
	Euchre()
	{
            
            
            card1.setVisible(false);
            card2.setVisible(false);
            card3.setVisible(false);
            card4.setVisible(false);
            card5.setVisible(false);
            //orderUp();
            //setHand();
            //setTrumpButtons("heart");
            
            
            
            jlbHelloWorld = new JLabel("Hello World");
            
            card2.addActionListener(new card2ButtonListener());
            card2.setSize(200,20);
            card4.addActionListener(new card4ButtonListener());
            card4.setSize(200,20);
            card3.addActionListener(new card3ButtonListener());
            card3.setSize(200,20);
            card1.addActionListener(new card1ButtonListener());
            card1.setSize(200,20);
            card5.addActionListener(new card5ButtonListener());
            card5.setSize(400,20);

            drawPanel();

            JPanel directionPanel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0; c.gridy = 0;        
            directionPanel.add(card1,c);
            c.gridx = 1; c.gridy = 0;
            directionPanel.add(card2,c);
            c.gridx = 2; c.gridy = 0;
            directionPanel.add(card3,c);
            c.gridx = 3; c.gridy = 0;
            directionPanel.add(card4,c);
            c.gridx = 4; c.gridy = 0;
            directionPanel.add(card5,c);
            //c.gridx = 5; c.gridy = 0;
            //directionPanel.add(PL,c);

            //JPanel connectionPanel = new JPanel(new GridBagLayout());
            connectButton.addActionListener(new ConnectButtonListener());
            c.gridx = 2; c.gridy = 1;
            directionPanel.add(connectButton,c);
            JButton disconnectButton = new JButton("Disconnect");
            disconnectButton.addActionListener(new DisconnectButtonListener());
            c.gridx = 3; c.gridy = 1;
            directionPanel.add(disconnectButton,c);

            /*setLayout(new GridLayout(2,2));
            add(theField);
            add(connectionPanel);
            add(directionPanel);*/
            setLayout(new GridBagLayout());
            c.gridx = 0; c.gridy = 0;
            c.gridheight = 15;
            c.gridwidth = 15;
            c.weightx = 1.0;c.weighty=1.0;
            c.fill = GridBagConstraints.BOTH;
            //add(theField,c);
            c.gridx = 0; c.gridy = 15;
            c.gridheight = 1;
            c.gridwidth = 5;
            c.fill = GridBagConstraints.NONE;
            add(directionPanel,c);
            add(playPanel);
            
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            
            this.setSize(500,500);
            this.setTitle("Euchre Extravaganza");
            setVisible(true);
	}
}
