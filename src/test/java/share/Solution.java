package share;

public class Solution {

    public static void main(String[] args) {

    }

    public String minRemoveToMakeValid(String s) {
        StringBuilder builder = new StringBuilder();
        int legalRIndex = -1;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ')' && i > legalRIndex) {
                continue;
            }

            if (s.charAt(i) == '(') {
                legalRIndex = Math.max(i + 1, legalRIndex);
                while (legalRIndex < s.length()) {
                    if (s.charAt(legalRIndex) == ')') {
                        builder.append(s.charAt(i));
                    }
                    legalRIndex++;
                }
            } else {
                builder.append(s.charAt(i));
            }
        }

        return builder.toString();
    }

    static class ListNode {
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
