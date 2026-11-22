import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Solution6（LeetCode 1452 收藏清单）的单元测试类
 * 学号：2024113155
 *
 * ==================== 测试用例设计总体原则 ====================
 * 1. 等价类划分原则：
 *    将输入划分为有效等价类与无效等价类，从每个等价类中选取代表性用例。
 *    - 有效等价类：存在子集关系的清单集合、不存在任何子集关系的清单集合、
 *      只含一个用户的清单（最小有效输入）、清单长度/用户数处于不同规模的集合。
 *    - 本题题目约束保证输入合法（字符串仅小写字母、各清单互不相同），
 *      故不针对非法输入设计用例，重点覆盖不同"子集关系拓扑结构"的等价类。
 * 2. 边界值分析原则：
 *    对输入规模的上下边界及边界附近取值设计用例：
 *    - 用户数 n 的最小值（n = 1）；
 *    - 清单长度的最小值（长度为 1 的清单作为最长清单的子集，是最小的"可子集"单元）；
 *    - 两个清单互不为子集（子集判定为否的边界情形）。
 * 3. 覆盖原则：
 *    覆盖题目给出的全部三个官方示例（代表三种典型结构：多重子集嵌套、
 *    单一子集、完全独立），并补充边界用例与递推子集链用例。
 * =============================================================
 */
class L2024113155_6_Test {

    private Solution6 solution;

    @BeforeEach
    void setUp() {
        solution = new Solution6();
    }

    /** 辅助方法：由若干字符串数组构造输入 List<List<String>> */
    private List<List<String>> build(String[]... companies) {
        List<List<String>> input = new ArrayList<>();
        for (String[] arr : companies) {
            input.add(Arrays.asList(arr));
        }
        return input;
    }

    /**
     * 测试目的：验证标准功能场景——多个清单存在子集关系时能正确筛除被其他清单包含的子集清单。
     * 测试用例（有效等价类：多重子集嵌套结构）：
     * 输入：[["leetcode","google","facebook"],["google","microsoft"],
     *        ["google","facebook"],["google"],["amazon"]]，
     * 期望输出：[0,1,4]。
     */
    @Test
    void testMultipleSubsetNesting() {
        List<Integer> actual = solution.peopleIndexes(build(
                new String[]{"leetcode", "google", "facebook"},
                new String[]{"google", "microsoft"},
                new String[]{"google", "facebook"},
                new String[]{"google"},
                new String[]{"amazon"}));
        assertEquals(Arrays.asList(0, 1, 4), actual);
    }

    /**
     * 测试目的：验证单一子集关系场景——仅有一个清单是另一个清单的子集。
     * 测试用例（有效等价类：单点子集结构）：
     * 输入：[["leetcode","google","facebook"],["leetcode","amazon"],["facebook","google"]]，
     * 期望输出：[0,1]。
     */
    @Test
    void testSingleSubset() {
        List<Integer> actual = solution.peopleIndexes(build(
                new String[]{"leetcode", "google", "facebook"},
                new String[]{"leetcode", "amazon"},
                new String[]{"facebook", "google"}));
        assertEquals(Arrays.asList(0, 1), actual);
    }

    /**
     * 测试目的：验证所有清单完全独立（互不为任何清单的子集）的场景。
     * 测试用例（有效等价类：无子集关系结构）：
     * 输入：[["leetcode"],["google"],["facebook"],["amazon"]]，
     * 期望输出：[0,1,2,3]。
     */
    @Test
    void testAllIndependent() {
        List<Integer> actual = solution.peopleIndexes(build(
                new String[]{"leetcode"},
                new String[]{"google"},
                new String[]{"facebook"},
                new String[]{"amazon"}));
        assertEquals(Arrays.asList(0, 1, 2, 3), actual);
    }

    /**
     * 测试目的：验证输入规模下边界——仅有一个用户时，其清单必然不是
     * 其他任何清单的子集，应被保留。
     * 测试用例（边界值：n = 1，用户数最小值）：
     * 输入：[["amazon"]]，期望输出：[0]。
     */
    @Test
    void testSingleUserLowerBound() {
        List<Integer> actual = solution.peopleIndexes(build(new String[]{"amazon"}));
        assertEquals(Collections.singletonList(0), actual);
    }

    /**
     * 测试目的：验证清单长度的边界情形——长度为 1 的清单是另一清单的
     * 最小子集单元，应被判定为子集并从结果中筛除。
     * 测试用例（边界值：清单长度为 1 的子集关系）：
     * 输入：[["google"],["google","facebook"]]，期望输出：[1]
     * （[0]=["google"] 是 [1] 的子集，被排除；[1] 无人包含它，保留）。
     */
    @Test
    void testSingletonListAsSubset() {
        List<Integer> actual = solution.peopleIndexes(build(
                new String[]{"google"},
                new String[]{"google", "facebook"}));
        assertEquals(Collections.singletonList(1), actual);
    }

    /**
     * 测试目的：验证子集判定的否定边界——两个清单长度相同，且部分元素重叠，
     * 互不为子集，均应保留。
     * 测试用例（边界值：互不为子集的等长清单）：
     * 输入：[["google","facebook"],["facebook","amazon"]]，期望输出：[0,1]。
     */
    @Test
    void testEqualSizeWithPartialOverlap() {
        List<Integer> actual = solution.peopleIndexes(build(
                new String[]{"google", "facebook"},
                new String[]{"facebook", "amazon"}));
        assertEquals(Arrays.asList(0, 1), actual);
    }

    /**
     * 测试目的：验证递推子集链场景——清单长度依次递增形成子集链，
     * 只有链条最顶端的清单被保留，验证判定与索引顺序无关、结果升序。
     * 测试用例（有效等价类：子集链结构）：
     * 输入：[["a"],["a","b"],["a","b","c"],["a","b","c","d"]]，
     * 期望输出：[3]。
     */
    @Test
    void testSubsetChain() {
        List<Integer> actual = solution.peopleIndexes(build(
                new String[]{"a"},
                new String[]{"a", "b"},
                new String[]{"a", "b", "c"},
                new String[]{"a", "b", "c", "d"}));
        assertEquals(Collections.singletonList(3), actual);
    }

    /**
     * 测试目的：验证非前缀子集场景——子集不要求是连续前缀，
     * 只要元素全部包含即判定为子集。
     * 测试用例（有效等价类：非前缀子集）：
     * 输入：[["x","z"],["x","y","z"],["y","z","w"]]，
     * 期望输出：[1,2]（[0] 是 [1] 的子集，应被筛除；
     * [1] 不被其他任何清单包含，[2]=["y","z","w"] 含 w，不被 [1] 包含，均保留）。
     */
    @Test
    void testNonPrefixSubset() {
        List<Integer> actual = solution.peopleIndexes(build(
                new String[]{"x", "z"},
                new String[]{"x", "y", "z"},
                new String[]{"y", "z", "w"}));
        assertEquals(Arrays.asList(1, 2), actual);
    }
}
