package code.it;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameUtils {

    private static long checkDead;
    private static long checkPossible;
    private static long checkEnd;
    private static long calculateNextMoveSingle;
    private static long updateMovable;
    private static long checkHistory;
    private static long addHistory;
    private static long calculateAverage;
    private static long createList;
    private static long sorting;

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
        System.out.println(String.format("checkHistory = %.2f ms", checkHistory/1_000_000.0));
        System.out.println(String.format("addHistory = %.2f ms", addHistory/1_000_000.0));
        System.out.println(String.format("calculateAverage = %.2f ms", calculateAverage/1_000_000.0));
        System.out.println(String.format("createList = %.2f ms", createList/1_000_000.0));
        System.out.println(String.format("sorting = %.2f ms", sorting/1_000_000.0));
        System.out.println("====================");
        System.out.println(String.format("Total Time Taken = %d ms", System.currentTimeMillis() - startTimeMs));
        System.out.println("--------------------");
    }

    public static boolean isDeadSpot(GameSetting gameSetting, Spot spot) {

        if (gameSetting.isGoal(spot)){
            return false;
        }

        // un-recoverable side
        if (!isAlive(gameSetting, spot, -1, 0, 0, -1) && !isAlive(gameSetting, spot, 1, 0, 0, -1)){
            return true;
        } else if (!isAlive(gameSetting, spot, -1, 0, 0, 1) && !isAlive(gameSetting, spot, 1, 0, 0, 1)){
            return true;
        } else if (!isAlive(gameSetting, spot, 0, -1, -1, 0) && !isAlive(gameSetting, spot, 0, 1, -1, 0)){
            return true;
        } else if (!isAlive(gameSetting, spot, 0, -1, 1, 0) && !isAlive(gameSetting, spot, 0, 1, 1, 0)){
            return true;
        }

        // conner
        if (!gameSetting.isSpace(spot.top()) && !gameSetting.isSpace(spot.right())) {
            return true;
        } else if (!gameSetting.isSpace(spot.bottom()) && !gameSetting.isSpace(spot.right())) {
            return true;
        } else if (!gameSetting.isSpace(spot.top()) && !gameSetting.isSpace(spot.left())) {
            return true;
        } else if (!gameSetting.isSpace(spot.bottom()) && !gameSetting.isSpace(spot.left())) {
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

    private static double distance(Spot spot1, Spot spot2){
        double dr = spot1.getRow() - spot2.getRow();
        double dc = spot1.getCol() - spot2.getCol();
        double distance = dr * dr + dc * dc;
        return distance;
    }

    public static int score(GameSetting setting, GameState state){

        long start = System.nanoTime();
        try {

            int score = 0;
            for (Spot box : state.boxes(setting)) {
                score += setting.getScroe(box);
            }
            return score;
        } finally {
            calculateAverage += System.nanoTime() - start;
        }
    }

    public static Collection<GameState> reduceByScore(GameSetting setting, Collection<GameState> states, int max){
        double total = 0;
        for (GameState nextState : states) {
            int distance = GameUtils.score(setting, nextState);
            total += distance;
            nextState.setScore(distance);
        }
        System.out.println(String.format("Average score = %.3f", total / states.size()));

        long start = System.nanoTime();
        List<GameState> sorted = new ArrayList<>(states);
        createList += System.nanoTime() - start;

        long startSort = System.nanoTime();
        Collections.sort(sorted);
        sorting += System.nanoTime() - startSort;

        return sorted.subList(0, max);
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

    private static Spot[][] deadGroups(Spot spot){
        return new Spot[][]{
                new Spot[]{spot.topLeft(), spot.top(), spot.left()},
                new Spot[]{spot.top(), spot.topRight(), spot.right()},
                new Spot[]{spot.right(), spot.bottomRight(), spot.bottom()},
                new Spot[]{spot.bottom(), spot.bottomLeft(), spot.left()},


//                new Spot[]{
//                        spot.left(), spot.topLeft(), spot.bottomLeft(),
//                        spot.topRight(), spot.bottomRight(),
//                        spot.right().topRight(), spot.right().right(), spot.right().bottomRight()
//                },
//                new Spot[]{
//                        spot.left().left(), spot.left().topLeft(), spot.left().bottomLeft(),
//                        spot.topLeft(), spot.bottomLeft(),
//                        spot.topRight(), spot.right(), spot.bottomRight()
//                },
        };
    }

    public static boolean isDeadSpot(GameSetting setting, GameState state){
        long start = System.nanoTime();
        try {
            List<Spot> boxes = state.boxes(setting);
            for (Spot box : boxes) {

                if (!setting.isGoal(box)) {

//                    Spot[] spots = box.possibleMoves();
//                    if (!setting.isSpace(spots[0]) && !setting.isSpace(spots[1])) {
//                        return true;
//                    } else if (!setting.isSpace(spots[1]) && !setting.isSpace(spots[2])) {
//                        return true;
//                    } else if (!setting.isSpace(spots[2]) && !setting.isSpace(spots[3])) {
//                        return true;
//                    } else if (!setting.isSpace(spots[3]) && !setting.isSpace(spots[0])) {
//                        return true;
//                    }

                    if (setting.isDeadZone(box)){
                        return true;
                    }

                    // group of box causing dead
                    for (Spot[] deadZone : deadGroups(box)) {
                        boolean dead = true;
                        for (Spot spot : deadZone) {
                            if (!spot.isValid()){
                                dead = false;
                                break;
                            }

                            if (setting.isSpace(spot) && !state.isBox(setting, spot)){
                                dead = false;
                                break;
                            }
                        }
                        if (dead){
                            return true;
                        }
                    }

                    // blocking hallway
                    if (setting.notSpace(box.top()) && setting.notSpace(box.bottom())){
                        if (state.isBox(setting, box.left())){
                            if (state.notMovable(setting, box.topLeft()) && state.notMovable(setting, box.bottomLeft())){
                                return true;
                            }
                        } else if (state.isBox(setting, box.right())){
                            if (state.notMovable(setting, box.topRight()) && state.notMovable(setting, box.bottomRight())){
                                return true;
                            }
                        }
                    } else if (setting.notSpace(box.left()) && setting.notSpace(box.right())){
                        if (state.isBox(setting, box.top())){
                            if (state.notMovable(setting, box.topLeft()) && state.notMovable(setting, box.topRight())){
                                return true;
                            }
                        } else if (state.isBox(setting, box.bottom())){
                            if (state.notMovable(setting, box.bottomLeft()) && state.notMovable(setting, box.bottomRight())){
                                return true;
                            }
                        }
                    }

                }
            }
            return false;
        } finally {
            checkDead += System.nanoTime() - start;
        }
    }

    public static List<Spot> possibleSpots(GameSetting gameSetting, GameState state, Spot box) {

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
            if (GameUtils.isDeadSpot(setting, spot)) {
                setting.setDeadZone(spot);
                System.out.println("Found deadzone: " + spot);
            }
        }


        // calculate distance
        GameUtils.calculateScore(setting);

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
        return "<div style='background-color: "+color+"; top: "+spot.getRow()*10+"px; left: "+spot.getCol()*10+"px; width:10px; height:10px; position: absolute; opacity: "+opacity+"; box-sizing: border-box; "+borderStyle+"'></div>";
    }

    private static String toHtml(GameSetting setting, Spot spot){
        int label = setting.getScroe(spot);
        return "<div style='color: #000; line-height:1; font-size:9px; top: "+spot.getRow()*10+"px; left: "+spot.getCol()*10+"px; width:10px; height:10px; text-align: center; position: absolute; opacity: 0.8; box-sizing: border-box;'>"+label+"</div>";
    }

    public static String toHtml(GameSetting settings, GameState state){
        return toHtml(settings, state, false);

    }

    public static String toHtml(GameSetting settings, GameState state, boolean includeScore){


        String wall = settings.walls().stream()
                .map(s -> toHtml(s, "black", 0))
                .collect(Collectors.joining("\n"));

        String space = settings.spaces().stream()
                .map(s -> toHtml(s, "white", 0))
                .collect(Collectors.joining("\n"));

        String deadzone = settings.deadzone().stream()
                .map(s -> toHtml(s, "red", 0, 0.4))
                .collect(Collectors.joining("\n"));

        String goal = settings.goals().stream()
                .map(s -> toHtml(s, "yellow", 0))
                .collect(Collectors.joining("\n"));

        String boxes = state.boxes(settings).stream()
                .map(s -> toHtml(s, "orange", 1))
                .collect(Collectors.joining("\n"));

        String score = "";
        if (includeScore) {
            score = settings.spaces().stream()
                    .map(s -> toHtml(settings, s))
                    .collect(Collectors.joining("\n"));
        }

        String player = toHtml(state.getPlayer(), "blue", 0);

        String distance = "<div>"+state.getScore()+"</div>";

        return "<div style='position:relative; width: "+settings.cols()*10+"px; height: "+settings.rows()*10+"px'>" +
                space + wall + deadzone + goal + boxes + player + distance + score +
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



    private static void calculateScore(GameSetting setting, Spot spot, int score){

        if (score > setting.getScroe(spot)){
            setting.setScore(spot, Math.max(0, score));

            for (Spot next : spot.possibleMoves()) {
                if (setting.isSpace(spot) && !setting.isDeadZone(spot)){
                    int reduction = setting.isGoal(next) ? -1 : -3;
                    calculateScore(setting, next, score + reduction);
                }
            }
        } else {
            // do nothing
        }

    }

    public static void calculateScore(GameSetting setting) {

        int baseScroe = 40;

        for (Spot spot : setting.goals()) {
            calculateScore(setting, spot, baseScroe);
        }

        for (Spot spot : setting.goals()) {
            int additionalScore = 0;
            for (Spot possibleNextSpot : spot.possibleMoves()) {
                if (setting.isWall(possibleNextSpot)){
                    additionalScore += 5;
                }
            }
            setting.setScore(spot, baseScroe + additionalScore);
            calculateScore(setting, spot, baseScroe);
        }

    }



    public static List<GameState> searchPlayerMoves(GameSetting setting, GameState from, GameState to){
        Spot start = from.getPlayer();
        Spot end = to.getPlayerPrevious();
        if (start.equals(end)) {
            return Collections.emptyList();
        }
        List<Spot> playerMoves = searchMove(setting, from, start, end);
        playerMoves.remove(0);
        List<GameState> states = new LinkedList<>();
        for (Spot moveTo : playerMoves) {
            states.add(from.copy().setPlayer(moveTo));
        }
        return states;
    }

    public static List<Spot> searchMove(GameSetting setting, GameState state, Spot from, Spot to){

        Set<Spot> historicalMoves = new HashSet<>();
        historicalMoves.add(from);

        Spot last = searchMove(setting, state, historicalMoves, Arrays.asList(from), to);
        if (last==null){
            throw new IllegalStateException("Unable to from path from " + from + " to " + to);
        }

        List<Spot> moves = new LinkedList<>();
        moves.add(last);
        while(last.getPrevious()!=null){
            moves.add(last.getPrevious());
            last = last.getPrevious();
        }
        Collections.reverse(moves);

        int lastIdx = moves.size() - 1;
        if (!from.equals(moves.get(0))) throw new IllegalStateException("Invalid from, expect " + from + ", got " + moves.get(0));
        if (!to.equals(moves.get(lastIdx))) throw new IllegalStateException("Invalid to, expect " + to + ", got " + moves.get(lastIdx));

        return moves;

    }

    private static Spot searchMove(GameSetting setting, GameState state, Set<Spot> historicalMoves, List<Spot> curr, Spot to){

        if (curr.isEmpty()){
            return null;
        }


        while(!curr.isEmpty()){

            Set<Spot> nextToSearch = new HashSet<>();
            for (Spot spot : curr) {
                for (Spot nextSpot : spot.possibleMoves()) {
                    if (!historicalMoves.contains(nextSpot) && setting.isSpace(nextSpot) && !state.isBox(setting, nextSpot)){
                        nextSpot.setPrevious(spot);

                        if (nextSpot.equals(to)){
                            return nextSpot;
                        }
                        nextToSearch.add(nextSpot);
                    }
                }
            }

            historicalMoves.addAll(nextToSearch);

            curr = new ArrayList<>(nextToSearch);

        }

        return null;


    }


    public static List<String> translateToMove(List<GameState> steps){

        List<String> moves = new ArrayList<>();
        GameState prev = null;
        for (GameState step : steps) {
            if (prev!=null){
                moves.add(prev.getPlayer().moveTo(step.getPlayer()));
            }
            prev = step;
        }
        return moves;

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
