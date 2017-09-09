package code.it;

import java.util.List;

public class GameState implements Comparable<GameState> {

    private final Board box;
    private final Board movable;
    private GameState previousState;
    private Spot player;
    private int score;

    public GameState(Board box, Board movable) {
        this.box = box;
        this.movable = movable;
    }

    public GameState setPreviousState(GameState previousState){
        this.previousState = previousState;
        return this;
    }

    public List<Spot> boxes(GameSetting setting) {
        return box.occupied(setting);
    }

    public GameState setMovable(GameSetting gameSetting, Spot spot, boolean value){
        movable.set(gameSetting.cols(), spot, value);
        return this;
    }

    public GameState setPlayer(Spot spot){
        this.player = spot;
        return this;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Spot getPlayer(){
        return this.player;
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
        gameState.player = player;
        return gameState;
    }

    public boolean isBox(GameSetting gameSetting, Spot s) {
        return box.isOccupied(gameSetting.cols(), s);
    }

    public GameState move(GameSetting gameSetting, Spot currentSpot, Spot newSpot) {
        if (box.isOccupied(gameSetting.cols(), currentSpot) && !box.isOccupied(gameSetting.cols(), newSpot)){
            box.remove(gameSetting.cols(), currentSpot).add(gameSetting.cols(), newSpot);
        }
        player = currentSpot;
        return this;
    }

    public boolean isMovable(GameSetting gameSetting, Spot p) {
        return movable.isOccupied(gameSetting.cols(), p);
    }

    public GameState getPreviousState() {
        return previousState;
    }

    public void clearMovable() {
        movable.clear();

    }

    @Override
    public int compareTo(GameState o) {
        return o.score - score;
    }

    public int getScore() {

        return score;
    }
}
