package com.ant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class SimpleState extends AbstractState{
    SimpleState(Coordinate antA, Coordinate antB){super(antA,antB);}

    @Override
    Collection<? extends SimpleState> getAccessibleStates(boolean diagonalsAllowed, boolean backtrackingAllowed, boolean lookForCrossing){
        return getAccessiblePairs(diagonalsAllowed);
    }

    @Override
    List<SimpleState> getAccessiblePairs(boolean diagonalsAllowed){
        List<SimpleState> accessibleSimpleStates = new ArrayList<>(64);
        List<Coordinate> aNeighbours = X.getNeighbours(diagonalsAllowed);
        List<Coordinate> bNeighbours = Y.getNeighbours(diagonalsAllowed);

        for(Coordinate aNeighbour : aNeighbours){
            for(Coordinate bNeighbour : bNeighbours){
                accessibleSimpleStates.add(new SimpleState(aNeighbour, bNeighbour));
            }
        }

        return accessibleSimpleStates;
    }

    @Override
    SimpleState add(AdditivePair<Coordinate> o){
        if (!(o instanceof  SimpleState))
            throw new IllegalArgumentException();

        return  new SimpleState(this.X.add(o.X), this.Y.add(o.Y));
    }

    @Override
    final SimpleState inverse(){
        return new SimpleState(X.inverse(), Y.inverse());
    }

    @Override
    boolean isAbsorbing(boolean lookForCrossing){
        return X.equals(Y);
    }


    /**
     * Given a SimpleState s, what are all of the potential States, i.e. where did the ants come from?
     * @param diagonalsAllowed Could the ants have come from a diagonal direction
     * @return All potential states for the SimpleState s
     */
    List<State> toTransientStates(boolean diagonalsAllowed, boolean lookForCrossing){
        List<Coordinate> legalDirections = new ArrayList<>();
        legalDirections.add(Coordinate.N);
        legalDirections.add(Coordinate.E);
        legalDirections.add(Coordinate.S);
        legalDirections.add(Coordinate.W);
        if(diagonalsAllowed) legalDirections.addAll(Coordinate.CornerAdjacents);
        List<State> states = new ArrayList<>((int)Math.pow(legalDirections.size(),2));


        for(Coordinate dA : legalDirections){
            for (Coordinate dB : legalDirections){
                if(Coordinate.validateCoordinate((Coordinate)X.subtract(dA)) && Coordinate.validateCoordinate((Coordinate)Y.subtract(dB))){
                    State s = new State(X,Y,dA,dB);
                    if(!s.isAbsorbing(lookForCrossing))
                        states.add(s);
                }
            }
        }

        return states;
    }
}
