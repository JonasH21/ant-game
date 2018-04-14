package com.ant;

import java.util.Collection;

abstract class AbstractState extends AdditivePair<Coordinate> {
    public AbstractState(Coordinate antA, Coordinate antB){super(antA,antB);}
    AbstractState(int ax, int ay, int bx, int by){this(new Coordinate(ax,ay), new Coordinate(bx,by));}
    abstract Collection<? extends AbstractState> getAccessibleStates(boolean diagonalsAllowed, boolean backtrackingAllowed, boolean lookForCrossing);
    abstract boolean isAbsorbing(boolean lookForCrossing);
}
