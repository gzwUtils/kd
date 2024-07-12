package com.gzw.kd.learn.tree;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

/**
 * @author gzw
 * @description： 多级菜单
 * @since：2024/2/24 17:28
 */
@SuppressWarnings({"unchecked","rawtypes","unused"})
@Slf4j
public class TreeHandler {

    public static <T extends TreeNode> List<T> buildTree(List<T> dataList,String  topId) {
        return buildTree(dataList, (data) -> data, (item) -> true,topId);
    }

    public static <T extends TreeNode> List<T> buildTree(List<T> dataList, Function<T, T> map,String  topId) {
        return buildTree(dataList, map, (item) -> true,topId);
    }



    /**
     * 数据集合构建成树形结构
     *
     * @param dataList 数据集合
     * @param map      调用者提供 Function<T, T> 由调用着决定数据最终呈现形势
     * @param filter   调用者提供 Predicate<T> false 表示过滤 （ 注: 如果将父元素过滤掉等于剪枝 ）
     * @param <T>      extends ITreeNode
     * @return tt
     */
    public static <T extends TreeNode> List<T> buildTree(List<T> dataList,  Function<T, T> map, Predicate<T> filter,String  topId) {
        // 1. 过滤树节点数据 准备数据
        dataList = dataList.stream()
                .filter(filter)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        // 2.按照父ID分组  获取子节点集合
        Map<String, List<T>> collect = dataList.parallelStream().collect(Collectors.groupingBy(TreeNode::getParentId));

        //3.父子关系 list集合
        dataList.forEach(var -> var.setTreeNodeList(collect.get(var.getId())));

        //4、过滤出符合顶层父Id的所有数据，即所有数据都归属于一个顶层父Id
        return dataList.stream().filter(val -> topId.equals(val.getParentId())).map(map).collect(Collectors.toList());
    }
}
