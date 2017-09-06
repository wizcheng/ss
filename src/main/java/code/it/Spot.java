package code.it;

import java.util.Arrays;
import java.util.List;

public class Spot {

    private int row;
    private int col;

    public Spot(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public List<Spot> possibleMoves() {
        return Arrays.asList(
                new Spot(row-1, col),
                new Spot(row+1, col),
                new Spot(row, col-1),
                new Spot(row, col+1)
        );
    }
}
