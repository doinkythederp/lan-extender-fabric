# LAN Extender: The world connector

LAN Extender generates a server address when you start your LAN worlds that lets others connect – even if they're connected to a different router!

## Installation

LAN Extender is a [**Fabric**](https://fabricmc.net) mod. Put the LAN Extender jar-file in the `mods` folder in your game directory. You must have [**Mod Menu**](https://www.curseforge.com/minecraft/mc-mods/modmenu), [**Fabric API**](https://www.curseforge.com/minecraft/mc-mods/fabric-api), and [**Cloth Config API**](https://www.curseforge.com/minecraft/mc-mods/cloth-config) (make sure to download the fabric version) installed alongside LAN Extender.

## Setup

LAN Extender requires an [**ngrok**](https://ngrok.com) account to function. [**Click here**](https://dashboard.ngrok.com/signup) to sign up. You should receive an email asking you to verify your email address. Doing so is recommended, but not neccesary for the mod to work. Once you've logged in, you should see the page below. Click on "Your Authtoken" – there is no need to download or install ngrok.

![Use the sidebar to access your authtoken](assets/after_ngrok_login.png)

Then, copy the authtoken, and paste it into the LAN Extender configuration screen.

![Copy the authtoken](assets/ngrok_auth_token.png)
![Open LAN Extender configuration](assets/modmenu.png)
![Paste the authtoken](assets/lan_extender_options.png)

Please note that the authtoken pictured in this guide will not work if you try to use it. You need to create and use your own. Also, if you accidentally leak your authtoken, you can return to the website above to reset it.

## Usage

Once you're in the world you want others to be able to join, click Open to LAN in the game menu and click the checkbox to allow people connected to a different router to join.

![Click the checkbox](assets/open_to_lan.png)
![View the address in chat](assets/opened_to_lan.png)

Anyone who you share the address with can join your world like a normal multiplayer server.

![Join the LAN world using Direct Connect](assets/joining_server.png)
