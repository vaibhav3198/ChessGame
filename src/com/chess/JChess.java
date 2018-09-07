package com.chess;

import com.chess.engine.board.board;
import com.chess.gui.Table;

public class JChess { //driver class
    public static void main(String[] args)
    {
        board chessBoard = board.createStandardBoard();

        System.out.println(chessBoard);
        Table.get().show();
    }
}
