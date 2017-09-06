package code.it;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class GameState {

    private final Board box;
    private final Board movable;

    public GameState(Board box, Board movable) {
        this.box = box;
        this.movable = movable;
    }

    public List<Spot> boxes() {
        return box.occupied();
    }

    public List<Spot> movables(){
        return movable.occupied();
    }

    public GameState setMovable(Spot spot, boolean value){
        movable.set(spot, value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameState that = (GameState) o;

        if (!box.equals(that.box)) return false;
        return movable.equals(that.movable);
    }

    @Override
    public int hashCode() {
        int result = box.hashCode();
        result = 31 * result + movable.hashCode();
        return result;
    }

    public GameState copy() {
        return new GameState(box.copy(), movable.copy());
    }

    public boolean isBox(Spot s) {
        return box.isOccupied(s);
    }

    public GameState move(Spot currentSpot, Spot newSpot) {
        if (box.isOccupied(currentSpot) && !box.isOccupied(newSpot)){
            box.remove(currentSpot).add(newSpot);
        }
        return this;
    }

    public boolean haveBox(Spot s) {
        return box.isOccupied(s);
    }

    public boolean isMovable(Spot p) {
        return movable.isOccupied(p);
    }
}
