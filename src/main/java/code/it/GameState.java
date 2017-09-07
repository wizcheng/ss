package code.it;

import java.util.List;

public class GameState {

    private final Board box;
    private final Board movable;
    private GameState previousState;

    public GameState(Board box, Board movable) {
        this.box = box;
        this.movable = movable;
    }

    public GameState setPreviousState(GameState previousState){
        this.previousState = previousState;
        return this;
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
        GameState gameState = new GameState(box.copy(), movable.copy());
        gameState.previousState = previousState;
        return gameState;
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

    public GameState getPreviousState() {
        return previousState;
    }
}
