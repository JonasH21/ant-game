package com.ant;

import java.util.*;

import com.github.sharedlibs.stopwatch.Stopwatch;
import org.springframework.util.StopWatch;
import org.zouzias.rek.algorithms.REKSolver;
import org.zouzias.rek.matrix.DoubleMatrix;
import org.zouzias.rek.matrix.SparseMatrix;
import org.zouzias.rek.vector.DenseVector;
import org.zouzias.rek.vector.DoubleVector;

public class Main {
    static Collection<Coordinate> GRID;
    private static HashMap<MyPair<Coordinate>, List<MyPair<Coordinate>>> AvailableMoves = new HashMap<>();



    public static void main(String[] args){
        System.out.println("Welcome to Jonas's ant game solution\n");
        System.out.println("I will solve the problem in the following subsections:");
        System.out.println("1a. Expected time to collision, diagonals OK,");
        System.out.println("1b. Expected time to collision, diagonals FORBIDDEN,");
        System.out.println("2. Expected time to crossing, diagonals FORBIDDEN,");
        System.out.println("where a \"collision\" is the situation defined in the problem, where both ants " +
                "occupy the same square.\n\n");
        System.out.println("I model the system as an absorbing Markov chain");
        System.out.println("We have the concept of a \"state\" which is a pair of coordinates, corresponding to " +
                "the positions of the two ants. For example ((x_1, y_1), (x_2, y_2)) is in the state space, S, for " +
                "each x_1, x_2, y_1, y_2 < 8.\n");
        System.out.println("Given a state s in S, define F(s) to be the set of states t in S that are accessible " +
                "from s. A state t=(ct1,ct2) is accessible from s=(cs1,cs2) if d(ct1,cs1) = d(ct2,ct2) = 1, " +
                "where d is a distance function on the set of coordinates " +
                "defined such that d(c1,c2) is the distance (minimum steps for an ant) between the coordinates " +
                "c1 and c2. (Note that F depends on the metric d.)");
        System.out.println("Now, due to independence, " +
                "the probability P_st of transitioning from state s to state t is equal to " +
                "I(t in F(s))/|F(s)|.");
        System.out.println("By defining the functions F on the state space, I will be in a position to populate " +
                "the transiton matrix P. The transition matrix is m by m matrix with entries P_st defined above, " +
                "where m is the size of the state space.");

        System.out.println("Define the matrix Q to be the submatrix of P where the absorbing states are removed. " +
                "I also remove the impossible states: if diagonals are not allowed then both coordinates must have " +
                "the same parity (in other words, the ants are either both on black squares or white squares " +
                "of our chess-board. " +
                "The absorbing states are the \"goal\" states, in the first case those states ((i,j),(i,j)) for all " +
                "i,j<8.");
        System.out.println("Using these probabilities, I create a system of linear equations " +
                "(I-Q)t = 1, where 1 is the column vector with entries all 1. I solve this for variable " +
                "t_j0, where j0 is the index of the initial state.");

        GRID = new ArrayList<>(64);
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                GRID.add(new Coordinate(x, y));

        System.out.println("The grid contains " + GRID);

        Question1();

    }

    private static void Question1(){
        SimpleStatesLogic(false, true, true);
        SimpleStatesLogic(true, true, true);
        SimpleStatesLogic(false, false, false);
        SimpleStatesLogic(false, true, false);
        SimpleStatesLogic(true, true, false); //~94.5
        SimpleStatesLogic(true, false, false); //~87.7
    }

