package code.it;

import java.util.List;

public class GameSetting {

    private final Board goal;
    private final Board space;

    public GameSetting(Board goal, Board space) {
        this.goal = goal;
        this.space = space;
    }


    public boolean isGoal(Spot box) {
        return goal.isOccupied(box);
    }

    public boolean isSpace(Spot s) {
        return space.isOccupied(s);
    }

    public List<Spot> spaces() {
        return space.occupied();
    }
}
