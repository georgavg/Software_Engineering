package TraineeshipApplication.model;

public enum Role {
    STUDENT("STUDENT"),
    PROFESSOR("PROFESSOR"),
    ADMIN("ADMIN"),
    COMPANY("COMPANY"),
	TRAINEESHIP_COMMITTEE("TRAINEESHIP_COMMITTEE");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
