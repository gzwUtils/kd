package com.gzw.kd.learn.code;

import java.util.Stack;

public class TestCode {


    /*
      识别有效括号
     */

    public static boolean isValid(String s) {
        if (s.isEmpty()) {
            return true;
        }
        s = s.trim();
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if ((c == ')' && top != '(') ||
                        (c == ']' && top != '[') ||
                        (c == '}' && top != '{')) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }


    /*
    大数相加
     */

    public String addStrings(String num1, String num2) {
        StringBuilder result = new StringBuilder();

        int i = num1.length() - 1;  // 从个位开始
        int j = num2.length() - 1;
        int carry = 0;  // 进位

        while (i >= 0 || j >= 0 || carry > 0) {
            int digit1 = i >= 0 ? num1.charAt(i) - '0' : 0;
            int digit2 = j >= 0 ? num2.charAt(j) - '0' : 0;

            int sum = digit1 + digit2 + carry;
            result.append(sum % 10);  // 取个位数
            carry = sum / 10;          // 计算进位

            i--;
            j--;
        }

        return result.reverse().toString();  // 反转得到正确顺序
    }

    /**
     * 搜索旋转排序数组
     */

    public int search(int[] nums, int target) {
        if (nums == null || nums.length == 0) return -1;

        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            }

            // 判断左半部分是否有序
            if (nums[left] <= nums[mid]) {  // 左半部分有序
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;  // target 在有序的左半部分
                } else {
                    left = mid + 1;   // target 在无序的右半部分
                }
            } else {  // 右半部分有序
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;   // target 在有序的右半部分
                } else {
                    right = mid - 1;  // target 在无序的左半部分
                }
            }
        }

        return -1;  // 没找到
    }


    public static void main(String[] args) {
        System.out.println(isValid("()"));
        System.out.println(isValid("()[]{}"));
        System.out.println(isValid("(]"));
        System.out.println(isValid("([)]"));
        System.out.println(isValid("{[]}"));
        System.out.println(isValid(""));
        System.out.println(isValid("([)]"));

        System.out.println(new TestCode().addStrings("11", "123"));
        System.out.println(new TestCode().addStrings("456", "77"));
        System.out.println(new TestCode().addStrings("0", "0"));


        System.out.println(new TestCode().search(new int[]{4,5,6,7,0,1,2}, 0));
        System.out.println(new TestCode().search(new int[]{4,5,6,7,0,1,2}, 3));
    }
}
