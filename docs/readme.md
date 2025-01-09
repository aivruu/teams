# teams | ![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/aivruu/teams/build.yml)
[![](https://jitpack.io/v/aivruu/teams.svg)](https://jitpack.io/#aivruu/teams)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/b08871d529b147f79ef24a9856abdee1)](https://app.codacy.com/gh/aivruu/teams/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
![GitHub License](https://img.shields.io/github/license/aivruu/teams)
![GitHub commit activity](https://img.shields.io/github/commit-activity/t/aivruu/teams)

`AldrTeams` is a modern-plugin for Paper (or forks) that allows you to create customizable-tags to use and show prior or
in front of your nick, and select them through a highly-customizable menu. As well, perform modifications to these tags
in-game easily.

## Features
* Create, delete, and modify tags.
* Select them trought a customizable-menu.
* Executable actions for menu-open and items-interaction.
* Customizable Menu Items (left and right-click actions, glow-effect, permission-required, tag-assigned).
* NMS usage for tag's scoreboard-team management and visuals.
* MiniMessage Support.

## Previews
Some previews of the plugin's features and functionality.

![join-tag-view](https://github.com/aivruu/teams/blob/main/previews/join-tag-view.gif)
![menu-viewer](https://github.com/aivruu/teams/blob/main/previews/menu-viewer.gif)
![tag-editing](https://github.com/aivruu/teams/blob/main/previews/tag-editing.gif)
![tag-right-click-preview](https://github.com/aivruu/teams/blob/main/previews/tag-right-click-preview.gif)
![tag-selection](https://github.com/aivruu/teams/blob/main/previews/tag-selection.gif)

## Building
The plugin's uses Gradle and requires Java 21+ for building.
```
git clone git@github.com:aivruu/teams
cd teams
./gradlew shadowJar
```