    /**
     * Solution for the questions where the statespace is simple, and does not contain data about the previous position
     * (i.e. a state is just a pair of coordinates).
     */
    private static void SimpleStatesLogic(boolean backtrackingAllowed, boolean diagonalsAllowed, boolean lookForCrossing){
        System.out.println("*********");
        System.out.println("Setup: backtracking " + backtrackingAllowed + ", diagonals " + diagonalsAllowed + ", " + (lookForCrossing ? "crossing" : "collide"));
        System.out.println("*********");

        MyTuple<List<AbstractState>, HashMap<AbstractState, Integer>> stateSpaceRes = getStateSpace(backtrackingAllowed, diagonalsAllowed, lookForCrossing);
        List<AbstractState> stateSpace = stateSpaceRes.X;
        HashMap<AbstractState,Integer> stateSpaceMap = stateSpaceRes.Y;
        int initialStateIndex = stateSpaceRes.Y.get(getInitialState(backtrackingAllowed,lookForCrossing));

        if(initialStateIndex<0)
            throw new RuntimeException("Initial state index not found");

        //dimension of the transient matrix
        int dim = stateSpace.size();

        System.out.println("Initializing sparse matrix Q of dimension " + dim);
        //now I work on populating Q, the fundamental matrix
        DoubleMatrix A = new SparseMatrix(dim,dim);

        //AbstractRealMatrix A = new OpenMapRealMatrix(dim,dim); <- Apache commons attempt

        System.out.println("Populating Q with transition probabilities");

        StopWatch s1 = new StopWatch("Check coverage of states");
        StopWatch s2 = new StopWatch("Covered before logic");
        StopWatch s3 = new StopWatch("Get moves");
        StopWatch s4 = new StopWatch("Check transience");
        StopWatch s5 = new StopWatch("Get index and store in matrix");
        StopWatch s6 = new StopWatch("Get index");
        StopWatch s7 = new StopWatch("Store in mtx");
        s1.setKeepTaskList(false);
        s2.setKeepTaskList(false);
        s3.setKeepTaskList(false);
        s4.setKeepTaskList(false);
        s5.setKeepTaskList(false);
        s6.setKeepTaskList(false);
        s7.setKeepTaskList(false);

        //find probability of transitioning from transient state s to transient space t
        //matrix A here is I-Q
        int rowIndex = -1; int colIndex;
        for(AbstractState s : stateSpace){
            rowIndex++;
            if(rowIndex % 1000 == 0) {
                System.out.println("Coverage chk / coverage lg / moves / transience / index+store (index / store)");
                System.out.print(s1.getTotalTimeMillis()+" / ");
                System.out.print(s2.getTotalTimeMillis()+" / ");
                System.out.print(s3.getTotalTimeMillis()+" / ");
                System.out.print(s4.getTotalTimeMillis()+" / ");
                System.out.print(s5.getTotalTimeMillis()+" (");
                    System.out.print(s6.getTotalTimeMillis()+" / ");
                    System.out.print(s7.getTotalTimeMillis()+")\n");
                System.out.println("computing row " + rowIndex + " for state " + s);
            }

            s1.start("");
            MyPair<Coordinate> stateOfA = ((State)s).getStateX();
            MyPair<Coordinate> stateOfB = ((State)s).getStateY();
            List<MyPair<Coordinate>> movesForA;
            List<MyPair<Coordinate>> movesForB;
            //using Collection<MyPair<Coordinate>> (ms): 11 / 6 / 908 / 11733 / 84224 (73998 / 8933)
            //using List<MyPair<Coordinate>>             14 / 3 / 299 /  7450 / 76947 (60196 / 15687)
            s1.stop();
            s2.start("");
            s2.stop();

            s3.start("Get moves");
            if(AvailableMoves.containsKey(stateOfA)) movesForA = AvailableMoves.get(stateOfA);
            else
                {
                movesForA = State.getAccessibleMoves(stateOfA, diagonalsAllowed, backtrackingAllowed);
                AvailableMoves.put(stateOfA, movesForA);
            }

            if(AvailableMoves.containsKey(stateOfB)) movesForB = AvailableMoves.get(stateOfB);
            else
                {
                movesForB = State.getAccessibleMoves(stateOfB, diagonalsAllowed, backtrackingAllowed);
                AvailableMoves.put(stateOfB, movesForB);
            }

            //without caching available moves (ms): 602 / 4 / 1840 / 2219 / 154888 (125025 / 25897)
            // with     "   "   "   "      "        43  / 4 / 790  / 5316 / 133301 (107359 / 22406)
            s3.stop();

            s4.start("Check transience");
            //check which of the A,B move combinations is possible given the absorbing property
            List<State> validatedStates = new ArrayList<>();
            for(MyPair<Coordinate> moveForA : movesForA){
                for(MyPair<Coordinate> moveForB : movesForB){
                    State proposedState = new State(moveForA.X, moveForB.X, moveForA.Y, moveForB.Y);
                    if(!proposedState.isAbsorbing(lookForCrossing)){
                        validatedStates.add(proposedState);
                    }
                }
            }
            s4.stop();

            s5.start("Get index and store in matrix");
            double probability = 1d/(double)(validatedStates.size());
            for(State validatedState : validatedStates){
                s6.start("");
                colIndex = stateSpaceMap.get(validatedState);
                s6.stop();
                s7.start("");
                if(colIndex == -1){
                    throw new RuntimeException();
                }

                double mtxEntry = -probability;
                //A.addToEntry(rowIndex,colIndex,mtxEntry);
                A.set(rowIndex,colIndex,mtxEntry);
                s7.stop();
            }
            s5.stop();
        }

        double[] one = new double[dim];
        Arrays.fill(one, 1d);

        for(int k = 0; k < dim; k++){
            A.set(k,k,1d);
        }

        System.out.println("Solving with REK");
        DoubleVector oneREK = new DenseVector(one);
        REKSolver solverREK = new REKSolver();
        DoubleVector solutionREK = solverREK.solve(A,oneREK, 240d);
        double e = solutionREK.get(initialStateIndex);
        System.out.println("t_" + initialStateIndex + "=" + e);

        solutionREK = solverREK.solve(A,oneREK, 300d);
        solutionREK = solverREK.solve(A,oneREK, 450d);
        solutionREK = solverREK.solve(A,oneREK, 600d);
        solutionREK = solverREK.solve(A,oneREK, 900d);
        solutionREK = solverREK.solve(A,oneREK, 1200d);
        solutionREK = solverREK.solve(A,oneREK, 3000d);

        e = solutionREK.get(initialStateIndex);
        System.out.println("t_" + initialStateIndex + "=" + e);

        /*
        // Apache commons LUDecomposition solver code

         System.out.println("Getting solver");
         DecompositionSolver solver = new LUDecomposition(A).getSolver();
         RealVector constants = new ArrayRealVector(one, false);
         System.out.println("Begin solving...");

         RealVector solution = solver.solve(constants);
         System.out.println("Print solution");
         System.out.println(solution);

         System.out.println("t_" + initialStateIndex + "=" + solution.getEntry(initialStateIndex));
         */

    }

