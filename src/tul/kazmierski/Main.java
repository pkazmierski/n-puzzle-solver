package tul.kazmierski;

import tul.kazmierski.heuristics.LinearConflictWithManhattanHeuristic;
import tul.kazmierski.heuristics.ManhattanDistanceHeuristic;
import tul.kazmierski.solvers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.System.exit;
import static tul.kazmierski.Util.*;

public class Main {

    public static ArrayList<Integer> solvedBoard = null;
    public static Dimensions dimensions = null;
    //TODO consider refactoring and moving this elsewhere
    public static Move[] movesOrder = null;
    public static int visitedCounter = 0;
    public static HashMap<Move, Move> oppositeMoves = new HashMap<>();


    /**
     * Required arguments in order:
     * 1. Search strategy
     * 2. Search strategy argument, i.e. order or heuristic id
     *
     * @param args Arguments passed to the program, as described above.
     */
    public static void main(String[] args) {
        if (args.length != 2 && args.length != 3)
            throw new IllegalArgumentException("Need 2 or 3 arguments. Got " + args.length);

        Scanner sc = new Scanner(System.in);
        String dimensionsArg;
        ArrayList<String> rowsArg = new ArrayList<>();

        oppositeMoves.put(Move.DOWN, Move.UP);
        oppositeMoves.put(Move.UP, Move.DOWN);
        oppositeMoves.put(Move.LEFT, Move.RIGHT);
        oppositeMoves.put(Move.RIGHT, Move.LEFT);

        //parse arguments as Strings
        if (sc.hasNextLine())
            dimensionsArg = sc.nextLine();
        else
            throw new IllegalArgumentException("Line empty. Expected board dimensions");

        while (sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            if (nextLine.length() != 0)
                rowsArg.add(nextLine);
        }

        //set-up session paramaters
        dimensions = parseDimensions(dimensionsArg);
        ArrayList<Integer> initialBoard = parseInitialBoard(rowsArg, dimensions);
        solvedBoard = generateSolvedBoard(initialBoard);

        System.out.println("Input board: ");
        printBoard(initialBoard);
        System.out.println("Solved board: ");
        printBoard(solvedBoard);

        if (!checkIfSolvable(initialBoard)) {
            System.err.println("The board is not solvable. Aborting...");
            exit(1);
        }

        PuzzleSolverOrder puzzleSolverOrder;
        PuzzleSolverHeuristic puzzleSolverHeuristic;
        State finalState = null;

        long startTime = System.currentTimeMillis();
        movesOrder = parseMovesOrder(args[1]);
        System.out.println("Moves order: " + Arrays.toString(movesOrder));


        switch (args[0]) {
            case "-b":
            case "--bfs":
                puzzleSolverOrder = new bfsSolver();
                finalState = puzzleSolverOrder.solveWithOrder(initialBoard, movesOrder);
                break;
            case "-d":
            case "--dfs":
                puzzleSolverOrder = new dfsSolver();
                finalState = puzzleSolverOrder.solveWithOrder(initialBoard, movesOrder);
                break;
            case "-i":
            case "--iddfs":
                puzzleSolverOrder = new iddfsSolver();
                finalState = puzzleSolverOrder.solveWithOrder(initialBoard, movesOrder);
                break;
            case "-h":
            case "--bf":
                puzzleSolverHeuristic = new bestFirstSolver();
                //FIXME don't call the function with a hardcoded heuristic
                finalState = puzzleSolverHeuristic.solveWithHeuristic(initialBoard, new LinearConflictWithManhattanHeuristic());
                break;
            case "-a":
            case "--astar":
                puzzleSolverHeuristic = new AStarSolver();
                //FIXME don't call the function with a hardcoded heuristic
                finalState = puzzleSolverHeuristic.solveWithHeuristic(initialBoard, new ManhattanDistanceHeuristic());
                break;
            case "-s":
            case "--sma":
                puzzleSolverHeuristic = new SMAStarSolver();
                //FIXME don't call the function with a hardcoded heuristic
                finalState = puzzleSolverHeuristic.solveWithHeuristic(initialBoard, new ManhattanDistanceHeuristic());
                break;
            default:
                throw new IllegalArgumentException("Incorrect 1st argument");
        }

        long stopTime = System.currentTimeMillis();
        System.out.println("Time to complete: " + (stopTime - startTime) + " ms");
        System.out.println("Nodes visited: " + visitedCounter);

        if(finalState == null)
            throw new IllegalArgumentException("Unknown error. Cannot solve the board");
        Move[] solutionMoves = reconstructSolution(finalState);
        System.out.println("Number of moves: " + solutionMoves.length);
        System.out.println(Arrays.toString(solutionMoves));
//        visualizeSolution(finalState);
    }
}
