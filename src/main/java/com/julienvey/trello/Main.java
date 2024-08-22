package com.julienvey.trello;

import com.julienvey.trello.domain.*;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.domaininternal.CommentToRead;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import cz.trixi.youtrack.YouTrack;

import java.io.IOException;

import java.text.Normalizer;
import java.util.*;

import static cz.trixi.Config.*;
import static cz.trixi.YoutrackImporter.trelloToYouTrack;

public class Main {

    public static void main( String[] args ) {
        trelloToYouTrack();
    }

}

