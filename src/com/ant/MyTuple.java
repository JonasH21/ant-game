package com.ant;

import java.util.Objects;

class MyTuple<T,U> {
    T X; U Y;

    MyTuple(T x, U y){X=x;Y=y;}

    @Override
    public String toString(){return "("+X+","+Y+")";}

    @Override
    public boolean equals(Object o){
        if (!(o instanceof MyTuple))
            return false;

        return this.X.equals(((MyTuple)o).X) && this.Y.equals(((MyTuple)o).Y);
    }

    @Override
    public int hashCode(){
        return Objects.hash(X,Y);
    }
}

