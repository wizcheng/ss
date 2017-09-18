package code.it;

import code.it.strategy.BFS;
import code.it.strategy.ScoreBased;
import code.it.strategy.Strategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) throws IOException {

        // https://gitlab.com/codeitsuissehk2017/sokoban/blob/master/resource/maps/map27.json

        List<String> lines = new ArrayList<>();

        // level 1
//        lines.add("xx---xxx");
//        lines.add("xx-*-xxx");
//        lines.add("xx- ----");
//        lines.add("---o o*-");
//        lines.add("-*ob  --");
//        lines.add("----o --");
//        lines.add("xxx-*-xx");
//        lines.add("xxx---xx");

        // level 8
//        lines.add("xxx------x");
//        lines.add("x---    -x");
//        lines.add("--* o-- --");
//        lines.add("-**o o  b-");
//        lines.add("-** o o --");
//        lines.add("------  -x");
//        lines.add("xxxxx----x");

        // level 13
//        lines.add("xx--------");
//        lines.add("xx-   -* -");
//        lines.add("x--  o***-");
//        lines.add("x-  o -**-");
//        lines.add("-- --o-o--");
//        lines.add("-   o    -");
//        lines.add("-   -  o -");
//        lines.add("-------b -");
//        lines.add("xxxxxx----");

        // level 15
//        lines.add("xxx------xx");
//        lines.add("xxx- ***-xx");
//        lines.add("----****-xx");
//        lines.add("-  ---o ---");
//        lines.add("- o o  oo -");
//        lines.add("-b o o    -");
//        lines.add("-   ---   -");
//        lines.add("-----x-----");


        // level 16
        lines.add("--------x");
        lines.add("-      -x");
        lines.add("- -oo  -x");
        lines.add("- ***- -x");
        lines.add("--***o --");
        lines.add("x- -- o -");
        lines.add("x-o  o  -");
        lines.add("x-  -  b-");
        lines.add("x--------");



        // level 20
//        lines.add("xxxxxx---------");
//        lines.add("xxxxxx-       -");
//        lines.add("xxxxxx- - - - -");
//        lines.add("xxxxxx-  o o- -");
//        lines.add("-------   o   -");
//        lines.add("-**-  -- o o- -");
//        lines.add("-**   -- o o  -");
//        lines.add("-**-  -- ------");
//        lines.add("-**- - o o -xxx");
//        lines.add("-**     o  -xxx");
//        lines.add("-  --- b ---xxx");
//        lines.add("----x-----xxxxx");


//        lines.add("xxx------xx");
//        lines.add("xxx- ***-xx");
//        lines.add("----****-xx");
//        lines.add("-  ---o ---");
//        lines.add("- o o  oo -");
//        lines.add("-b o o    -");
//        lines.add("-   ---   -");
//        lines.add("-----x-----");

//        lines.add("x----x");
//        lines.add("--  -x");
//        lines.add("-bo -x");
//        lines.add("--o --");
//        lines.add("-- o -");
//        lines.add("-*o  -");
//        lines.add("-**o*-");
//        lines.add("------");

//        lines.add("--------x");
//        lines.add("-      -x");
//        lines.add("- -oo  -x");
//        lines.add("- ***- -x");
//        lines.add("--***o --");
//        lines.add("x- -- o -");
//        lines.add("x-o  o  -");
//        lines.add("x-  -  b-");
//        lines.add("x--------");

        // level 21
//        lines.add("xxxxx-----xx");
//        lines.add("----x-   --x");
//        lines.add("-  ---o-  -x");
//        lines.add("- o ****- --");
//        lines.add("-  --****  -");
//        lines.add("-    o--o- -");
//        lines.add("- o-o   o  -");
//        lines.add("-- b ---o- -");
//        lines.add("x-----x-   -");
//        lines.add("xxxxxxx-----");

        // level 25
//        lines.add("------------xx");
//        lines.add("-**  -     ---");
//        lines.add("-**  - o  o  -");
//        lines.add("-**  -o----  -");
//        lines.add("-**    b --  -");
//        lines.add("-**  - -  o --");
//        lines.add("------ --o o -");
//        lines.add("xx- o  o o o -");
//        lines.add("xx-    -     -");
//        lines.add("xx------------");

        // 27
//        lines.add("xx----xxxxxxxxxx");
//        lines.add("xx-  -----------");
//        lines.add("xx-    o   o o -");
//        lines.add("xx- o- o -  o  -");
//        lines.add("xx-  o o  -    -");
//        lines.add("--- o- -  ---- -");
//        lines.add("-b-o o o  --   -");
//        lines.add("-    o -o-   - -");
//        lines.add("--  o    o o o -");
//        lines.add("x---- ----------");
//        lines.add("x-      -xxxxxxx");
//        lines.add("x-      -xxxxxxx");
//        lines.add("x-******-xxxxxxx");
//        lines.add("x-******-xxxxxxx");
//        lines.add("x-******-xxxxxxx");
//        lines.add("x--------xxxxxxx");


        // 28
//        lines.add("xxxxxxxxxxxxxx--------");
//        lines.add("xxxxxxxxxxxxxx-  ****-");
//        lines.add("xxx------------  ****-");
//        lines.add("xxx-    -  o o   ****-");
//        lines.add("xxx- ooo-o  o -  ****-");
//        lines.add("xxx-  o     o -  ****-");
//        lines.add("xxx- oo -o o o--------");
//        lines.add("----  o -     -xxxxxxx");
//        lines.add("-   - ---------xxxxxxx");
//        lines.add("-    o  --xxxxxxxxxxxx");
//        lines.add("- oo-oo b-xxxxxxxxxxxx");
//        lines.add("-   -   --xxxxxxxxxxxx");
//        lines.add("---------xxxxxxxxxxxxx");




        // level 30
//        lines.add("xxx----------x");
//        lines.add("----******  -x");
//        lines.add("-   *****-  -x");
//        lines.add("-  -****** --x");
//        lines.add("-- ----o--o-xx");
//        lines.add("-bo  o o   ---");
//        lines.add("- oo    --   -");
//        lines.add("- -  oo--  - -");
//        lines.add("-   o  - oo  -");
//        lines.add("-  o  o   -o -");
//        lines.add("----   - o o -");
//        lines.add("xxx-   -     -");
//        lines.add("xxx-----------");

        FileUtils.write(new File("all_solution.txt"), "\n\n"+LocalDateTime.now().toString()+"\n", "UTF-8", true);
        ObjectMapper objectMapper = new ObjectMapper();

        for (File file : new File("./maps").listFiles()) {

            String mapId = file.getName().replace(".json", "");

            if (!mapId.equals("map27")){
                continue;
            }

//            if (mapId.equals("map27") ||
//                    mapId.equals("map21") ||
//                    mapId.equals("map28") ||
//                    mapId.equals("map30")) {
//
//                FileUtils.write(new File("all_solution.txt"), String.format("Map %s failed OOM\n", mapId), "UTF-8", true);
//                continue;
//            }

            try {
                String mapFileContent = FileUtils.readLines(file, "UTF-8")
                        .stream()
                        .filter(s -> !s.startsWith("//"))
                        .collect(Collectors.joining("\n"));


                Map mapFile = objectMapper.readValue(mapFileContent, Map.class);
                List<String> mapLines = (List<String>) mapFile.get("map");


                System.out.println(mapId);
                System.out.println("-----------------------");
                for (String mapLine : mapLines) {
                    System.out.println(mapLine);
                }


                Solution solution = solve(mapLines, mapId);
                if (solution.solved()){
                    System.out.println(String.format("Map %s solved in %d ms, %d steps", mapId, solution.timeTakenMs, solution.states.size()));
                    FileUtils.write(new File("all_solution.txt"), String.format("Map %s solved in %d ms, %d steps\n", mapId, solution.timeTakenMs, solution.states.size()), "UTF-8", true);
                } else {
                    System.out.println(String.format("Map %s failed in %d ms", mapId, solution.timeTakenMs));
                    FileUtils.write(new File("all_solution.txt"), String.format("Map %s failed in %d ms\n", mapId, solution.timeTakenMs), "UTF-8", true);
                }

            } catch (Exception e) {
                FileUtils.write(new File("all_solution.txt"), String.format("Map %s failed with exception: %s\n", mapId, e.getMessage()), "UTF-8", true);
            }

        }


        System.exit(0);



    }


    public static class Solution {
        List<GameState> states;
        long timeTakenMs;

        public Solution(List<GameState> states, long timeTakenMs) {
            this.states = states;
            this.timeTakenMs = timeTakenMs;
        }

        public boolean solved(){
            return states!=null && !states.isEmpty();
        }

        public List<GameState> getStates() {
            return states;
        }

        public void setStates(List<GameState> states) {
            this.states = states;
        }

        public long getTimeTakenMs() {
            return timeTakenMs;
        }

        public void setTimeTakenMs(long timeTakenMs) {
            this.timeTakenMs = timeTakenMs;
        }
    }


    private static Solution solve(List<String> lines, String mapId) throws IOException {

        long startMs = System.currentTimeMillis();

        GameHistory history = GameUtils.createHistory();
        GameUtils.Game game = GameUtils.init(lines);

//        Strategy strategy = new BFS(1000);
        Strategy strategy = new ScoreBased();
        List<GameState> steps = strategy.solve(game.setting, history, game.initialState);
        int numberOfState = steps.size();

        GameState previousStep = null;
        List<GameState> fullSteps = new LinkedList<>();
        for (GameState currentStep : steps) {
            if (previousStep!=null){
                try {
                    List<GameState> intermStates = GameUtils.searchPlayerMoves(game.setting, previousStep, currentStep);
                    intermStates.forEach(s -> s.setInterm(true));
                    fullSteps.addAll(intermStates);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(previousStep);
                    System.out.println(currentStep);
                }
            }
            fullSteps.add(currentStep);
            previousStep = currentStep;
        }
        steps = fullSteps;
        int numberOfMoves = steps.size();

        System.out.println("Number of steps " + numberOfState);
        System.out.println("Number of moves " + numberOfMoves);
        StringBuilder output = new StringBuilder();
        int i = 1;
        for (GameState step : steps) {
            output.append("Step " + i++ + " of " + steps.size()).append("<br/>");
            if (!step.isInterm()) {
                output.append("(core step)").append("<br/>");
            }
            output.append("------------").append("<br/>");
            output.append("<div id='step_" + i + "_" + numberOfMoves + "'>").append("\n");
            output.append(GameUtils.toHtml(game.setting, step)).append("<br/>").append("\n");
            output.append("</div>").append("\n");
            output.append("<br/>");
        }

        System.out.println(output.toString());


        String template = FileUtils.readFileToString(new File("./template.html"), "UTF-8");
        template = template.replace("{{solution}}", output.toString());
        template = template.replace("{{steps}}", numberOfMoves + "");

        FileUtils.writeStringToFile(new File("./solution_"+mapId+".html"), template, "UTF-8");

        List<String> moves = GameUtils.translateToMove(steps);
        FileUtils.writeLines(new File("./solution_"+mapId+".txt"), moves);

        long timeTakenMs = System.currentTimeMillis() - startMs;
        return new Solution(steps, timeTakenMs);

    }
}
