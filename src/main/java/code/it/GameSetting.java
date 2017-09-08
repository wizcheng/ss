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
        return goal.isOccupied(cols(), box);
    }

    public boolean isSpace(Spot s) {
        return space.isOccupied(cols(), s);
    }

    public boolean isWall(Spot s){
        return wall.isOccupied(cols(), s);
    }

    public List<Spot> spaces() {
        return space.occupied(this);
    }


    public List<Spot> walls() {
        return wall.occupied(this);
    }

    public List<Spot> goals() {
        return goal.occupied(this);
    }
}
