package cz.trixi.youtrack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static cz.trixi.Config.*;

public class YouTrack {

    private final String apiURL = baseUrl + "/api";

    private String projectId;

    private Map<String, String> tagIdMap = new HashMap<>();

    public void createProject() throws IOException {
        JSONObject projectData = new JSONObject();
        projectData.put("description", youTrackProjectDescription);
        projectData.put("name", youTrackProjectName);
        projectData.put("shortName", youTrackProjectShortName);
        JSONObject leader = new JSONObject();
        leader.put("id", youTrackProjectLeaderId);
        projectData.put("leader", leader);
        projectData.put("template", youTrackProjectTemplate);

        String endpoint = "/admin/projects?fields=id,shortName,name,leader(id,login,name)";
        JSONObject responseJson = sendPostRequest(endpoint, projectData);

        if (responseJson != null) {
            projectId = responseJson.getString("id");
        }
    }

    public void createAgileBoard() throws IOException {
        JSONObject boardData = new JSONObject();
        boardData.put("name", youTrackAgileBoardName);
        JSONObject projects = new JSONObject();
        projects.put("id", projectId);
        boardData.put("projects", new JSONObject[]{projects});
        boardData.put("updateableByProjectBased", true);
        boardData.put("visibleForProjectBased", true);

        String endpoint = "/agiles?template=scrum&fields=id,name,owner(id,name),projects(id,name),sprints(id,name)";
        sendPostRequest(endpoint, boardData);
    }

    public String createIssue(String summary, String description, String trelloColumnName) throws IOException {
        JSONObject issueData = new JSONObject();
        issueData.put("summary", summary);
        issueData.put("description", description);
        JSONObject project = new JSONObject();
        project.put("id", projectId);
        issueData.put("project", project);

        JSONArray customFields = new JSONArray();
        JSONObject customField = new JSONObject();
        customField.put("name", "State");
        customField.put("$type", "SingleEnumIssueCustomField");

        String youTrackState = getYoutrackStateFromTrelloColumnName(trelloColumnName);
        JSONObject valueObject = new JSONObject();
        valueObject.put("name", youTrackState);
        valueObject.put("$type", "StateBundleElement");
        customField.put("value", valueObject);

        customFields.put(customField);
        issueData.put("customFields", customFields);

        String endpoint = "/issues?fields=idReadable";
        JSONObject responseJson = sendPostRequest(endpoint, issueData);

        if (responseJson != null) {
            return responseJson.getString("idReadable");
        } else {
            return null;
        }
    }

    public String getYouTrackUserIdFromTrelloUserId(String trelloUserId) {
        return trelloToYouTrackUsers.get(trelloUserId);
    }

    public void addCommentToIssue(String youTrackIssueId, String text, String user) throws IOException {
        JSONObject commentData = new JSONObject();
        commentData.put("text", text);
        JSONObject author = new JSONObject();

        String userId = getYouTrackUserIdFromTrelloUserId( user );
        author.put("id", userId);
        commentData.put("author", author);

        String endpoint = "/issues/" + youTrackIssueId + "/comments?fields=id,author(login,name,id),deleted,text,updated,visibility(permittedGroups(name,id),permittedUsers(id,name,login))";

        sendPostRequest(endpoint, commentData);
    }

    public void createTag(String tagName) throws IOException {
        JSONObject tagData = new JSONObject();
        tagData.put("name", tagName);

        String endpoint = "/issueTags?fields=id,name,owner(login,name),visibleFor(name,id),issues(idReadable,summary)";

        JSONObject responseJson = sendPostRequest(endpoint, tagData);

        if (responseJson != null) {
            String tagId = responseJson.getString("id");
            tagIdMap.put(tagName, tagId);
        }
    }

    public void assignTagToIssue(String issueId, String tagId) throws IOException {
        JSONObject tagData = new JSONObject();
        tagData.put("id", tagId);

        String endpoint = "/issues/" + issueId + "/tags?fields=id,name,owner(login,name),visibleFor(name,id)";

        sendPostRequest(endpoint, tagData);
    }

    private JSONObject sendPostRequest(String endpoint, JSONObject requestData) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL( apiURL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + youTrackToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                writer.write(requestData.toString());
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return new JSONObject(response.toString());
                }
            } else {
                return null;
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public Map<String, String> getTagIdMap() {
        return tagIdMap;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
