package tul.kazmierski;

import tul.kazmierski.heuristics.InversionsHeuristic;
import tul.kazmierski.solvers.aStarSolver;
import tul.kazmierski.solvers.bfsSolver;
import tul.kazmierski.solvers.dfsSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.exit;
import static tul.kazmierski.Util.*;

public class Main {

    public static ArrayList<Integer> solvedBoard = null;
    public static Dimensions dimensions = null;
    //TODO consider refactoring and moving this elsewhere
    public static Move[] movesOrder = null;
    public static int visitedCounter = 0;


    /**
     * Required arguments in order:
     * 1. Search strategy
     * 2. Search strategy argument, i.e. order or heuristic id
     *
     * @param args Arguments passed to the program, as described above.
     */
    public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Need 2 arguments. Got " + args.length);

        Scanner sc = new Scanner(System.in);
        String dimensionsArg;
        ArrayList<String> rowsArg = new ArrayList<>();

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

        switch (args[0]) {
            case "-b":
            case "--bfs":
                movesOrder = parseMovesOrder(args[1]);
                System.out.println("Moves order: " + Arrays.toString(movesOrder));
                puzzleSolverOrder = new bfsSolver();
                finalState = puzzleSolverOrder.solveWithOrder(initialBoard, movesOrder);
                break;
            case "-d":
            case "--dfs":
                movesOrder = parseMovesOrder(args[1]);
                System.out.println("Moves order: " + Arrays.toString(movesOrder));
                puzzleSolverOrder = new dfsSolver();
                finalState = puzzleSolverOrder.solveWithOrder(initialBoard, movesOrder);
                break;
            case "-i":
            case "--idfs":
                movesOrder = parseMovesOrder(args[1]);
                break;
            case "-h":
            case "--bf":
                break;
            case "-a":
            case "--astar":
                movesOrder = parseMovesOrder("R");
                System.out.println("Moves order: " + Arrays.toString(movesOrder));
                puzzleSolverHeuristic = new aStarSolver();
                //FIXME don't call the function with a hardcoded heuristic
                finalState = puzzleSolverHeuristic.solveWithHeuristic(initialBoard, new InversionsHeuristic());
                break;
            case "-s":
            case "--sma":
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
