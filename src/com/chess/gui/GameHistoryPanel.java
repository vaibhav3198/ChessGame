package com.chess.gui;

import com.chess.engine.alliance;
import com.chess.engine.board.board;
import com.chess.engine.board.move;
import com.sun.rowset.internal.Row;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import static com.chess.gui.Table.*;

public class GameHistoryPanel extends JPanel {

    private final DataModel model;
    private final JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100,400);

    GameHistoryPanel()
    {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);  //enable scroll if it exceeds screen/panel
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane,BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final board b, final MoveLog moveHistory){
        int currentRow=0;
        this.model.clear();
        for(final move m: moveHistory.getMoves())
        {
            final String moveText = m.toString();
            if(m.getMovedPiece().getPieceAlliance().isWhite())
            {
                this.model.setValueAt(moveText,currentRow,0);
            }
            else if(m.getMovedPiece().getPieceAlliance().isBlack())
            {
                this.model.setValueAt(moveText,currentRow,1);
                currentRow++;
            }
        }
        if(moveHistory.getMoves().size()>0)
        {
            final move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);  //last move played
            final String moveText = lastMove.toString();

            if(lastMove.getMovedPiece().getPieceAlliance().isWhite())
            {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(b),currentRow,0);  //+ for check
            }
            else if(lastMove.getMovedPiece().getPieceAlliance().isBlack())
            {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(b),currentRow -1,1);
            }
        }
        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());//if moves exceeds scroll panel, it auto exceeds
    }

    private String calculateCheckAndCheckMateHash(final board b) {
        if(b.currentPlayer().isInCheckMate())
        {
            return "#";
        }
        else if(b.currentPlayer().isInCheck())
        {
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel{
        private final List<GameHistoryPanel.Row> values;  //v39:1200
        private static final String[] NAMES = {"White","Black"};

        DataModel()
        {
            this.values = new ArrayList<>();
        }

        public void clear()
        {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount()
        {
            if(this.values==null)
            {
                return 0;
            }
            return this.values.size();
        }
        @Override
        public int getColumnCount()
        {
            return NAMES.length;
        }
        @Override
        public Object getValueAt(final int row, final int column)
        {
            final GameHistoryPanel.Row currentRow = this.values.get(row);
            if(column==0)
            {
                return currentRow.whiteMove;
            }
            else if(column==1)
            {
                return currentRow.blackMove;
            }
            else {
                return null;  //doesnt enter here if working properly
            }
        }

        @Override
        public void setValueAt(final Object aValue, final int row,final int column) //v39:1728
        {
            final GameHistoryPanel.Row currentRow;
            if(this.values.size()<=row)
            {
                currentRow=new GameHistoryPanel.Row();
                this.values.add(currentRow);
            }
            else {
                currentRow=this.values.get(row);
            }
            if(column==0)
            {
                currentRow.setWhiteMove((String)aValue);
                fireTableRowsInserted(row,row);
            }
            else if(column==1)
            {
                currentRow.setBlackMove((String)aValue);
                fireTableCellUpdated(row,column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column)
        {
            return move.class;
        }

        @Override
        public String getColumnName(final int column)
        {
            return NAMES[column];
        }
    }

    private static class Row{
        private String whiteMove;
        private String blackMove;

        Row()
        {

        }

        public String getWhiteMove() {
            return whiteMove;
        }

        public String getBlackMove() {
            return blackMove;
        }

        public void setWhiteMove(final String whiteMove)
        {
            this.whiteMove = whiteMove;
        }

        public void setBlackMove(String blackMove) {
            this.blackMove = blackMove;
        }
    }
}
