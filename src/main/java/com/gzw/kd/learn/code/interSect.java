package com.gzw.kd.learn.code;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 项目名称：spring-demo
 * 类 名 称：Test1
 * 类 描 述：code
 * 创建时间：2021/9/23 2:35 下午
 *
 * @author gzw
 */
@SuppressWarnings("all")
public class interSect {

    public static final Pattern URL_PATTERN = Pattern.compile("(http|https):\\/\\/([\\w.]+\\/?)\\S*");

    public static void main(String[] args) {
        int [] ll={0,1,0,3};
        int [] lt={2,4,6,8,3,1};
        int[] intersect = intersect(ll, lt);
        for (int i = 0; i <intersect.length ; i++) {
            System.out.println(intersect[i]);
        }
        List list = moveZeroes(ll);
        System.out.println(list.toString());
    }

    public static int[] intersect(int[] nums1, int[] nums2) {
        Map<Object, Object> hashMap = new HashMap<>();
        Arrays.sort(nums1);
        Arrays.sort(nums2);
        List<Integer> list=new ArrayList<Integer>();
        for (int i = 0,  j=0; i <nums1.length && j<nums2.length;) {
            if(nums1[i]==nums2[j]){
                list.add(nums2[j]);
                i++;
                j++;
            }else if(nums1[i]<nums2[j]){
                i++;
            }else {
                j++;
            }
        }
        int [] ls=new int [list.size()];
        for (int i = 0; i <list.size() ; i++) {
            ls[i]=list.get(i);
        }
        return ls;
    }
    public static List moveZeroes(int[] nums) {
        if(nums.length==0){
            return Collections.EMPTY_LIST;
        }
        int j=0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[j++] = nums[i];
            }
        }
        for (int i = j; i <nums.length ; i++) {
            nums[i]=0;
        }
        List<Integer> collect = Arrays.stream(nums).boxed().collect(Collectors.toList());
        return collect;
    }

    /**
     * 根据时间判断星期
     *
     * @param date
     * @return
     */
    public  static Integer get(String date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            simpleDateFormat.setLenient(false);
            Date newDate = simpleDateFormat.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(newDate);
            int i = c.get(Calendar.DAY_OF_WEEK) - 1;
            return i;
        } catch (ParseException e) {
            throw new RuntimeException("日期转换失败");
        }

    }
}
