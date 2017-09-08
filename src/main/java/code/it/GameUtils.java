package code.it;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameUtils {

    private static long checkDead = 0;
    private static long checkPossible = 0;
    private static long checkEnd = 0;
    private static long calculateNextMoveSingle = 0;
    private static long updateMovable = 0;
    private static long startTimeMs = System.currentTimeMillis();

    private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    static  {
        service.scheduleAtFixedRate(() -> {
            printTimes();
        }, 30, 60, TimeUnit.SECONDS);
    }

    public static void printTimes(){
        System.out.println("--------------------");
        System.out.println(String.format("checkDead = %.2f ms", checkDead/1_000_000.0));
        System.out.println(String.format("checkPossible = %.2f ms", checkPossible/1_000_000.0));
        System.out.println(String.format("checkEnd = %.2f ms", checkEnd/1_000_000.0));
        System.out.println(String.format("calculateNextMoveSingle = %.2f ms", calculateNextMoveSingle/1_000_000.0));
        System.out.println(String.format("updateMovable = %.2f ms", updateMovable/1_000_000.0));
        System.out.println("====================");
        System.out.println(String.format("Total Time Taken = %.2f ms", System.currentTimeMillis() - startTimeMs));
        System.out.println("--------------------");
    }

    private static boolean isPossible(GameSetting gameSetting, GameState state, Spot box, Spot spot){
        long start = System.nanoTime();
        try {
            return state.isMovable(gameSetting, box.otherSideOf(spot))
                    && gameSetting.isSpace(spot)
                    && !state.isBox(gameSetting, spot);
        } finally {
            checkPossible += System.nanoTime() - start;
        }
    }

    public static boolean isDead(GameSetting setting, GameState state){
        long start = System.nanoTime();
        try {
            List<Spot> boxes = state.boxes(setting);
            for (Spot box : boxes) {

                if (!setting.isGoal(box)) {

                    List<Spot> nonSpace = box.possibleMoves().stream()
                            .filter(s -> !setting.isSpace(s))
                            .collect(Collectors.toList());
                    if (nonSpace.size() == 2 && nonSpace.get(0).isDiagonal(nonSpace.get(1))) {
//                    System.out.println("box at " + box + " is dead");
                        return true;
                    }
                }
            }
            return false;
        } finally {
            checkDead += System.nanoTime() - start;
        }
    }

    private static List<Spot> possibleSpots(GameSetting gameSetting, GameState state, Spot box) {

        List<Spot> nextSpots = new ArrayList<>(4);
        for (Spot spot : box.possibleMoves()) {
            if (!state.isBox(gameSetting, spot)
                    && gameSetting.isSpace(spot)
                    && isPossible(gameSetting, state, box, spot)){
                nextSpots.add(spot);
            }
        }
        return nextSpots;
    }

    public static boolean isEnd(GameSetting setting, GameState state){
        long start = System.nanoTime();
        try {
            return !state.boxes(setting)
                    .stream()
                    .anyMatch(box -> !setting.isGoal(box));
        } finally {
            checkEnd += System.nanoTime() - start;
        }
    }

    public static Set<GameState> nextMove(GameSetting gameSetting, GameHistory history, GameState state){
        long start = System.nanoTime();
        try {
            Set<GameState> nextMoves = new HashSet<>(10000);
            for (Spot box : state.boxes(gameSetting)) {
                List<Spot> nextSpots = possibleSpots(gameSetting, state, box);
                for (Spot nextSpot : nextSpots) {
                    GameState nextState = state.copy().move(gameSetting, box, nextSpot);
                    nextState.setPreviousState(state);
                    nextMoves.add(nextState);
                }
            }
            return nextMoves;
        } finally {
            calculateNextMoveSingle += System.nanoTime() - start;
        }
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
        Spot player = null;

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
                        space.add(cols, spot);
                        goal.add(cols, spot);
                        break;
                    case 'o':
                        space.add(cols, spot);
                        box.add(cols, spot);
                        break;
                    case 'b':
                        space.add(cols, spot);
                        movable.add(cols, spot);
                        player = spot;
                        break;
                    case ' ':
                        space.add(cols, spot);
                        break;
                    case '-':
                        wall.add(cols, spot);
                        break;
                    case 'x':
                        break;
                    default:
                        throw new IllegalStateException("Unknown! " + digit);
                }
            }
        }
        GameState initialState = new GameState(box, movable);
        initialState.setPlayer(player);
        return new Game(
                new GameSetting(rows, cols, goal, space, wall),
                initialState
        );
    }


    private static void updateMovable(GameSetting gameSetting, GameState state, Spot current) {

        List<Spot> spots = current.possibleMoves();
        for (Spot spot : spots) {
            if (state.isMovable(gameSetting, spot)){
                // already visit
                // about
            } else {

                if (gameSetting.isSpace(spot) && !state.isBox(gameSetting, spot)){
                    state.setMovable(gameSetting, spot, true);
                    updateMovable(gameSetting, state, spot);
                } else {
                    // not movable
                    // end
                }
            }
        }

    }

    public static void updateMovable(GameSetting gameSetting, GameState state) {

        long start = System.nanoTime();
        try {
            state.clearMovable();
            Spot player = state.getPlayer();
            state.setMovable(gameSetting, player, true);
            updateMovable(gameSetting, state, player);
        } finally {
            updateMovable += System.nanoTime() - start;
        }
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

        String boxes = state.boxes(settings).stream()
                .map(s -> toHtml(s, "orange", 1))
                .collect(Collectors.joining());

        String player = toHtml(state.getPlayer(), "blue", 0);

        return "<div style='position:relative; width: "+settings.cols()*10+"px; height: "+settings.rows()*10+"px'>" +
                space + wall + goal + boxes + player +
                "</div>";


    }

    public static List<List<String>> toView(GameSetting gameSetting, GameState state) {
        List<List<String>> results = new ArrayList<>();
        for (int row = 0; row < gameSetting.rows(); row++) {
            ArrayList<String> currRow = new ArrayList<>();
            results.add(currRow);
            for (int col = 0; col < gameSetting.cols(); col++) {

                Spot spot = new Spot(row, col);
                String digit = "x";
                if (gameSetting.isWall(spot)){
                    digit = "-";
                } else if (state.isBox(gameSetting, spot)){
                    digit = "o";
                } else if (gameSetting.isGoal(spot)) {
                    digit = "*";
                } else if (gameSetting.isSpace(spot)) {
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
