package share;

public class Solution {

    public static void main(String[] args) {
       new Solution().solveSudoku(new char[][]{{'5','3','.','.','7','.','.','.','.'},{'6','.','.','1','9','5','.','.','.'},{'.','9','8','.','.','.','.','6','.'},{'8','.','.','.','6','.','.','.','3'},{'4','.','.','8','.','3','.','.','1'},{'7','.','.','.','2','.','.','.','6'},{'.','6','.','.','.','.','2','8','.'},{'.','.','.','4','1','9','.','.','5'},{'.','.','.','.','8','.','.','7','9'}});
    }


    boolean[][] used;
    public void solveSudoku(char[][] board) {
        //初始化当前行已用过的数字
        used = new boolean[board.length][board.length + 1];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != '.') {
                    used[i][board[i][j] - 48] = true;
                }
            }
        }

        bt(board,0,0);
    }

    boolean bt(char[][] board, int startRow, int col) {
        if (startRow == board.length) {
            return true;
        }

        if (col==board.length){
            return bt(board, startRow + 1, 0);
        }

        if (board[startRow][col] != '.') {
            return bt(board, startRow, col + 1);
        }

        for (int i = 1; i <= 9; i++) {
            if (!used[startRow][i] && valid(board, startRow, col, (char)(i+'0'))) {
                used[startRow][i]=true;
                board[startRow][col] = (char) (i + '0');
                boolean result=false;
                if(col==board.length-1){
                    result=bt(board, startRow + 1, 0);
                }else{
                    result=bt(board, startRow, col+1);
                }

                if(!result){
                    board[startRow][col] = '.';
                    used[startRow][i]=false;
                }else {
                    return true;
                }
            }
        }

        return false;
    }

    boolean valid(char[][] board, int startRow, int col, char num) {
        for (int i = 0; i < board.length; i++) {
            if (board[startRow][i] == num || board[i][col] == num) {
                return false;
            }
        }

        for (int i = (startRow / 3) * 3; i < (startRow / 3 + 1) * 3; i++) {
            for (int j = (col / 3) * 3; j < (col / 3 + 1) * 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }

        return true;
    }


    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }
}
