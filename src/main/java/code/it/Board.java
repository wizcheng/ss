package code.it;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Board {

    private final BitSet[] boxes;


    public Board(int rows, int cols) {
        boxes = new BitSet[rows];
        for (int i = 0; i < rows; i++) {
            boxes[i] = new BitSet(cols);
        }
    }

    public boolean isOccupied(int row, int col){
        return boxes[row].get(col);
    }

    public boolean isOccupied(Spot spot){
        return isOccupied(spot.getRow(), spot.getCol());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(boxes, board.boxes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(boxes);
    }

    public List<Spot> occupied() {
        List<Spot> spots = new ArrayList<>();
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length(); j++) {
                if (isOccupied(i, j)){
                    spots.add(new Spot(i, j));
                }
            }
        }
        return spots;
    }

    public Board copy() {
        return new Board(boxes.length, boxes[0].length());
    }

    public Board remove(Spot spot) {
        boxes[spot.getRow()].clear(spot.getCol());
        return this;
    }

    public Board add(Spot spot){
        boxes[spot.getRow()].set(spot.getCol());
        return this;
    }

    public Board set(Spot spot, boolean value) {
        boxes[spot.getRow()].set(spot.getCol(), value);
        return this;
    }
}