package code.it;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Board {

    private final BitSet boxes;

    private Board(BitSet bitSet){
        this.boxes = bitSet;
    }

    public Board(int rows, int cols) {
        boxes = new BitSet(rows * cols);
    }

    private int position(int columns, Spot spot){
        return spot.getRow() * columns + spot.getCol();
    }

    public boolean isOccupied(int columns, Spot spot){
        return boxes.get(position(columns, spot));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        return boxes.equals(board.boxes);
    }

    @Override
    public int hashCode() {
        return boxes.hashCode();
    }

    public List<Spot> occupied(GameSetting setting) {
        List<Spot> spots = new ArrayList<>();
        for (int row = 0; row < setting.rows(); row++) {
            for (int col = 0; col < setting.cols(); col++) {
                Spot spot = new Spot(row, col);
                if (isOccupied(setting.cols(), spot)){
                    spots.add(spot);
                }
            }
        }
        return spots;
    }

    public Board copy() {
        return new Board((BitSet) boxes.clone());
    }

    public Board remove(int columns, Spot spot) {
        return set(columns, spot, false);
    }

    public Board add(int columns, Spot spot){
        return set(columns, spot, true);
    }

    public Board set(int columns, Spot spot, boolean value) {
        boxes.set(position(columns, spot), value);
        return this;
    }

    public void clear() {
        boxes.clear();
    }
}
