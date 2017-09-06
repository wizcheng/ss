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

    public Spot otherSideOf(Spot spot) {
        if (row == spot.row){
            if (col + 1 == spot.col){
                return new Spot(row, col - 1);
            } else if (col - 1 == spot.col ){
                return new Spot(row, col + 1);
            } else {
                throw new IllegalStateException("Invalid state");
            }

        } else if (col == spot.col) {
            if (row +1 == spot.row){
                return new Spot(row - 1, col);
            } else if ( row-1 == spot.row){
                return new Spot(row + 1, col);
            } else {
                throw new IllegalStateException("Invalid state");
            }
        } else {
            throw new IllegalStateException("Invalid state");
        }
    }
}
