package com.tallate.hotkey.util.cache;

import java.util.Map;

public class LRUCache<V> {

    class Node {
        private String key;
        private V val;
        Node prev, next;

        Node(String k, V v) {
            this.key = k;
            this.val = v;
            prev = null;
            next = null;
        }
    }

    private Map<String, Node> m;
    private Node head, tail;
    private int capacity;
    private int count = 0;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head = new Node(null, null);
        tail = new Node(null, null);
        head.next = tail;
        tail.prev = head;
    }

    public V get(String key) {
        Node node = m.get(key);
        if (node == null) {
            return null;
        }
        // 将该节点从链表中移除
        node.prev.next = node.next;
        node.next.prev = node.prev;
        // 加入到首部
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        return node.val;
    }

    public V put(String key, V val) {
        Node node = m.get(key);
        if (null == node) {
            Node newNode = new Node(key, val);
            if (count == capacity) {
                Node toDel = tail.prev;
                tail.prev = toDel.prev;
                toDel.prev.next = tail;
                m.put(toDel.key, null);
                count--;
            }
            m.put(key, newNode);
            newNode.next = head.next;
            newNode.prev = head;
            head.next.prev = newNode;
            head.next = newNode;
            count++;
            return null;
        }
        node.val = val;
        // 从队列中移除
        node.prev.next = node.next;
        node.next.prev = node.prev;
        // 加入到首部
        node.next = head.next;
        head.next.prev = node;
        node.prev = head;
        head.next = node;
        return node.val;
    }

}