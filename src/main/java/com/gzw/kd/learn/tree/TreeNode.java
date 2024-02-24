package com.gzw.kd.learn.tree;


import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description： 树节点
 * @since：2024/2/24 17:25
 */

@Accessors(chain = true)
@Data
public class TreeNode<T> {


    private String id;

    private String parentId;

    private List<T> treeNodeList;

}
