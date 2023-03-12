package com.gzw.kd.learn.code;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 项目名称：spring-demo
 * 类 名 称：gzwNodeList
 * 类 描 述：链表
 * 创建时间：2021/11/18 6:35 下午
 *
 * @author gzw
 */
@SuppressWarnings("all")
public class gzwNodeList<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        LinkedList<Object> objects = new LinkedList<>();
        objects.add("ls");
        System.out.println(objects.get(0));
        gzwNodeList<Object> objectgzwNodeList = new gzwNodeList<>();
        objectgzwNodeList.addLast("gzw");
        objectgzwNodeList.addLast("zhs");
        Object o = objectgzwNodeList.get(0);
        Object o1 = objectgzwNodeList.get(1);
        System.out.println(o);
        System.out.println(o1);
    }

    transient int size=1;

    transient Node<T> first;

    transient Node<T> last;

    public gzwNodeList(){
    }

    Node<T> node(int index) {
        // assert isElementIndex(index);

        if (index < (size >> 1)) {
            Node<T> x = first;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
            return x;
        } else {
            Node<T> x = last;
            for (int i = size - 1; i > index; i--) {
                x = x.pre;
            }
            return x;
        }
    }
    public T get(int index) {
        return  node(index).item;
    }
      void addLast(T e){
        final  Node<T> l=last;
        final Node<T> newNode =new Node<>(l,e,null);
        last=newNode;
        if(l==null){
            first=newNode;
        }else {
            l.next=newNode;
            size++;
        }
    }


    void addFirst(T e){
        final  Node<T> l=first;
        final Node<T> newNode =new Node<>(null,e,l);
        first=newNode;
        if(l==null){
            last=newNode;
        }else {
            l.pre=newNode;
            size++;
        }
    }
    private static class Node<T>{
        T item;
        Node<T> next;
        Node<T> pre;

        public Node(Node<T> pre,T item, Node<T> next) {
            this.item = item;
            this.next = next;
            this.pre = pre;
        }
    }
}
