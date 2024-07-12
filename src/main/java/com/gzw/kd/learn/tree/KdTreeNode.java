package com.gzw.kd.learn.tree;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description：
 * @since：2024/2/24 18:27
 */
@Accessors(chain = true)
@Data
public class KdTreeNode extends TreeNode<KdTreeNode>{

    /**
     * 姓名
     */
    private String name;


    /**
     * 树路径
     */
    private String treePath;


}
