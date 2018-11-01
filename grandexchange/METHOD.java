package scripts.api.grandexchange;

public enum METHOD {

    DEFAULT(null),

    NOTES(new String[] { "Collect-note", "Collect-notes", "Collect to inventory", "Collect" }),

    ITEMS(new String[] { "Collect-item", "Collect-items", "Collect to inventory", "Collect" }),

    BANK(new String[] { "Bank", "Collect to bank" });

    public final String[] actions;

    METHOD(String[] actions) {
        this.actions = actions;
    }

    public String[] getActions() {
        return this.actions;
    }

}