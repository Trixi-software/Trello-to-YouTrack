package com.julienvey.trello.impl.domaininternal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.julienvey.trello.domain.TrelloEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentToRead extends TrelloEntity {
    private String id;
    private String idMemberCreator;
    private Data data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdMemberCreator() {
        return idMemberCreator;
    }

    public void setIdMemberCreator(String idMemberCreator) {
        this.idMemberCreator = idMemberCreator;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
