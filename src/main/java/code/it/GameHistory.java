package code.it;

import java.util.HashSet;
import java.util.Set;

public class GameHistory {

    private Set<GameState> history;

    public GameHistory() {
        history = new HashSet<>(10000);
    }

    public void add(GameState state){
        history.add(state);
    }

    public boolean exist(GameState state){
        return history.contains(state);
    }

}
