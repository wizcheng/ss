package code.it;

import java.util.List;

public class GameSetting {

    private final int rows;
    private final int cols;
    private final Board goal;
    private final Board space;
    private final Board wall;

    public GameSetting(int rows, int cols, Board goal, Board space, Board wall) {
        this.rows = rows;
        this.cols = cols;
        this.goal = goal;
        this.space = space;
        this.wall = wall;
    }

    public int rows(){
        return rows;
    }

    public int cols(){
        return cols;
    }

    public boolean isGoal(Spot box) {
        return goal.isOccupied(box);
    }

    public boolean isSpace(Spot s) {
        return space.isOccupied(s);
    }

    public boolean isWall(Spot s){
        return wall.isOccupied(s);
    }

    public List<Spot> spaces() {
        return space.occupied();
    }


    public List<Spot> walls() {
        return wall.occupied();
    }

    public List<Spot> goals() {
        return goal.occupied();
    }
}
