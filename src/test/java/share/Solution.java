package share;

import java.util.*;

public class Solution {

    public static void main(String[] args) {
        int[][] classes=new int[][]{{1,2},{3,5},{2,2}};
        System.out.println(new Solution().maxAverageRatio(classes, 2));
    }

    public double maxAverageRatio(int[][] classes, int extraStudents) {

        //尝试把每个学生分配到每个班，找下通过率提升最大的班
        int luckyClassIndex = 0;
        double maxUpRation = 0;
        for (int i = 0; i < extraStudents; i++) {
            for (int j = 0; j < classes.length; j++) {
                double upRation = (double) (classes[j][0] + 1) / (classes[j][1] + 1) - (double) classes[j][0] / classes[j][1];
                if (upRation > maxUpRation) {
                    luckyClassIndex = j;
                    maxUpRation = upRation;
                }
            }
            classes[luckyClassIndex][0]++;
            classes[luckyClassIndex][1]++;
            maxUpRation=0;
            luckyClassIndex=0;
        }

//        //统计通过率
//        double sum=0;
//        for (int i = 0; i < classes.length; i++) {
//            sum+=classes[i][0]/classes[i][1];
//        }
//        return sum/classes.length;
        return Arrays.stream(classes).mapToDouble(i -> (double) i[0] / i[1]).average().getAsDouble();
    }

    private List<List<Integer>> result = new LinkedList<List<Integer>>();
    private List<Integer> item = new LinkedList<>();
    private int[] numsUsed = null;

    public List<List<Integer>> permute(int[] nums) {
        numsUsed = new int[nums.length];
        backTracking(nums);
        return result;
    }

    private void backTracking(int[] nums) {
        if (item.size() == nums.length) {
            List<Integer> itemResult = new LinkedList<>();
            itemResult.addAll(item);
            return;
        }


        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                item.add(nums[i]);
                numsUsed[i] = 1;
                backTracking(nums);
                item.remove(item.size() - 1);
                numsUsed[i] = 0;
            }
        }
    }

    public static void heapSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            buildHeap(arr, arr.length - 1 - i);
            swap(arr, 0, arr.length - 1 - i);
        }
    }

    private static void buildHeap(int[] arr, int end) {
        int currentRootIndex = Math.min(arr.length / 2 - 1, end - 1);

        while (currentRootIndex >= 0) {
            buildHeapNode(arr, currentRootIndex, end);
            currentRootIndex--;
        }
    }

    private static void buildHeapNode(int[] arr, int index, int end) {
        if (index >= end) {
            return;
        }

        int leftChildIndex = 2 * index + 1;
        int rightChildIndex = leftChildIndex + 1;
        if (rightChildIndex <= end && arr[rightChildIndex] > arr[leftChildIndex] && arr[rightChildIndex] > arr[index]) {
            swap(arr, rightChildIndex, index);
            buildHeapNode(arr, rightChildIndex, end);
        } else if (leftChildIndex <= end && arr[leftChildIndex] > arr[index]) {
            swap(arr, leftChildIndex, index);
            buildHeapNode(arr, leftChildIndex, end);
        }
    }

    public static void mergeSort(int[] arr, int low, int high) {
        if (low >= high) {
            return;
        }

        int middle = low + (high - low) / 2;
        mergeSort(arr, low, middle);
        mergeSort(arr, middle + 1, high);

        int fi = low, fj = middle, si = middle + 1, sj = high;
        int[] tmpArr = new int[high - low + 1];
        int tmpIndex = 0;

        while (fi <= fj && si <= sj) {
            if (arr[fi] < arr[si]) {
                tmpArr[tmpIndex] = arr[fi];
                fi++;
            } else {
                tmpArr[tmpIndex] = arr[si];
                si++;
            }
            tmpIndex++;
        }

        if (fi <= fj) {
            while (tmpIndex < tmpArr.length) {
                tmpArr[tmpIndex++] = arr[fi++];
            }
        }

        if (si <= sj) {
            while (tmpIndex < tmpArr.length) {
                tmpArr[tmpIndex++] = arr[si++];
            }
        }

        for (int i = 0; i < tmpArr.length; i++) {
            arr[low] = tmpArr[i];
            low++;
        }
    }

    public static void quickSort(int[] arr, int low, int high) {
        if (low >= high) {
            return;
        }

        int i = low;
        int j = high;
        int value = arr[low];

        while (i < j) {
            while (i < j && arr[j] > value) {
                j--;
            }
            if (i < j) {
                arr[i] = arr[j];
                i++;
            }

            while (i < j && arr[i] < value) {
                i++;
            }
            if (i < j) {
                arr[j] = arr[i];
                j--;
            }
        }

        arr[i] = value;
        quickSort(arr, low, i);
        quickSort(arr, i + 1, high);
    }

    public static void insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    swap(arr, j, j - 1);
                }
            }
        }
    }

    public static void selectSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int minIndex = i;
            for (int j = i; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            swap(arr, minIndex, i);
        }
    }

    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    private static void swap(int[] arr, int i, int j) {
        if (i != j) {
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }


}
