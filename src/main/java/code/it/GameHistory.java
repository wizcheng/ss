package code.it;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameHistory {

    private Cache<GameState, Boolean> history;

    public GameHistory() {
        history = CacheBuilder.newBuilder()
                .softValues()
                .maximumSize(5_000_000)
                .build();
    }

    public void add(GameState state){
        history.put(state, true);
    }

    public boolean exist(GameState state){
        return history.getIfPresent(state) != null;
    }

    public int size() {
        return (int) history.size();
    }
}
