Practice project: Chat Room

1. Run Sercer in Intellij (local host)
   waiting until see the msg: "Chat Server is listening on port 2222"

2. Run client in Command Line.
   Following directions on console. 
   It will ask you: "Enter the username && optional [portNumber] [serverAddress]:"
   Please input client name, like type in "Jack"

3. Receive msg from Server on consult as following:
 
	Hello.! Welcome to the Multi-Thread Chat Room.
	There are xxx other connected clients.

	Here are the instructions:
	Type 'logoff' if you want to disconnect from the server.
	Type 'who' to see the list of active clients.
	Type '@user_specified' to send message to a specified user.
	Type '@all' to broadcast to all users connected.
	Type '!!user_specified' to send auto-generated insult message to a specified user. 

4. Try query other users by input "who"
   May get response "Current connected users are: rose"

5. Try send msg to direct user by input "@rose how are you?"
   rose should get a msg "jack to you in private: how are you?"

6. Try send broadcast msg by input "@all hello"
   Every connected users get boradcast msg "jack to all: hello"

7. Try send insult to special user by input "!!rose"
   Every connected users get boradcast msg "rose to all: -->jack: insult msg"

8. Try logoff by input "logoff"
   Get respond msg "You are disconnected to the system successfully, see you next time :)"

9. You can open more Command Line window to run multiple clients



Hoping you enjoy it. Have fine!
                                                     --Yi, Jiayi, Xiaowei
