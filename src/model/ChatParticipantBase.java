package model;

public abstract class ChatParticipantBase extends BaseEntity implements Loggable {
    protected String name;

    public ChatParticipantBase(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getSystemPrompt();
    public abstract String getRole();
    public abstract void displayInfo();
}