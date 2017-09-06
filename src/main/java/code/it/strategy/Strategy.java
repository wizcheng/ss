package code.it.strategy;

import code.it.GameHistory;
import code.it.GameSetting;
import code.it.GameState;

import java.util.List;

public interface Strategy {

    List<GameState> solve(GameSetting setting, GameHistory history, GameState initialState);

}
