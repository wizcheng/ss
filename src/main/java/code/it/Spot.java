package code.it;

import java.util.Arrays;
import java.util.List;

public class Spot {

    private int row;
    private int col;

    private Spot previous;

    public Spot(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Spot getPrevious() {
        return previous;
    }

    public void setPrevious(Spot previous) {
        this.previous = previous;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Spot[] possibleMoves() {
        return new Spot[]{
                new Spot(row - 1, col),
                new Spot(row, col + 1),
                new Spot(row + 1, col),
                new Spot(row, col - 1)
        };
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

    public boolean isDiagonal(Spot spot) {
        return Math.abs(row - spot.row) == 1
                && Math.abs(col - spot.col) == 1;
    }

    public String toString(){
        return "(" + row + ", " + col + ")";
    }

    public boolean isValid() {
        return row >= 0 && col >=0;
    }

    public Spot top(){ return new Spot(row - 1, col); }
    public Spot left(){ return new Spot(row, col-1);}
    public Spot bottom(){ return new Spot(row+1, col);}
    public Spot right(){ return new Spot(row, col+1);}
    public Spot topRight(){ return top().right(); }
    public Spot topLeft(){ return top().left();}
    public Spot bottomRight(){ return bottom().right(); }
    public Spot bottomLeft(){ return bottom().left();}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Spot spot = (Spot) o;

        if (row != spot.row) return false;
        return col == spot.col;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    public Spot calculatePath(Spot newSpot) {
        int deltaRow = newSpot.row - row;
        int deltaCol = newSpot.col - col;
        return new Spot(row - deltaRow, col - deltaCol);
    }

    public String moveTo(Spot next) {
        if (next.equals(top())){
            return "up";
        } else if (next.equals(left())) {
            return "left";
        } else if (next.equals(bottom())) {
            return "down";
        } else if (next.equals(right())) {
            return "right";
        }
        throw new IllegalStateException("Unable to translate to move, from=" + this + ", to=" + next);
    }
}
