package code.it.strategy;

import code.it.GameHistory;
import code.it.GameSetting;
import code.it.GameState;
import code.it.GameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BFS implements Strategy {

    private int maxSteps;

    public BFS(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    @Override
    public List<GameState> solve(GameSetting setting, GameHistory history, GameState initialState) {

        System.out.println("Game settings ----");
        System.out.println(setting);

        boolean gameCompleted = false;
        boolean gameFailed = false;
        int step = 0;

        System.out.println("Initial ----");
        System.out.println(initialState);

        List<GameState> steps = new ArrayList<>();
        List<GameState> currentStates = new ArrayList<>();
        GameState state = GameUtils.updateMovable(setting, initialState.copy());
        currentStates.add(state);
        history.add(state);

        System.out.println("Start ----");
        System.out.println(state);

        while(!gameCompleted && !gameFailed && step++<maxSteps){

            System.out.println("Iterator " + step +", " + currentStates.size() + " possible move(s).");

            List<GameState> nextStates = search(setting, history, currentStates)
                    .stream()
                    .filter(s -> !history.exist(s))
                    .filter(s -> !GameUtils.isDead(setting, s))
                    .collect(Collectors.toList());

            nextStates.forEach(System.out::println);

            if (nextStates.size()==0){
                gameFailed = true;
            } else {
                List<GameState> solutions = nextStates.stream()
                        .filter(s -> GameUtils.isEnd(setting, s))
                        .collect(Collectors.toList());


                if (solutions.size() > 0){
                    gameCompleted = true;
                } else {
                    currentStates = nextStates;
                }
            }
        }

        if (gameCompleted) System.out.println("Game completed");
        else if (gameFailed) System.out.println("Game failed, no possible moves");
        else System.out.println("Game failed, exist max move(s) ("+maxSteps+")");

        return steps;
    }

    private List<GameState> search(GameSetting setting, GameHistory history, List<GameState> states) {

        return states.stream()
                .map(s -> GameUtils.nextMove(setting, history, s))
                .flatMap(Collection::stream)
                .distinct()
                .map(s -> GameUtils.updateMovable(setting, s))
                .collect(Collectors.toList());

    }


}
