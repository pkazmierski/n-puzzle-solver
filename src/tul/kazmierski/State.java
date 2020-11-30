package tul.kazmierski;

import java.util.ArrayList;

public class State {
    public ArrayList<Integer> board;
    public State parent;
    public Move moveToExecute;
    public int depth;

    public State(ArrayList<Integer> board, State parent, Move moveToExecute) {
        this.board = board;
        this.parent = parent;
        this.moveToExecute = moveToExecute;
        this.depth = parent == null ? 0 : parent.depth + 1;
    }

    public State(ArrayList<Integer> board, State parent, Move moveToExecute, int depth) {
        this.board = board;
        this.parent = parent;
        this.moveToExecute = moveToExecute;
        this.depth = depth;
    }
}
