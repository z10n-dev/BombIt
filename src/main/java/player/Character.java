package player;

public enum Character {
    ADVENTURER("Adventurer", "adventurer.png"),
    CYCLOPS("Cyclops", "cyclops.png"),
    GHOST("Ghost", "ghost.png"),
    KNIGHT("Knight", "knight.png"),
    MAIDEN("Maiden", "maiden.png"),
    VIKING("Viking", "viking.png"),
    WIZARD("Wizard", "wizard.png");

    private final String displayName;
    private final String imageFileName;

    Character(String displayName, String imageFileName) {
        this.displayName = displayName;
        this.imageFileName = imageFileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImageFileName() {
        return imageFileName;
    }
}
