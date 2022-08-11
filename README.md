# 3D OpenGL Powered Game Engine

![BasicGraphics_gif](https://user-images.githubusercontent.com/36086269/182716270-c7735bcd-28dc-41c8-b0c6-92522b66812f.gif)



## Description

This is a game engine built using the OpenGL wrappers for Java from Lightweight Java Game Library(LWJGL). The engine takes advantage of the GPU parallelization benefits provided by OpenGL to display complex lighting, reflections, and shadow mapping. In addition to the graphical components, the engine contains a capable physics engine that supports mass collision detection, water wave physics, and Newtonian mechanics. The engine itself can be extended to produce a wide variety of game implementations, and a sample game has been built on it in this project.

![image](https://user-images.githubusercontent.com/36086269/182705843-53b7b3d2-461b-406d-a154-f0208b547ca5.png)


## Gameplay

So far, the game-play of the sample game is very basic as most of the work has gone into the underlying engine. That being said, the sample game utilizes the engine's procedural terrain generation tools to generate a variety of biomes and terrains. Mountains, lakes, hills, plains, and oceans will all be generated, each with a unique appearance. In the sample world, the player is in a first person perspective, and has the ability to walk, run, jump, and look around. The player has the ability to shoot triangles that will eventually land and collide into the ground or a tree. Trees have gone through various stages of development in the game, so different screenshots may showcase them quite differently. Collision detection is implemented between all objects that should realistically collide, although ground collision detection is implemented differently than object collision detection. A variety of lighting effects can be seen in the demonstration, including reflections, shading, and shadow casting.

## Screenshots

![image](https://user-images.githubusercontent.com/36086269/182712439-b37f66c5-fc77-41b1-a09c-5c21aad17d2b.png)

![image](https://user-images.githubusercontent.com/36086269/182712602-22a20b04-47b9-442f-a21d-664bc7a91e22.png)

![image](https://user-images.githubusercontent.com/36086269/182712830-624ab600-bd04-47bd-a5e9-07c3238dea14.png)

![image](https://user-images.githubusercontent.com/36086269/182713034-463b0a32-4db4-4d62-aede-d90b7927d46a.png)

## Interesting Bugs

### A collection of some bugs we encountered while developing this project

![image](https://user-images.githubusercontent.com/36086269/182713618-a8be46a0-8e92-42cf-b47f-4c4694b466a0.png)

![image](https://user-images.githubusercontent.com/36086269/182713725-7a5d66a1-487e-4d3b-9175-9530e778a7a9.png)

![image](https://user-images.githubusercontent.com/36086269/182714213-9ea22779-c037-4d60-aeee-3fbaac353232.png)

![image](https://user-images.githubusercontent.com/36086269/182714463-11622105-dc31-414f-b721-ab37dfdd3fe1.png)


