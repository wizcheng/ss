package code.it.strategy;

import code.it.GameHistory;
import code.it.GameSetting;
import code.it.GameState;
import code.it.GameUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BFS implements Strategy {

    private boolean debug = true;
    private int maxSteps;
    private int maxWidth = 10_000;
    private int triggerWidth = 300_000;

    public BFS(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    @Override
    public List<GameState> solve(GameSetting setting, GameHistory history, GameState initialState) {

        StringBuilder debugOutput = new StringBuilder();
        System.out.println("Game settings ----");
        System.out.println(GameUtils.toString(GameUtils.toView(setting, initialState)));

        boolean gameCompleted = false;
        boolean gameFailed = false;
        int step = 0;

        GameState endState = null;
        Collection<GameState> currentStates = new LinkedList<>();
        GameState state = initialState.copy();
        GameUtils.updateMovable(setting, state);
        currentStates.add(state);
        history.add(state);

        System.out.println("Start ----");
        System.out.println(GameUtils.toString(GameUtils.toView(setting, initialState)));
        if (debug){
            debugOutput.append(GameUtils.toHtml(setting, initialState, true));
            logDebug(debugOutput);
        }

        while(!gameCompleted && !gameFailed && step++<maxSteps){

            System.out.println(String.format("Iteration %d, states=%d, history=%d", step, currentStates.size(), history.size()));

            Collection<GameState> nextStates = search(setting, history, currentStates);

            if (nextStates.size()==0){
                gameFailed = true;
            } else {
                for (GameState nextState : nextStates) {
                    if (GameUtils.isEnd(setting, nextState)) {
                        gameCompleted = true;
                        endState = nextState;
                    }
                }

            }

            if (nextStates.size() > triggerWidth){
                // try reduce it
                System.out.println("Try to reduce width from " + nextStates.size() + " to " + maxWidth);
                currentStates = GameUtils.reduceByScore(setting, nextStates, maxWidth);

            } else {
                currentStates = nextStates;

            }


            // with 5 out
            int i = 0;
            int size = currentStates.size();
            int[] dumps = new int[]{
                (int) (size * 0),
                (int) (size * 0.25),
                (int) (size * 0.5),
                (int) (size * 0.75)
            };

            debugOutput.append(String.format("Iteration %d, states=%d, history=%d<br/>", step, currentStates.size(), history.size()));
            debugOutput.append("<div style='display: flex'>");
            for (GameState nextState : currentStates) {
                for (int dump : dumps) {
                    if (i==dump){
                        debugOutput.append(GameUtils.toHtml(setting, nextState));
                    }
                }
                i++;
            }
            debugOutput.append("</div>");
            logDebug(debugOutput);


        }

        List<GameState> steps = retrieveSteps(endState);
        if (steps.size() > 0 ){
            debugOutput.append("Completed in " + steps.size() + " steps<br/>");
            for (GameState gameState : steps) {
                debugOutput.append(GameUtils.toHtml(setting, gameState));
            }
        } else {
            debugOutput.append("FAILED");
        }


        if (gameCompleted) System.out.println("Game completed");
        else if (gameFailed) System.out.println("Game failed, no possible moves");
        else System.out.println("Game failed, exist max move(s) ("+maxSteps+")");

        logDebug(debugOutput);

        return steps;
    }

    private void logDebug(StringBuilder debugLog) {
        if (debug){
            try {
                FileUtils.writeStringToFile(new File("./debug.html"), "<html><body>"+debugLog.toString()+"</body></html>", "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<GameState> retrieveSteps(GameState state) {
        List<GameState> steps = new ArrayList<>();
        while(state!=null){
            steps.add(state);
            state = state.getPreviousState();
        }
        Collections.reverse(steps);
        return steps;
    }

    private Collection<GameState> search(GameSetting setting, GameHistory history, Collection<GameState> states) {

        Collection<GameState> nextStates = new LinkedList<>();

        for (GameState state : states) {
            Set<GameState> currNextStates = GameUtils.nextMove(setting, history, state);
            for (GameState currNextState : currNextStates) {
                GameUtils.updateMovable(setting, currNextState);

                if (!GameUtils.inHistory(history, currNextState)
                        && !GameUtils.isDeadSpot(setting, currNextState)) {

                    nextStates.add(currNextState);
                }
                GameUtils.addHistory(history, currNextState);
            }
        }

        return nextStates;

    }


}
