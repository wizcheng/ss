package code.it;

import code.it.strategy.BFS;
import code.it.strategy.ScroeBase;
import code.it.strategy.Strategy;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        lines.add("xxx------x");
        lines.add("x---    -x");
        lines.add("--* o-- --");
        lines.add("-**o o  b-");
        lines.add("-** o o --");
        lines.add("------  -x");
        lines.add("xxxxx----x");

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
//        lines.add("--------x");
//        lines.add("-      -x");
//        lines.add("- -oo  -x");
//        lines.add("- ***- -x");
//        lines.add("--***o --");
//        lines.add("x- -- o -");x
//        lines.add("x-o  o  -");
//        lines.add("x-  -  b-");
//        lines.add("x--------");



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




        GameHistory history = GameUtils.createHistory();
        GameUtils.Game game = GameUtils.init(lines);

        Strategy strategy = new BFS(1000);
//        Strategy strategy = new ScroeBase();
        List<GameState> steps = strategy.solve(game.setting, history, game.initialState);

        System.out.println("Number of steps " + steps.size());
        StringBuilder output = new StringBuilder();
        output.append("<html><body>");
        int i = 1;
        for (GameState step : steps) {
            output.append("Step " + i++ + " of " + steps.size()).append("<br/>");
            output.append("------------").append("<br/>");
            output.append(GameUtils.toHtml(game.setting, step)).append("<br/>");
            output.append("<br/>");
        }
        output.append("</body></html>");
        FileUtils.writeStringToFile(new File("./solution.html"), output.toString(), "UTF-8");

        System.out.println(output.toString());
        System.exit(0);



    }
}
