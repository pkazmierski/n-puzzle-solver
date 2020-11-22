package tul.kazmierski;

import java.util.ArrayList;

public class State {
    public ArrayList<Integer> board;
    public State parent;
    public Move moveToExecute;

    public State(ArrayList<Integer> board, State parent, Move moveToExecute) {
        this.board = board;
        this.parent = parent;
        this.moveToExecute = moveToExecute;
    }
}
