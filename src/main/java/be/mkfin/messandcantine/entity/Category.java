package be.mkfin.messandcantine.entity;

public enum Category {

    VEGAN("VEGAN"),
    DRINK("Drink"),
    SANDWICH("Sandwich"),
    MEAT_LOVER("MEAT LOVER"),
    STARTER("Starter"),
    DESERT("DESERT");

    private final String label;

    Category(String label) {
        this.label = label;
    }
}
