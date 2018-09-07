package com.chess.engine.board;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class boardUtils {

    public static final boolean[] FirstColumn=initColumn(0);
    public static final boolean[] SecondColumn=initColumn(1);
    public static final boolean[] SeventhColumn=initColumn(6);
    public static final boolean[] EightColumn=initColumn(7);

    public static final boolean[] EightRank = initRow(0);
    public static final boolean[] SeventhRank = initRow(8);
    public static final boolean[] SixthRank = initRow(16);
    public static final boolean[] FifthRank = initRow(24);
    public static final boolean[] FourthRank = initRow(32);
    public static final boolean[] ThirdRank = initRow(40);
    public static final boolean[] SecondRank = initRow(48);
    public static final boolean[] FirstRank = initRow(56);

    public static final int START_TILE_INDEX = 0;
    public static final int numTiles = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    public static final List<String> ALGEBRAIC_NOTATION = initialiseAlgebraicNotation();
    public static final Map<String,Integer > POSITION_TO_COORDINATE = initialisePositionToCoordinateMap();

    private static boolean[] initRow(int rowNumber)
    {
        final boolean[] row = new boolean[numTiles];
        do {
            row[rowNumber]=true;
            rowNumber++;
        }while(rowNumber% NUM_TILES_PER_ROW !=0);
        return row;
    }

    private static Map<String,Integer> initialisePositionToCoordinateMap()
    {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = START_TILE_INDEX; i < numTiles; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return ImmutableMap.copyOf(positionToCoordinate);
    }

    private static List<String> initialiseAlgebraicNotation()
    {
        return ImmutableList.copyOf(new String[] {
                "a8","b8","c8","d8","e8","f8","g8","h8",
                "a7","b7","c7","d7","e7","f7","g7","h7",
                "a6","b6","c6","d6","e6","f6","g6","h6",
                "a5","b5","c5","d5","e5","f5","g5","h5",
                "a4","b4","c4","d4","e4","f4","g4","h4",
                "a3","b3","c3","d3","e3","f3","g3","h3",
                "a2","b2","c2","d2","e2","f2","g2","h2",
                "a1","b1","c1","d1","e1","f1","g1","h1"
        });
    }



    private boardUtils()
    {
        throw new RuntimeException("Initialisation Failed! ");
    }

    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column =new boolean[64];
        do {
            column[columnNumber] = true;
            columnNumber+=8;
        }while (columnNumber<64);
        //by default all values are false
        return column;
    }

    public static boolean isValidTileCoordinate(int candidateDestinationCoordinate) {
        return candidateDestinationCoordinate >=0 && candidateDestinationCoordinate <64;
    }

    public static int getCoordinateAtPosition(final String position) {
        return POSITION_TO_COORDINATE.get(position);
    }

    public static String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }
}
