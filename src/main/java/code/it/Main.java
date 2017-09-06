package code.it;

import code.it.strategy.BFS;

import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) {


        List<String> lines = new ArrayList<>();
        lines.add("#####");
        lines.add("#@ ##");
        lines.add("#.$* #");
        lines.add("#   ##");
        lines.add("#    #");
        lines.add("######");

        GameHistory history = GameUtils.createHistory();
        GameUtils.Game game = GameUtils.init(lines);

        BFS strategy = new BFS(1000);
        List<GameState> steps = strategy.solve(game.setting, history, game.initialState);

        System.out.println("Number of steps " + steps.size());
        steps.forEach(System.out::println);


    }
}