    private static int estimateStateSpaceSize(boolean backtrackingAllowed, boolean diagonalsAllowed, boolean lookForCrossing){
        if(backtrackingAllowed && !lookForCrossing)
            return (int)Math.pow(64,2);
        if(!diagonalsAllowed)
            return (int)Math.pow(64,2) * 16;

        return (int)Math.pow(64,2) * (int)Math.pow(8,2);
    }

    private static AbstractState getInitialState(boolean backtrackingAllowed, boolean lookForCrossing){
        if(backtrackingAllowed && !lookForCrossing)
            return new SimpleState(new Coordinate(0,0), new Coordinate(7,7));
        else
            return new State(new Coordinate(0,0), new Coordinate(7,7), Coordinate.O,Coordinate.O);
    }

    private static MyTuple<List<AbstractState>, HashMap<AbstractState, Integer>> getStateSpace(boolean backtrackingAllowed, boolean diagonalsAllowed, boolean lookForCrossing){
        boolean useEnrichedStateSpace = !backtrackingAllowed || lookForCrossing;
        int initialCapacity = estimateStateSpaceSize(backtrackingAllowed,diagonalsAllowed, lookForCrossing);

        List<AbstractState> stateSpace = new ArrayList<>(initialCapacity);
        HashMap<AbstractState, Integer> stateSpaceMap = new HashMap<>(initialCapacity);

        System.out.println("Populating state space, estimated size "+initialCapacity);

        if(useEnrichedStateSpace){
            stateSpace.add(getInitialState(backtrackingAllowed, lookForCrossing));
            stateSpaceMap.put(getInitialState(backtrackingAllowed, lookForCrossing), 0);
        }

        for(Coordinate c1 : GRID){
            for(Coordinate c2 : GRID){
                SimpleState ss = new SimpleState(c1,c2);

                //we store the state if (1.) it's not absorbing and (2.) it's possible
                if(diagonalsAllowed || !Coordinate.parity(ss.X, ss.Y)) {
                    if(useEnrichedStateSpace) {
                        List<State> transientStates = ss.toTransientStates(diagonalsAllowed, lookForCrossing);
                        //stateSpace.addAll(ss.toTransientStates(diagonalsAllowed, lookForCrossing));
                        for(State ts : transientStates){
                            stateSpace.add(ts);
                            stateSpaceMap.put(ts, stateSpace.size()-1);
                        }
                    }
                    else{
                        if(!ss.isAbsorbing(lookForCrossing)) {
                            stateSpace.add(ss);
                            stateSpaceMap.put(ss, stateSpace.size()-1);
                        }
                    }
                }
            }
        }

        return new MyTuple<>(stateSpace, stateSpaceMap);
    }
}
