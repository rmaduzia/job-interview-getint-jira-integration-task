package io.getint.recruitment_task;

public enum ListOfFieldsToCopy {

    PROJECT_FIELD("project"),
    SUMMARY_FIELD("summary"),
    DESCRIPTION_FIELD("description"),
    ISSUE_TYPE("issuetype");

    public final String fieldName;

    ListOfFieldsToCopy(String fieldName) {
        this.fieldName = fieldName;
    }

    public static String getAllFieldsNames() {
        return "&fields=" + String.join("&fields=", PROJECT_FIELD.fieldName, SUMMARY_FIELD.fieldName,
            DESCRIPTION_FIELD.fieldName, ISSUE_TYPE.fieldName);
    }
}