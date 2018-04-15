package com.ant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simplestate (i.e. pair of coordinates), enriched with Direction dir specifying the direction of origin.
 * The direction is a pair of deltas, so dir = ((0,1),(1,0)) suggests that AntA came from the S and AntB came from
 * the W
 */
public class State extends SimpleState {
    MyPair<Coordinate> directionOfTravel;

    public State(Coordinate x, Coordinate y, Coordinate dirX, Coordinate dirY){
        super(x,y);
        directionOfTravel = new SimpleState(dirX, dirY);
    }

    State(SimpleState current, SimpleState previous){
        super(current.X, current.Y);
        directionOfTravel = (current.subtract(previous));
    }


    @Override
    public MyPair<Coordinate> getState(int index){
        Coordinate position, directionOfTravel;
        if(index == 0){
            position = X;
            directionOfTravel = this.directionOfTravel.X;
        }
        else if(index == 1){
            position = Y;
            directionOfTravel = this.directionOfTravel.Y;
        }
        else
            throw new IllegalArgumentException("index must be 0 or 1 (for X or Y respectively)");

        return new MyPair<>(position, directionOfTravel);
    }



    @Override
    Collection<State> getAccessibleStates(boolean diagonalsAllowed, boolean backtrackingAllowed, boolean lookForCrossing){
        SimpleState prev = new SimpleState(X,Y);
        Collection<State> states = new ArrayList<>(64);


        for(Coordinate xNeighbour : X.getNeighbours(diagonalsAllowed)){
            for(Coordinate yNeighbour : Y.getNeighbours(diagonalsAllowed)){
                SimpleState next = new SimpleState(xNeighbour,yNeighbour);
                State s = new State(next, prev);
                if(lookForCrossing
                        && next.X.equals(prev.Y) && next.Y.equals(prev.X))
                    continue;
                if (backtrackingAllowed
                        ||!( s.directionOfTravel.X.equals(this.directionOfTravel.X.inverse()) //has X back-tracked?
                        || s.directionOfTravel.Y.equals(this.directionOfTravel.Y.inverse()) //has Y "?
                ))
                    states.add(s);
                //TODO optimise and/or use isAbsorbing method
            }
        }

        return states;
    }


    @Override
    public boolean isAbsorbing(boolean lookForCrossing){
        if (!lookForCrossing)
            return super.isAbsorbing(lookForCrossing);

        return Y.add(directionOfTravel.X).equals(X) //X's previous position was where Y is now
                && X.add(directionOfTravel.Y).equals(Y); //Y's previous position was where X is now
    }

    @Override
    public String toString(){
        return "(" + super.toString() + ", " + directionOfTravel + ")";
    }


    @Override
    public boolean equals(Object o){
        if (!(o instanceof State))
            return false;

        return super.equals(o) && ((State)o).directionOfTravel.equals(this.directionOfTravel);
    }

    public MyPair<Coordinate> getCoordinates(){
        return new MyPair<>(X,Y);
    }

    /**
     * For an 8x8 grid with 8 possible directions of travel, this encodes the state as an integer 0 \le n \le 8^6
     * @return For a state w=(((AX,AY),(BX,BY)), (dA,dB)),
     * f(w) = AX*8^0 + AY*8^1 + BX*8^2 + BY*8^3 + dA*8^4 + dB*8^4,
     * using the integer coordinates for AX,AY,BX,BY and the code() function for coordinates dA,dB.
     */
    int encode(){
        return (int)Math.min(Math.pow(8,6), X.X + X.Y*8 + Y.X*Math.pow(8,2) + Y.Y*Math.pow(8,3)
                + directionOfTravel.X.code()*Math.pow(8,4) + directionOfTravel.Y.code()*Math.pow(8,5));
    }

    static State decode(int code){
        if(code < 0 || code > Math.pow(8,6))
            throw new IllegalArgumentException();

        if(code == Math.pow(8,6))
            return new State(new Coordinate(0,0), new Coordinate(7,7), Coordinate.O, Coordinate.O);

        int AX = code % 8;
        int AY = code % (int)Math.pow(8,1);
        int BX = code % (int)Math.pow(8,2);
        int BY = code % (int)Math.pow(8,3);
        Coordinate dA = Coordinate.decode(code % (int)Math.pow(8,4));
        Coordinate dB = Coordinate.decode(code % (int)Math.pow(8,5));

        return new State(new Coordinate(AX,AY), new Coordinate(BX,BY), dA,dB);
    }
}
