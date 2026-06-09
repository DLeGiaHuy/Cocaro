package CoCaro.logic;

import javax.swing.*;

public class GameLogic {

    public static boolean checkWin(JButton[][] board, int winCondition) {
        int size = board.length;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                String player = board[r][c].getText();
                if (player.equals("")) continue;

                if (checkDirection(board, r, c, 0,  1, player, winCondition)) return true; // ngang
                if (checkDirection(board, r, c, 1,  0, player, winCondition)) return true; // dọc
                if (checkDirection(board, r, c, 1,  1, player, winCondition)) return true; // chéo xuống
                if (checkDirection(board, r, c,-1,  1, player, winCondition)) return true; // chéo lên
            }
        }
        return false;
    }

    /**
     * Trả về true nếu ô (row,col) nằm trong chuỗi thắng của player.
     * Kiểm tra đủ 4 hướng: ngang, dọc, chéo \.
     */
    public static boolean isWinningCell(
            JButton[][] board,
            int row,
            int col,
            char player,
            int winCondition) {

        int[][] directions = { {0,1}, {1,0}, {1,1}, {1,-1} };

        for (int[] d : directions) {
            if (countLine(board, row, col, d[0], d[1], player) >= winCondition)
                return true;
        }
        return false;
    }

    /**
     * Đếm số ô liên tiếp của player đi qua (row,col) theo hướng (dr,dc) và ngược lại.
     */
    private static int countLine(JButton[][] board, int row, int col,
                                  int dr, int dc, char player) {
        int size  = board.length;
        String p  = "" + player;
        int count = 1; // tính ô hiện tại

        // đi theo chiều thuận
        int r = row + dr, c = col + dc;
        while (r >= 0 && r < size && c >= 0 && c < size && board[r][c].getText().equals(p)) {
            count++;
            r += dr; c += dc;
        }

        // đi theo chiều ngược
        r = row - dr; c = col - dc;
        while (r >= 0 && r < size && c >= 0 && c < size && board[r][c].getText().equals(p)) {
            count++;
            r -= dr; c -= dc;
        }

        return count;
    }

    private static boolean checkDirection(
            JButton[][] board,
            int r, int c,
            int dr, int dc,
            String player,
            int winCondition) {

        int size = board.length;
        for (int i = 0; i < winCondition; i++) {
            int nr = r + dr * i;
            int nc = c + dc * i;
            if (nr < 0 || nc < 0 || nr >= size || nc >= size) return false;
            if (!board[nr][nc].getText().equals(player))       return false;
        }
        return true;
    }

    public static boolean isFull(JButton[][] board) {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][j].getText().equals("")) return false;
        return true;
    }
}