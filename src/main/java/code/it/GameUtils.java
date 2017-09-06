package code.it;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameUtils {

    private static boolean isPossible(GameSetting setting, GameState state, Spot box, Spot spot){
        return state.isMovable(box.otherSideOf(spot))
                && setting.isSpace(spot)
                && !state.isBox(spot);
    }

    public static boolean isDead(GameSetting setting, GameState state){
        List<Spot> boxes = state.boxes();
        for (Spot box : boxes) {

            if (!setting.isGoal(box)){

                long possibleMoves = possibleSpots(setting, state, box).count();

                if (possibleMoves == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Stream<Spot> possibleSpots(GameSetting setting, GameState state, Spot box) {
        return box.possibleMoves()
                .stream()
                .filter(s -> !state.isBox(s))
                .filter(s -> setting.isSpace(s))
                .filter(s -> isPossible(setting, state, box, s));
    }

    public static boolean isEnd(GameSetting setting, GameState state){
        return !state.boxes()
                .stream()
                .anyMatch(box -> !setting.isGoal(box));
    }

    public static List<GameState> nextMove(GameSetting setting, GameHistory history, GameState state){

        return state.boxes()
                .stream()
                .map(box -> {
                    return possibleSpots(setting, state, box)
                            .map(newSpot -> state.copy().move(box, newSpot))
                            .map(s -> s.setPreviousState(state))
                            .collect(Collectors.toList());
                })
                .flatMap(l -> l.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public static GameHistory createHistory(){
        return new GameHistory();
    }

    public static Game init(List<String> board){

        Board box = new Board(10, 10);
        Board movable = new Board(10, 10);
        Board space = new Board(10, 10);
        Board goal = new Board(10, 10);

//        #   (hash)      Wall
//        .	(period)	Empty goal
//        @	(at)    	Player on floor
//        +	(plus)		Player on goal
//        $	(dollar)	Box on floor
//        *	(asterisk)	Box on goal

        for (int row = 0; row < board.size(); row++) {
            for (int col = 0; col < board.get(row).length(); col++) {
                char digit = board.get(row).charAt(col);
                Spot spot = new Spot(row, col);
                switch (digit){
                    case '#':
                        break;
                    case '.':
                        space.add(spot);
                        goal.add(spot);
                        break;
                    case '@':
                        space.add(spot);
                        movable.add(spot);
                        break;
                    case '+':
                        space.add(spot);
                        movable.add(spot);
                        goal.add(spot);
                        break;
                    case '$':
                        space.add(spot);
                        box.add(spot);
                        break;
                    case '*':
                        space.add(spot);
                        box.add(spot);
                        goal.add(spot);
                        break;
                    case ' ':
                        space.add(spot);
                        break;
                    default:
                        throw new IllegalStateException("Unknown! " + digit);
                }
            }
        }
        return new Game(
                new GameSetting(goal, space),
                new GameState(box, movable)
        );
    }

    public static GameState updateMovable(GameSetting setting, GameState state) {

        final GameState nextState = state;
        GameState prevState;
        int iteration = 0;
        do {

            prevState = nextState.copy();
            final GameState toTest = prevState;

            setting.spaces()
                    .forEach(s -> nextState.setMovable(s, false));

            setting.spaces()
                    .stream()
                    .filter(s -> !state.haveBox(s))
                    .filter(s -> toTest.isMovable(s) || s.possibleMoves().stream().anyMatch(p -> toTest.isMovable(p)))
                    .forEach(s -> nextState.setMovable(s, true));

            if (iteration++ > 1000000){
                throw new IllegalStateException("Invalid implementation");
            }

        } while (!prevState.equals(nextState));

        return nextState;
    }


    public static class Game {

        public GameSetting setting;
        public GameState initialState;

        public Game(GameSetting setting, GameState initialState) {
            this.setting = setting;
            this.initialState = initialState;
        }
    }





}
