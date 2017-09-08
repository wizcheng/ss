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
    private static long checkHistory = 0;
    private static long addHistory = 0;

    private static long startTimeMs = System.currentTimeMillis();
    private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    static  {
        service.scheduleAtFixedRate(() -> {
            printTimes();
        }, 30, 60, TimeUnit.SECONDS);
    }

    public static boolean isDead(GameSetting gameSetting, Spot spot) {
        //
        if (!isAlive(gameSetting, spot, -1, 0, 0, -1) && !isAlive(gameSetting, spot, 1, 0, 0, -1)){
            return true;
        } else if (!isAlive(gameSetting, spot, -1, 0, 0, 1) && !isAlive(gameSetting, spot, 1, 0, 0, 1)){
            return true;
        } else if (!isAlive(gameSetting, spot, 0, -1, -1, 0) && !isAlive(gameSetting, spot, 0, 1, -1, 0)){
            return true;
        } else if (!isAlive(gameSetting, spot, 0, -1, 1, 0) && !isAlive(gameSetting, spot, 0, 1, 1, 0)){
            return true;
        }

        return false;
    }

    private static boolean isAlive(GameSetting gameSetting, Spot spot, int rowOffset, int colOffset, int rowOffsetToCheck, int colOffsetToCheck){
        // check vertical
        Spot spotToCheck = new Spot(spot.getRow() + rowOffsetToCheck, spot.getCol() + colOffsetToCheck);
        if (gameSetting.isSpace(spotToCheck) || gameSetting.isGoal(spot)){
            return true;
        } else {
            // try to move
            Spot spotToMoveTo = new Spot(spot.getRow() + rowOffset, spot.getCol() + colOffset);
            if (!gameSetting.isSpace(spotToMoveTo)){
                return false;
            } else {
                return isAlive(gameSetting, spotToMoveTo, rowOffset, colOffset, rowOffsetToCheck, colOffsetToCheck);
            }
        }
    }

    public static void printTimes(){
        System.out.println("--------------------");
        System.out.println(String.format("checkDead = %.2f ms", checkDead/1_000_000.0));
        System.out.println(String.format("checkPossible = %.2f ms", checkPossible/1_000_000.0));
        System.out.println(String.format("checkEnd = %.2f ms", checkEnd/1_000_000.0));
        System.out.println(String.format("calculateNextMoveSingle = %.2f ms", calculateNextMoveSingle/1_000_000.0));
        System.out.println(String.format("updateMovable = %.2f ms", updateMovable/1_000_000.0));
        System.out.println(String.format("checkHistory = %.2f ms", checkHistory/1_000_000.0));
        System.out.println(String.format("addHistory = %.2f ms", addHistory/1_000_000.0));
        System.out.println("====================");
        System.out.println(String.format("Total Time Taken = %d ms", System.currentTimeMillis() - startTimeMs));
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

    private static Spot[][] deadZones(Spot spot){
        return new Spot[][]{
                new Spot[]{spot.topLeft(), spot.top(), spot.left()},
                new Spot[]{spot.top(), spot.topRight(), spot.right()},
                new Spot[]{spot.right(), spot.bottomRight(), spot.bottom()},
                new Spot[]{spot.bottom(), spot.bottomLeft(), spot.left()}
        };
    }

    public static boolean isDead(GameSetting setting, GameState state){
        long start = System.nanoTime();
        try {
            List<Spot> boxes = state.boxes(setting);
            for (Spot box : boxes) {

                if (!setting.isGoal(box)) {

                    Spot[] spots = box.possibleMoves();
                    if (!setting.isSpace(spots[0]) && !setting.isSpace(spots[1])) {
                        return true;
                    } else if (!setting.isSpace(spots[1]) && !setting.isSpace(spots[2])) {
                        return true;
                    } else if (!setting.isSpace(spots[2]) && !setting.isSpace(spots[3])) {
                        return true;
                    } else if (!setting.isSpace(spots[3]) && !setting.isSpace(spots[0])) {
                        return true;
                    }


                    for (Spot[] deadZone : deadZones(box)) {
                        if ((!setting.isSpace(deadZone[0]) || state.isBox(setting, deadZone[0]))
                                && (!setting.isSpace(deadZone[1]) || state.isBox(setting, deadZone[1]))
                                && (!setting.isSpace(deadZone[2]) || state.isBox(setting, deadZone[2])) ){
                            return true;
                        }
                    }

                    if (setting.isDeadZone(box)){
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
        Board deadzone = new Board(rows, cols);
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
        GameSetting setting = new GameSetting(rows, cols, goal, space, wall, deadzone);

        // mark deadzone
        for (Spot spot : setting.spaces()) {
            if (GameUtils.isDead(setting, spot)) {
                setting.setDeadZone(spot);
                System.out.println("Found deadzone: " + spot);
            }
        }



        return new Game(
                setting,
                initialState
        );
    }


    private static void updateMovable(GameSetting gameSetting, GameState state, Spot current) {

        Spot[] spots = current.possibleMoves();
        for (Spot spot : spots) {
            if (state.isMovable(gameSetting, spot)){
                // already visit
                // about
            } else {

                if (spot.isValid() && gameSetting.isSpace(spot) && !state.isBox(gameSetting, spot)){
                    state.setMovable(gameSetting, spot, true);
                    updateMovable(gameSetting, state, spot);
                } else {
                    // not movable
                    // end
                }
            }
        }

    }

    public static boolean inHistory(GameHistory history, GameState state) {
        long start = System.nanoTime();
        try {
            return history.exist(state);
        } finally {
            checkHistory += System.nanoTime() - start;
        }
    }

    public static void addHistory(GameHistory history, GameState state) {
        long start = System.nanoTime();
        try {
            history.add(state);
        } finally {
            addHistory += System.nanoTime() - start;
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
        return toHtml(spot, color, border, 0.8);
    }


    private static String toHtml(Spot spot, String color, int border, double opacity){
        String borderStyle = border > 0 ? "border: solid 1px brown" : "";
        return "<div style='background-color: "+color+"; top: "+spot.getRow()*10+"; left: "+spot.getCol()*10+"; width:10px; height:10px; position: absolute; opacity: "+opacity+"; box-sizing: border-box; "+borderStyle+"'></div>";
    }

    public static String toHtml(GameSetting settings, GameState state){


        String wall = settings.walls().stream()
                .map(s -> toHtml(s, "black", 0))
                .collect(Collectors.joining());

        String space = settings.spaces().stream()
                .map(s -> toHtml(s, "white", 0))
                .collect(Collectors.joining());

        String deadzone = settings.deadzone().stream()
                .map(s -> toHtml(s, "red", 0, 0.4))
                .collect(Collectors.joining());

        String goal = settings.goals().stream()
                .map(s -> toHtml(s, "yellow", 0))
                .collect(Collectors.joining());

        String boxes = state.boxes(settings).stream()
                .map(s -> toHtml(s, "orange", 1))
                .collect(Collectors.joining());

        String player = toHtml(state.getPlayer(), "blue", 0);

        return "<div style='position:relative; width: "+settings.cols()*10+"px; height: "+settings.rows()*10+"px'>" +
                space + wall + deadzone + goal + boxes + player +
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
