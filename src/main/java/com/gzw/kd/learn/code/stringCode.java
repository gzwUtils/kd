package com.gzw.kd.learn.code;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 项目名称：spring-demo
 * 类 名 称：Test
 * 类 描 述：string
 * 创建时间：2021/5/31 11:08 下午
 * @author gzw
 */
@SuppressWarnings("all")
public class stringCode {

    public  static int removeDuplicates(int[] nums) {
        Arrays.sort(nums);
        int l=0;
        for (int i = 0; i <nums.length ; i++) {
            l^=nums[i];
        }
       return l;
    }

    public static void reverseString(char[] s) {
        char ll;
        for (int i = 0; i < s.length / 2; i++) {
            ll = s[i];
            s[i] = s[s.length - 1 - i];
            s[s.length - 1 - i] = ll;
        }
    }

    public static int firstUniqChar(String s) {
        if (StringUtils.isBlank(s)) {
            return -1;
        }
        Map<String, Integer> map = new HashMap<>();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (map.containsKey(String.valueOf(chars[i]))) {
                return s.indexOf(String.valueOf(chars[i]));
            }
            map.put(String.valueOf(chars[i]), i);
        }
        return -1;
    }


    public static int test(int[] nums){
        HashSet<Integer> hashSet = new HashSet<>();
        int count = 1;
        for (int i = 0; i < nums.length; i++) {
            if (hashSet.contains(nums[i])) {
                ++count;
            }
            hashSet.add(nums[i]);
        }
        return nums.length - count;
    }


    public static boolean isAnagram(String s, String t) {
    if(s.length()!=t.length()){
        return false;
    }
    boolean flag=false;
    int [] a=new int[26];
    int [] b=new int [26];
        for (int i = 0; i <s.length() ; i++) {
            a[s.charAt(i)-'a']++;
            b[t.charAt(i)-'a']++;
        }
        for (int j = 0; j <26 ; j++) {
            if(a[j]==b[j]){
                flag=true;
            }else {
                break;
            }
        }
        return flag;
    }
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap(8);
        for (int i = 0; i < nums.length; i++) {
            int ll = target - nums[i];
            if (map.containsKey(ll)) {
                return new int[]{i, map.get(ll)};
            }
            map.put(nums[i], i);
        }
        return new int []{};
    }
    @SuppressWarnings({"Duplicates"})
    public static String stampToDateNew(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        long lt = new Long(s);
        Date date = new Date(lt);
        return simpleDateFormat.format(date);
    }

    public static boolean get(int [] arr,int len,int target){
        if(arr==null || len==0){
            return false;
        }
        if(len==1){
            return arr[0]==target;
        }
        for (int i = 0; i <arr.length ; i++) {
            if(arr[i]==target || get(Arrays.copyOfRange(arr, i + 1, len - 1),len-i-1,target-arr[i])){
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        System.out.println(stampToDateNew(String.valueOf(System.currentTimeMillis())));
        int [] ll=new int [] {1,3,4,6,8};
        int[] ints1 = twoSum(ll, 12);
        List<int[]> ints2 = Arrays.asList(ints1);
        System.out.println(JSON.toJSONString(ints2));
        int code = firstUniqChar("code");
        System.out.println(code);
        boolean anagram = isAnagram("anagram", "nagaram");
        System.out.println(anagram);
        int [] ll1 ={1,2,4,5};
        boolean b = get(ll1, ll1.length, 7);
        System.out.println(b+"=========================================================");
        int[] intss = new int[]{1, 0, 0,2,3,2,5,6,2};
        int tested = test(intss);
        System.out.println(tested);
        /**
         * HashMap springBoot Kafka es redis
         */
        ll();
        int[] ints = new int[]{1, 0, 0};
        int i = removeDuplicates(ints);
        System.out.println(i);
        System.out.println("--------------------------------");
        int[] array = {6,3,7,1,9,4,8,5,2,10};
        int low = 0,high = array.length - 1;
        quickRow(array,low,high);
        for (int q : array){
            System.out.println(q);
        }
    }

    private static void ll() {
        int i = 1;
        i = i++;
        int j = i++;
        int k = i + ++i * i++;
        System.out.println("i=" + i);
        System.out.println("j=" + j);
        System.out.println("k=" + k);
    }


    public static void quickRow(int [] ll,int low ,int high){
        if(low > high){
            return;
        }
        int i,j,prow;
        i = low;
        j = high;
        prow = ll[low];

        while (i < j){
            //从右往左找比节点小的数，循环结束要么找到了，要么i=j
            while (ll[j] >= prow && i < j){
                  j--;
            }
            //
            while (ll[i] <=prow && i<j){
                    i++;
            }
            //如果i!=j说明都找到了，就交换这两个数
            if (i <= j){
                int temp = ll[i];
                ll[i] = ll[j];
                ll[j] = temp;
            }

            ll[low] = ll[i];
            ll[i] = prow;
            //数组“分两半”,再重复上面的操作
            quickRow(ll,low,i - 1);
            quickRow(ll,i + 1,high);
        }
    }

}
