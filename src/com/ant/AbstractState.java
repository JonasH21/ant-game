package com.ant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class AbstractState extends AdditivePair<Coordinate> {
    AbstractState(Coordinate antA, Coordinate antB){super(antA,antB);}
    AbstractState(int ax, int ay, int bx, int by){this(new Coordinate(ax,ay), new Coordinate(bx,by));}
    abstract Collection<? extends AbstractState> getAccessibleStates(boolean diagonalsAllowed, boolean backtrackingAllowed, boolean lookForCrossing);
    abstract boolean isAbsorbing(boolean lookForCrossing);

    /**
     * Returns the state for the given index
     * @param index 0 is X, 1 is Y.
     * @return A pair of coordinates (c,d) where c is the position and d is the direction of travel. If no direction is
     * recorded, d is returned null
     */
    abstract MyPair<Coordinate> getState(int index);

    /**
     * Given an ant's predicament defined by ant = ((x,y),(dx,dy)), where (x,y) is its position coordinate
     * and (dx,dy) is its direction of travel, provide a collection of potential moves given the restrictions on
     * diagonals and backtracking.
     * @param ant pair of coordinates (c,d) defining the position and direction information.
     * @param diagonalsAllowed context of problem
     * @param backtrackingAllowed context of problem
     * @return list of coordinates {(c,d)} containing accessible states
     */
    public static List<MyPair<Coordinate>> getAccessibleMoves(MyPair<Coordinate> ant, boolean diagonalsAllowed, boolean backtrackingAllowed){
        List<Coordinate> accessiblePositions = ant.X.getAccessiblePairs(diagonalsAllowed);
        if(!backtrackingAllowed) {
            if (ant.Y == null)
                throw new RuntimeException("Unexpected exception: state has no direction information, but backtracking is forbidden. Cannot resolve accessible moves.");

            accessiblePositions.remove(ant.X.add(ant.Y.inverse()));
        }

        List<MyPair<Coordinate>> moves = new ArrayList<>();

        for(Coordinate coordinate : accessiblePositions){
            moves.add(new MyPair<>(coordinate, (Coordinate)coordinate.subtract(ant.X)));
        }

        return moves;
    }
}
