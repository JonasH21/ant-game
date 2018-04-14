package com.ant;

import java.util.Collection;
import java.util.List;

/**
 * A pair which is in some sense connected to other pairs. Has additive functions and additive inverse.
 */
abstract class AdditivePair<T> extends MyPair<T> {
    AdditivePair(T x, T y) {super(x,y);}

    abstract List<? extends MyPair> getAccessiblePairs(boolean diagonalsAllowed);

    abstract AdditivePair<T> add(AdditivePair<T> o);
    abstract AdditivePair<T> inverse();
    final AdditivePair<T> subtract(AdditivePair<T> o){ return this.add(o.inverse()); }
}
