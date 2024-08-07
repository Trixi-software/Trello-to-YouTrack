# Trello to YouTrack Importer

## Overview

The Trello to YouTrack Importer is a Java application designed to facilitate the migration of issues from Trello to YouTrack. This tool extends the functionality of the [taskadapter/trello-java-wrapper](https://github.com/taskadapter/trello-java-wrapper) project, providing a seamless way to transfer all your Trello data into YouTrack.

## Features

- **Project Creation**: Automatically creates a new YouTrack project.
- **Issue Import**: Imports all issues from Trello, including archived ones.
- **Comments Transfer**: Migrates all comments along with the users who made them.
- **Attachments Handling**: Transfers attachments, including GitHub commits and pull requests.
- **Labels Import**: Imports all labels associated with the issues.
- **Issue Linking**: Adds a comment in YouTrack with a link to the Trello issue, and vice versa, ensuring issues are interlinked for easy reference.

## Usage

Before you can use the Trello to YouTrack Importer, you need to set up the configuration in the `Config` class. The details are mentioned
in the `Config` class itself.