package code.it;

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

                List<Spot> nonSpace = box.possibleMoves().stream()
                        .filter(s -> !setting.isSpace(s))
                        .collect(Collectors.toList());
                if (nonSpace.size()==2 && nonSpace.get(0).isDiagonal(nonSpace.get(1))) {
                    System.out.println("box at " + box + " is dead");
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


        int rows = board.size();
        int cols = board.get(0).length();

        Board box = new Board(rows, cols);
        Board movable = new Board(rows, cols);
        Board space = new Board(rows, cols);
        Board goal = new Board(rows, cols);
        Board wall = new Board(rows, cols);

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
                    case '*':
                        space.add(spot);
                        goal.add(spot);
                        break;
                    case 'o':
                        space.add(spot);
                        box.add(spot);
                        break;
                    case 'b':
                        space.add(spot);
                        movable.add(spot);
                        break;
                    case ' ':
                        space.add(spot);
                        break;
                    case '-':
                        wall.add(spot);
                        break;
                    case 'x':
                        break;
                    default:
                        throw new IllegalStateException("Unknown! " + digit);
                }
            }
        }
        return new Game(
                new GameSetting(rows, cols, goal, space, wall),
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

    public static String toString(List<List<String>> view){
        return view.stream()
                .map(row -> row.stream().collect(Collectors.joining()))
                .collect(Collectors.joining("\n"));
    }



    private static String toHtml(Spot spot, String color, int border){
        String borderStyle = border > 0 ? "border: solid 1px brown" : "";
        return "<div style='background-color: "+color+"; top: "+spot.getRow()*10+"; left: "+spot.getCol()*10+"; width:10px; height:10px; position: absolute; opacity: 0.7; box-sizing: border-box; "+borderStyle+"'></div>";
    }

    public static String toHtml(GameSetting settings, GameState state){


        String wall = settings.walls().stream()
                .map(s -> toHtml(s, "black", 0))
                .collect(Collectors.joining());

        String space = settings.spaces().stream()
                .map(s -> toHtml(s, "white", 0))
                .collect(Collectors.joining());

        String goal = settings.goals().stream()
                .map(s -> toHtml(s, "yellow", 0))
                .collect(Collectors.joining());

        String boxes = state.boxes().stream()
                .map(s -> toHtml(s, "orange", 1))
                .collect(Collectors.joining());

        return "<div style='position:relative; width: "+settings.cols()*10+"px; height: "+settings.rows()*10+"px'>" +
                space + wall + goal + boxes +
                "</div>";


    }

    public static List<List<String>> toView(GameSetting setting, GameState state) {
        List<List<String>> results = new ArrayList<>();
        for (int row = 0; row < setting.rows(); row++) {
            ArrayList<String> currRow = new ArrayList<>();
            results.add(currRow);
            for (int col = 0; col < setting.cols(); col++) {

                Spot spot = new Spot(row, col);
                String digit = "x";
                if (setting.isWall(spot)){
                    digit = "-";
                } else if (state.isBox(spot)){
                    digit = "o";
                } else if (setting.isGoal(spot)) {
                    digit = "*";
                } else if (setting.isSpace(spot)) {
                    digit = " ";
                }

                currRow.add(digit);
            }
        }
        return results;
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
