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

    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (goal.isOccupied(row, col)){
                    builder.append("X");
                } else if (space.isOccupied(row, col)){
                    builder.append("-");
                } else {
                    builder.append("#");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
