package com.ant;

import java.util.*;

public class StateIterator<T extends AbstractState> implements Iterator<T> {
    private Iterator<Coordinate> iX, iY;
    private StateSpace<T> stateSpace;
    private T state;

    StateIterator(StateSpace<T> stateSpace){
        this.stateSpace = stateSpace;
        iX = Main.GRID.iterator();
        iY = Main.GRID.iterator();
    }

    @Override
    public boolean hasNext() {
        return iX.hasNext() || iY.hasNext();
    }

    @Override
    public T next() {

        return null;
    }
}
