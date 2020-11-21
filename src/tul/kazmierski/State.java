package tul.kazmierski;

public class State {
    public int[] board;
    public State parent;
    public Move moveToExecute;

    public State(int[] board, State parent, Move moveToExecute) {
        this.board = board;
        this.parent = parent;
        this.moveToExecute = moveToExecute;
    }
}
