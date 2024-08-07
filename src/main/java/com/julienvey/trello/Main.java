package com.julienvey.trello;

import com.julienvey.trello.domain.*;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.domaininternal.CommentToRead;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import com.julienvey.trello.youtrack.YouTrack;

import java.io.IOException;

import java.text.Normalizer;
import java.util.*;

import static com.julienvey.trello.Config.*;

public class Main {

    public static void main( String[] args ) {
        trelloToYouTrack();
    }

    private static void trelloToYouTrack() {
        try {
            YouTrack youTrack = new YouTrack();
            youTrack.createProject();
            youTrack.createAgileBoard();

            Trello trelloApi = new TrelloImpl( trelloKey, trelloAccessToken, new ApacheHttpClient() );
            Board board = trelloApi.getBoard( trelloBoardId );
            List<Card> issues = trelloApi.getBoardCards( board.getId() );
            List<Card> archivedIssues = trelloApi.getBoardArchivedCards( board.getId() );
            List<TList> trelloColumns = board.fetchLists();
            List<Label> allLabels = board.fetchLabels();

            for ( Label l : allLabels ) {
                youTrack.createTag( l.getName() );
            }
            Map<String, String> youTrackTags = youTrack.getTagIdMap();

            for ( Card issue : issues ) {
                List<CommentToRead> comments = trelloApi.getCardComments( issue.getId() );
                List<Attachment> attachments = trelloApi.getCardAttachments( issue.getId() );
                List<Label> labels = issue.getLabels();

                Optional<TList> currentColumn = trelloColumns.stream().filter( e -> Objects.equals( e.getId(), issue.getIdList() ) ).findFirst();

                String youTrackIssueId = youTrack.createIssue( issue.getName(), issue.getDesc(), currentColumn.get().getName() );

                for ( CommentToRead c : comments ) {
                    youTrack.addCommentToIssue( youTrackIssueId, c.getData().getText(), c.getIdMemberCreator() );
                }
                for ( Attachment a : attachments ) {
                    youTrack.addCommentToIssue( youTrackIssueId, a.getUrl(), "default" );
                }
                for ( Label l : labels ) {
                    youTrack.assignTagToIssue( youTrackIssueId, youTrackTags.get( l.getName() ));
                }

                if ( linkTrelloCardsToYouTrackIssues ) {
                    trelloApi.addCommentToCard( issue.getId(),  youTrack.getBaseUrl() + "/issue/" + youTrackIssueId + "/" + toUrlFriendlyString( issue.getName() ));
                }
                youTrack.addCommentToIssue( youTrackIssueId, issue.getUrl(), "default" );
            }
            for ( Card archivedIssue : archivedIssues ) {
                List<CommentToRead> comments = trelloApi.getCardComments( archivedIssue.getId() );
                List<Attachment> attachments = trelloApi.getCardAttachments( archivedIssue.getId() );
                List<Label> labels = archivedIssue.getLabels();
                String youTrackIssueId = youTrack.createIssue( archivedIssue.getName(), archivedIssue.getDesc(), "Done" );
                for ( CommentToRead c : comments ) {
                    youTrack.addCommentToIssue( youTrackIssueId, c.getData().getText(), c.getIdMemberCreator() );
                }
                for ( Attachment a : attachments ) {
                    youTrack.addCommentToIssue( youTrackIssueId, a.getUrl(), "default" );
                }
                for ( Label l : labels ) {
                    youTrack.assignTagToIssue( youTrackIssueId, youTrackTags.get( l.getName() ));
                }
                if ( linkTrelloCardsToYouTrackIssues ) {
                    trelloApi.addCommentToCard( archivedIssue.getId(),  youTrack.getBaseUrl() + "/issue/" + youTrackIssueId + "/" + toUrlFriendlyString( archivedIssue.getName() ));
                }
                youTrack.addCommentToIssue( youTrackIssueId, archivedIssue.getUrl(), "default" );
            }

        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Method to normalize string, so it could be used in URL.
     */
    public static String toUrlFriendlyString(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String urlFriendly = withoutDiacritics.replaceAll("[^\\p{Alnum}]+", "-");
        urlFriendly = urlFriendly.replaceAll("^-+|-+$", "");

        return urlFriendly;
    }

}

