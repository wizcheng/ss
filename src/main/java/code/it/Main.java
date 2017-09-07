package code.it;

import code.it.strategy.BFS;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) throws IOException {


        List<String> lines = new ArrayList<>();

        lines.add("xx---xxx");
        lines.add("xx-*-xxx");
        lines.add("xx- ----");
        lines.add("---o o*-");
        lines.add("-*ob  --");
        lines.add("----o --");
        lines.add("xxx-*-xx");
        lines.add("xxx---xx");

//        lines.add("xxx----------x");
//        lines.add("----******  -x");
//        lines.add("-  *****-  -xx");
//        lines.add("-  -****** --x");
//        lines.add("-- ----o--o-xx");
//        lines.add("-bo  o o  ---x");
//        lines.add("- oo    --  -x");
//        lines.add("- -  oo--  - -");
//        lines.add("-  o  - oo  -x");
//        lines.add("-  o  o  -o -x");
//        lines.add("----  - o o -x");
//        lines.add("xxx-  -    -xx");
//        lines.add("xxx-----------");

//        "--------xxxxxxxx",
//        "-  -  -xxxxxxxx",
//        "- o    ----xxxxx",
//        "-- --o -b ------",
//        "x-  o o--o  -  -",
//        "x- -  o - o ***-",
//        "x- o -o    -***-",
//        "x-    -- o-***-",
//        "x--- o -  o ***-",
//        "xxx--- -  o-***-",
//        "xxxx-  oo  -----",
//        "xxxx-    --xxxx",
//        "xxxx-------xxxxx"


        GameHistory history = GameUtils.createHistory();
        GameUtils.Game game = GameUtils.init(lines);

        BFS strategy = new BFS(1000);
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



    }
}
