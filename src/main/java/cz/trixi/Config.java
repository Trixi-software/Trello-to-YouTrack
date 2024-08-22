package cz.trixi;

import java.util.Map;

public class Config {

    /** Set to true if you want to link Trello cards to YouTrack issues. If set true then each issue in trello will get a comment
     * with the link to the newly created youTrack issue. */
    public static boolean linkTrelloCardsToYouTrackIssues = false;

    /** Generate your YouTrack token based on a manual at this website https://www.jetbrains.com/help/youtrack/server/manage-permanent-token.html and copy it here. */
    public static final String youTrackToken = "";

    /** YouTrack base URL.
     * Example: "https://example.youtrack.cloud" */
    public static final String baseUrl = "";

    /** Generate your API key at this website https://trello.com/power-ups/admin by setting up a new power-up/integration.
     * Then click the link on the right side to generate a new trello token. */
    static String trelloKey = "";
    static String trelloAccessToken = "";

    /** You can find your board id by exporting as JSON. The id is the first parameter. Or just put ".json" at the end of your
     * board url in your browser and copy the id from there. */
    static String trelloBoardId = "";

    public static String youTrackProjectDescription = "";
    public static String youTrackProjectName = "";
    public static String youTrackProjectShortName = "";
    /** You can find ids of all users by using a request as described here: https://www.postman.com/youtrack-dev/workspace/youtrack/request/24356758-0efd1e8f-6365-4847-9824-0af1fd609b48.
     * Example: "1-5" */
    public static String youTrackProjectLeaderId = "";
    /** Template for the YouTrack project. By default scrum is used. */
    public static String youTrackProjectTemplate = "scrum";
    public static String youTrackAgileBoardName = "";

    /** Map of Trello user ids to YouTrack user ids. Put the trello user id as a key, youTrack user id as the value.
     * Example: "example_user_1", "2-1" */
    public static final Map<String, String> trelloToYouTrackUsers = Map.of(
            "", ""
    );

    /** Method to get the YouTrack state based on the Trello column name. By default there is an example how the method should be constructed.
     * Change the trello column names and youTrack states based on your needs. */
    public static String getYoutrackStateFromTrelloColumnName(String columnName) {
        switch (columnName) {
        case "Icebox":
        case "To Do":
            return "Submitted";
        case "Sprint":
            return "Open";
        case "In Progress":
            return "In Progress";
        case "Needs Code Review":
        case "Needs Acceptation":
            return "Fixed";
        case "Done":
            return "Done";
        default:
            return "Submitted";
        }
    }

}
