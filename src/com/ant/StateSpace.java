package com.ant;

import java.util.Iterator;

public class StateSpace<T extends AbstractState> implements Iterable<T> {
    public StateSpace(T x){

    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<T> iterator() {
        return new StateIterator(this);
    }

    public T getData(Coordinate x, Coordinate y){
        return null;
    }
}
