package net.yura.domination.engine.core;

public enum StatType {
    COUNTRIES("countries", false),
    ARMIES("armies", false),
    KILLS("kills", true),
    CASUALTIES("casualties", true),
    REINFORCEMENTS("reinforcements", true),
    CONTINENTS("continents", false),
    CONNECTED_EMPIRE("empire", false),
    ATTACKS("attacks", true),
    RETREATS("retreats", true),
    COUNTRIES_WON("victories", true),
    COUNTRIES_LOST("defeats", true),
    ATTACKED("attacks", true),
    CARDS("cards", false),
    DICE("dice", false);

    private String name;
    private boolean summable;

    StatType(String name, boolean summable) {
        this.name = name;
        this.summable = summable;
    }

    public boolean isSummable() {
        return summable;
    }

    public String getName() {
        return name;
    }

    public static StatType fromOrdinal(int id) {
        return values()[id];
    }

}
