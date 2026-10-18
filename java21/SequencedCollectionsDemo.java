// JEP 431: Sequenced Collections
// Run: java SequencedCollectionsDemo.java

import java.util.*;

public class SequencedCollectionsDemo {

    public static void main(String[] args) {
        list();
        deque();
        set();
        map();
        reversedIsView();
    }

    static void list() {
        System.out.println("=== List ===");
        List<Integer> list = new ArrayList<>(List.of(0, 1, 2));
        // [ja] 従来
        // [zh] 以前的写法
        // [en] Before
        System.out.println("get(0) / get(size()-1) : " + list.get(0) + " / " + list.get(list.size() - 1));
        // [ja] Java 21
        // [zh] Java 21
        // [en] Java 21
        System.out.println("getFirst() / getLast() : " + list.getFirst() + " / " + list.getLast());
        System.out.println("reversed()             : " + list.reversed() + "  original: " + list);
        // [ja] 従来の Collections.reverse は元のリストを書き換える
        // [zh] 以前的 Collections.reverse 会直接修改原列表
        // [en] The old Collections.reverse modifies the list itself
        List<Integer> copy = new ArrayList<>(list);
        Collections.reverse(copy);
        System.out.println("Collections.reverse    : " + copy + "  (the copy itself was modified)");
    }

    static void deque() {
        System.out.println("=== Deque ===");
        Deque<Integer> deque = new ArrayDeque<>(List.of(0, 1, 2));
        // [ja] 従来：逆順は descendingIterator で取り出す
        // [zh] 以前的写法：用 descendingIterator 倒序取出
        // [en] Before: reverse order through descendingIterator
        List<Integer> rev = new ArrayList<>();
        deque.descendingIterator().forEachRemaining(rev::add);
        System.out.println("descendingIterator     : " + rev);
        // [ja] Java 21
        // [zh] Java 21
        // [en] Java 21
        System.out.println("reversed()             : " + deque.reversed() + "  original: " + deque);
    }

    static void set() {
        System.out.println("=== LinkedHashSet ===");
        LinkedHashSet<Integer> set = new LinkedHashSet<>(List.of(0, 1, 2));
        // [ja] 従来：最後の要素はループで最後まで回すしかない
        // [zh] 以前的写法：最后一个元素只能循环到底才能拿到
        // [en] Before: the only way to the last element is to loop to the end
        Integer last = null;
        for (Integer x : set) last = x;
        System.out.println("iterator().next() / loop : " + set.iterator().next() + " / " + last);
        // [ja] Java 21
        // [zh] Java 21
        // [en] Java 21
        System.out.println("getFirst() / getLast()   : " + set.getFirst() + " / " + set.getLast());
        System.out.println("reversed()               : " + set.reversed());
        // [ja] 先頭に追加（すでにあれば先頭へ移動）
        // [zh] 加到最前面（已存在的元素会移到最前面）
        // [en] Add at the front (an existing element moves to the front)
        set.addFirst(9);
        System.out.println("addFirst(9)              : " + set);
    }

    static void map() {
        System.out.println("=== LinkedHashMap ===");
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        map.putLast(0, "zero");
        map.putLast(1, "one");
        map.putLast(2, "two");
        System.out.println("map                    : " + map);
        System.out.println("firstEntry / lastEntry : " + map.firstEntry() + " / " + map.lastEntry());
        System.out.println("reversed()             : " + map.reversed());
        // [ja] 既存のキーは先頭へ移動
        // [zh] 已存在的键会移到最前面
        // [en] An existing key moves to the front
        map.putFirst(1, "ONE");
        System.out.println("putFirst(1, \"ONE\")     : " + map);
        System.out.println("pollLastEntry()        : " + map.pollLastEntry() + "  remaining: " + map);
    }

    static void reversedIsView() {
        System.out.println("=== reversed() is a view ===");
        Deque<Integer> deque = new ArrayDeque<>(List.of(0, 1, 2));
        SequencedCollection<Integer> view = deque.reversed();
        // [ja] 逆順ビューの先頭 = 元の末尾
        // [zh] 倒序视图的开头 = 原集合的末尾
        // [en] The front of the reversed view = the end of the original
        view.addFirst(9);
        System.out.println("after view.addFirst(9) : view=" + view + "  original=" + deque);
    }
}
