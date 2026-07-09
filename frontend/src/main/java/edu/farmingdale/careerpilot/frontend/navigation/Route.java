package edu.farmingdale.careerpilot.frontend.navigation;

public enum Route {
    DASHBOARD("Dashboard"),
    PROFILE("Resume Profile"),
    GENERATE("Generate"),
    DOCUMENTS("Saved Documents");

    private final String label;

    Route(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
