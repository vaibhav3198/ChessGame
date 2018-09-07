
package com.chess.engine.board;  //just to reorganise the classes
import com.chess.engine.pieces.piece;  //to resolve piece packages

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public abstract  class tile {
    protected final int tileCoordinate;

    private static final Map<Integer,EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    private static Map<Integer,EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Integer,EmptyTile> emptyTileMap =new HashMap<>();
        for(int i=0;i<64;i++)
        {
            emptyTileMap.put(i,new EmptyTile(i));
        }
        //return ImmutableMap.copyOf(emptyTileMap);  in case you need immutable, but first download guava library
        //return  emptyTileMap;
        return Collections.unmodifiableMap(emptyTileMap);  //still immutable but less than guava
    }

    public static tile createTile(final int tc,final piece p1)
    {
     return p1!=null ? new OccupiedTile(tc,p1):EMPTY_TILES_CACHE.get(tc);

    }

    private tile(int tc)
    {
        this.tileCoordinate=tc;
    }

    public abstract boolean isTileOccupied();
    public abstract piece getPiece();

    public int getTileCoordinate()
    {
        return this.tileCoordinate;
    }

    //two final classes that extends the above abstract class

    public static final class EmptyTile extends tile
    {
        private EmptyTile(final int tc)
        {
            super(tc);
        }

        @Override
        public String toString()
        {
            return "-";
        }


        @Override
        public boolean isTileOccupied()
        {
            return false;  //empty tile is not ocupied!
        }

        @Override
        public piece getPiece()
        {
            return null; //no piece on empty tile
        }
    }

    public static final class OccupiedTile extends tile
    {
        private final piece pieceOnTile;
        private OccupiedTile(final int tc,piece p1)
        {
            super(tc);
            this.pieceOnTile=p1;
        }

        @Override
        public String toString()
        {
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }

        @Override
        public boolean isTileOccupied()
        {
            return true;  //occupied tile is ocupied!
        }

        @Override
        public piece getPiece()
        {
            return this.pieceOnTile; //piece exist on occupied tile
        }
    }
}
