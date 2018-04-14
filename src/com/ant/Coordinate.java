package com.ant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A coordinate in a predefined grid size: contains problem-specific logic to get neighbours/velidation
 */
public class Coordinate extends AdditivePair<Integer> {
    static final Coordinate N = new Coordinate(0,1);
    static final Coordinate E = new Coordinate(1,0);
    static final Coordinate S = new Coordinate(0,-1);
    static final Coordinate W = new Coordinate(-1,0);
    static final Coordinate O = new Coordinate(0,0);
    static final Collection<Coordinate> CornerAdjacents = new ArrayList<Coordinate>(4){{add(N.add(E));add(N.add(W));add(S.add(E));add(S.add(W));}};

    Coordinate(int x, int y){super(x,y);}

    @Override
    List<Coordinate> getAccessiblePairs(boolean diagonalsAllowed){
        List<Coordinate> neighbours = new ArrayList<>(8);

        //here i check potential neighbours, abusing the fact that they will lie in the 3x3 box surrounding
        //the coordinate (in the context of this problem). I verify that (a) the distance is 1, (b) the coordinate is
        //valid (in the grid)
        List<Coordinate> candidateNeighbours = new ArrayList<>();
        candidateNeighbours.add(this.add(N));
        candidateNeighbours.add(this.add(E));
        candidateNeighbours.add(this.add(S));
        candidateNeighbours.add(this.add(W));

        if (diagonalsAllowed){
            candidateNeighbours.add(this.add(N.add(E)));
            candidateNeighbours.add(this.add(N.add(W)));
            candidateNeighbours.add(this.add(S.add(E)));
            candidateNeighbours.add(this.add(S.add(W)));
        }

        for (Coordinate candidateNeighbour : candidateNeighbours)
            if(validateCoordinate(candidateNeighbour))
                neighbours.add(candidateNeighbour);

        return neighbours;
    }

    List<Coordinate> getNeighbours(boolean diagonalsAllowed){
        return getAccessiblePairs(diagonalsAllowed);
    }

    static boolean validateCoordinate(Coordinate c){
        return !(c.X < 0 || c.Y < 0 || c.X >7 || c.Y > 7);
    }

    /**
     * Check if the parity of the coordinates match. Parity(c,d) = Parity(c) + Parity(d)
     * EVEN+ODD=ODD, ODD+EVEN=ODD, ODD+ODD=EVEN, EVEN+EVEN=EVEN
     *
     * @return 0 (false) for even parity/match, 1 (true) for odd parity/different
     */
    static boolean parity(Coordinate c, Coordinate d){
        return c.parity() != d.parity();
    }

    /**
     * Parity(x,y) = Parity(x) + Parity(y).
     * EVEN+ODD=ODD, ODD+EVEN=ODD, ODD+ODD=EVEN, EVEN+EVEN=EVEN
     *
     * Can also be considered to be checking if the chessboard square is black or white.
     * @return 0 (false) for even parity, 1 (true) for odd parity
     */
    private boolean parity(){
        return 0 != (X + Y )%2;
    }

    /**
     * Adds another coordinate direction to the coordinate.
     * @param o
     * @return this coordinate plus direction
     */
    @Override
    Coordinate add(AdditivePair o){
        if (!(o instanceof Coordinate))
            throw new IllegalArgumentException("Expected coordinate as argument for coordinate.add(c)");
        return new Coordinate(X + ((Coordinate) o).X, Y + ((Coordinate) o).Y);
    }

    @Override
    Coordinate inverse(){
        return new Coordinate(-X, -Y);
    }

    /**
     * code() for the quadrant/axis of the coordinate, starting at N axis and going clockwise
     * @return
     * positive y 0
     * positive x 1
     * negative y 2
     * negative x 3
     * NEquadrant 4
     * SEquadrant 5
     * SWquadrant 6
     * NWquadrant 7
     * origin 8
     */
    int code(){
        //TODO I am not happy with this, should be changed if time
        if(X==0){
            if(Y==0) return 8;
            if(Y>0) return 0;
            if(Y<0) return 2;
        }
        if(X>0){
            if(Y==0) return 1;
            if(Y>0) return 4;
            if(Y<0) return 5;
        }
        if(X<0){
            if(Y==0) return 3;
            if(Y>0) return 7;
            if(Y<0) return 6;

        }
        return  -1;
    }

    static Coordinate decode(int code){
        code = Math.abs(code % 9);
        switch(code){
            case 0: return N;
            case 1: return E;
            case 2: return S;
            case 3: return W;
            case 4: return N.add(E);
            case 5: return S.add(E);
            case 6: return S.add(W);
            case 7: return N.add(W);
            case 8: return O;
        }

        return O;
    }
}
