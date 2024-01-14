Project Theme: Game with Client Server and Path Finding Algorithm

How to run the project:
1) runServerV2.bat or java -Djava.library.path=lib\natives -cp lib\jars\*;bin; server.ServerV2
2) runClientV2.bat or java -Djava.library.path=lib\natives -cp lib\jars\*;bin; client.ClientLauncherV2

Brief description of the server capabilities:
	-Clients will wait for the server to be up
	-Clients will try to reconnect after 5sec if the connection fails, unless they close the game window.
	-Server can run commands such as "help", "kick n", "start".
	-The client cannot access the game without Server's permission, achieved by clicking "Accept" in the table, as well as starting the game from the server side with the command "start" in the bottom JTextField

Tools and sources:
	-Libraries: LWJGL(Light Weight Java Game Library) -> openGL, slick-util
	-Technologies: JDK, Eclipse
	-Sources: OpenGL Graphical Game Engine - ThinMatrix https://www.youtube.com/watch?v=VS8wlS9hF8E&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP
		A* pathfinding algorithm - RyiSnow https://www.youtube.com/watch?v=2JNEme00ZFA
		StackOverflow, Reddit, etc...