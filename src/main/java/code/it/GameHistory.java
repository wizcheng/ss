package code.it;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameHistory {

    private Set<GameState> history;

    public GameHistory() {
        history = new HashSet<>(1000000);
    }

    public void add(GameState state){
        history.add(state);
    }

    public void addAll(List<GameState> states) {
        history.addAll(states);
    }

    public boolean exist(GameState state){
        return history.contains(state);
    }

    public int size() {
        return history.size();
    }
}
