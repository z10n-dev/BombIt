package state;

import core.GameContext;
import player.Player;
import player.Character;
import style.Colors;

public class GamePlayState extends GameState{

    Player player;

    public GamePlayState(GameContext gameContext, Character selectedCharacter) {
        super(gameContext);
        this.player = new Player(selectedCharacter);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw() {
        app.background(Colors.BACKGROUND);
    }
}
