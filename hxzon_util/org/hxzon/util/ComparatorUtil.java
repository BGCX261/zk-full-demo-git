package org.hxzon.util;

import java.util.Arrays;
import java.util.Comparator;

public class ComparatorUtil {
    private static int cutoff = 1;

    //QuickSelect 将第k小的元素放在 a[k-1]
    //right，不包含
    public static <T> void quickSelectMinK(T a[], int k, int left, int right, Comparator<T> comp) {

        if (left + cutoff > right) {
            insertSort(a, left, right, comp);
            return;
        }
        T pivot = a[left];//median3(a[0], a[left], a[right - 1], comp);
        //取三数中值作为枢纽元，可以很大程度上避免最坏情况
        int i = left;
        int j = right - 1;
        for (;;) {
            while (i < right && comp.compare(a[i], pivot) < 0) {
                i++;
            }
            while (j >= left && comp.compare(a[j], pivot) > 0) {
                j--;
            }
            if (i < j) {
                swap(a, i, j);
            } else {
                break;
            }
        }
        //重置枢纽元
        //swap(a, i, right - 1);

        if (k <= i) {
            quickSelectMinK(a, k, left, i, comp);
        } else if (k > i + 1) {
            quickSelectMinK(a, k, i + 1, right, comp);
        }

    }

    public static <T> T median3(T a, T b, T c, Comparator<T> comp) {
        T bigger = comp.compare(a, b) > 0 ? a : b;
        return comp.compare(bigger, c) < 0 ? bigger : c;
    }

    public static <T> void swap(T a[], int i, int j) {
        T tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    public static <T> void insertSort(T a[], int left, int right, Comparator<T> comp) {
        Arrays.sort(a, left, right, comp);//right，不包含
    }

    //===========================
    //test
    private static class TestObj {
        private int i;

        public TestObj(int i) {
            this.i = i;
        }

        @Override
        public String toString() {
            return String.valueOf(i);
        }
    }

    private static class TestObjComparator implements Comparator<TestObj> {

        @Override
        public int compare(TestObj o1, TestObj o2) {
            return o1.i - o2.i;
        }

    }

    public static void main(String args[]) {
        TestObj[] objs = new TestObj[] { new TestObj(9), new TestObj(10), new TestObj(11), new TestObj(1),//
                new TestObj(8), new TestObj(5), new TestObj(2), new TestObj(12),//
                new TestObj(7), new TestObj(6), new TestObj(3), new TestObj(4), };
        quickSelectMinK(objs, 10, 0, objs.length, new TestObjComparator());
        for (TestObj obj : objs) {
            System.out.println(obj.i);
        }
    }
}
