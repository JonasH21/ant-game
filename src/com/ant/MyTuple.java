package com.ant;

import java.util.Objects;

public class MyTuple<T,U> {
    public T X; public U Y;

    MyTuple(T x, U y){X=x;Y=y;}

    @Override
    public String toString(){return "("+X+","+Y+")";}

    @Override
    public boolean equals(Object o){
        if (!(o instanceof MyTuple))
            return false;

        if(this.Y == null || ((MyTuple)o).Y == null)
            return this.Y == ((MyTuple)o).Y;

        return this.X.equals(((MyTuple)o).X) && this.Y.equals(((MyTuple)o).Y);
    }

    @Override
    public int hashCode(){
        return Objects.hash(X,Y);
    }
}

