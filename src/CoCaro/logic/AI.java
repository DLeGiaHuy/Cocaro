package CoCaro.logic;

import javax.swing.*;
import java.util.Random;

public class AI {

    public static int[] getMove(JButton[][] board, int difficulty) {
        Random rand = new Random();

        // DỄ - đi ngẫu nhiên
        if (difficulty == 1) {
            return randomMove(board, rand);
        }

        // THƯỜNG - 50% ngẫu nhiên, 50% thông minh
        else if (difficulty == 2) {
            if (rand.nextBoolean()) {
                return randomMove(board, rand);
            } else {
                return findBest(board);
            }
        }

        // KHÓ - luôn tìm nước tốt nhất
        else {
            return findBest(board);
        }
    }

    private static int[] randomMove(JButton[][] board, Random rand) {
        // Tránh vòng lặp vô tận: thu thập ô trống trước
        java.util.List<int[]> empty = new java.util.ArrayList<>();
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                if (board[i][j].getText().equals(""))
                    empty.add(new int[]{i, j});

        if (empty.isEmpty()) return new int[]{0, 0};
        return empty.get(rand.nextInt(empty.size()));
    }

    private static int[] findBest(JButton[][] board) {
        int best = Integer.MIN_VALUE;
        int r = -1, c = -1;

        // Ưu tiên tìm trong vùng có quân để tránh đi ô góc xa
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (!board[i][j].getText().equals("")) continue;

                // Chỉ xét ô gần quân đã đánh (trong vòng 2 ô)
                if (!hasNeighbor(board, i, j, 2)) continue;

                int score = evaluate(board, i, j);
                if (score > best) {
                    best = score;
                    r = i;
                    c = j;
                }
            }
        }

        // Nếu bàn cờ trống hoàn toàn, đánh vào giữa
        if (r == -1) {
            r = board.length / 2;
            c = board[0].length / 2;
        }

        return new int[]{r, c};
    }

    /** Kiểm tra xem ô (r,c) có quân nào trong vòng radius ô không */
    private static boolean hasNeighbor(JButton[][] board, int r, int c, int radius) {
        for (int i = r - radius; i <= r + radius; i++) {
            for (int j = c - radius; j <= c + radius; j++) {
                if (i < 0 || i >= board.length || j < 0 || j >= board[i].length) continue;
                if (i == r && j == c) continue;
                if (!board[i][j].getText().equals("")) return true;
            }
        }
        return false;
    }

    private static int evaluate(JButton[][] board, int r, int c) {
        // Tấn công (O) ưu tiên hơn phòng thủ (X)
        int attack = countLine(board, r, c, 'O') * 3;
        int defend = countLine(board, r, c, 'X') * 2;
        return attack + defend;
    }

    /** Đếm số quân liên tiếp nhiều nhất theo 4 hướng tại ô (r,c) nếu đặt quân p */
    private static int countLine(JButton[][] board, int r, int c, char p) {
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        int max = 0;
        String mark = "" + p;

        for (int[] d : dirs) {
            int count = 0;

            // chiều thuận
            int nr = r + d[0], nc = c + d[1];
            while (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length
                    && board[nr][nc].getText().equals(mark)) {
                count++;
                nr += d[0]; nc += d[1];
            }

            // chiều ngược
            nr = r - d[0]; nc = c - d[1];
            while (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length
                    && board[nr][nc].getText().equals(mark)) {
                count++;
                nr -= d[0]; nc -= d[1];
            }

            if (count > max) max = count;
        }
        return max;
    }
}