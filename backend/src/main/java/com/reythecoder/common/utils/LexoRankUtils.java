package com.reythecoder.common.utils;

/**
 * LexoRank 排序工具类
 *
 * LexoRank 是一种词典序排序算法，支持动态插入而无需重排其他元素。
 * 使用 36 进制字符集：0123456789abcdefghijklmnopqrstuvwxyz
 *
 * 排序值格式：至少 2 个字符，例如 "a0", "b0", "abc123"
 */
public final class LexoRankUtils {

    /**
     * 36 进制字符集：0-9 + a-z
     */
    private static final String CHARSET = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 36;

    private LexoRankUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * 生成初始排序值
     *
     * @param index 索引值 (0-25)，对应字母 a-z
     * @return 初始排序值，如 index=0 返回 "a0", index=1 返回 "b0", ..., index=25 返回 "z0"
     * @throws IllegalArgumentException 当 index 超出 [0, 25] 范围
     */
    public static String initialRank(int index) {
        if (index < 0 || index >= 26) {
            throw new IllegalArgumentException("Index must be between 0 and 25, got: " + index);
        }
        // index 0 -> 'a', index 1 -> 'b', ..., index 25 -> 'z'
        return CHARSET.charAt(index + 10) + "0";
    }

    /**
     * 计算两个排序值的中间值
     *
     * @param lower 下界排序值（可为 null，表示无下界）
     * @param upper 上界排序值（可为 null，表示无上界）
     * @return 中间排序值
     * @throws IllegalArgumentException 当 lower >= upper 时
     */
    public static String between(String lower, String upper) {
        // 两者都为 null，返回初始值
        if (lower == null && upper == null) {
            return initialRank(0);
        }

        // 只有下界，生成一个更大的值
        if (lower != null && upper == null) {
            return after(lower);
        }

        // 只有上界，生成一个更小的值
        if (lower == null && upper != null) {
            return before(upper);
        }

        // 两者都有，验证合法性
        if (lower.compareTo(upper) >= 0) {
            throw new IllegalArgumentException("lower must be less than upper, got: " + lower + " >= " + upper);
        }

        // 将排序值转换为数值表示
        long lowerVal = toNumeric(lower);
        long upperVal = toNumeric(upper);

        // 计算中间值
        long midVal = (lowerVal + upperVal) / 2;

        // 如果中间值等于下界，需要在上界方向扩展
        if (midVal == lowerVal) {
            // 在 lower 后面追加字符，使其大于 lower 但小于 upper
            return lower + CHARSET.charAt(0);
        }

        return fromNumeric(midVal);
    }

    /**
     * 在当前排序值之前生成新值
     *
     * @param current 当前排序值（可为 null）
     * @return 小于 current 的排序值
     */
    public static String before(String current) {
        if (current == null) {
            return initialRank(0);
        }

        // 策略：在当前值前面插入一个更小的字符
        // 例如："b0" -> "a0", "a0" -> "a00"
        if (current.length() < 2) {
            throw new IllegalArgumentException("Invalid rank format: " + current);
        }

        char firstChar = current.charAt(0);
        int firstIndex = CHARSET.indexOf(firstChar);

        if (firstIndex > 0) {
            // 第一个字符可以减小
            return CHARSET.charAt(firstIndex - 1) + "0";
        } else {
            // 第一个字符已经是最小，在末尾追加最小字符
            return current + CHARSET.charAt(0);
        }
    }

    /**
     * 在当前排序值之后生成新值
     *
     * @param current 当前排序值（可为 null）
     * @return 大于 current 的排序值
     */
    public static String after(String current) {
        if (current == null) {
            return initialRank(0);
        }

        if (current.length() < 2) {
            throw new IllegalArgumentException("Invalid rank format: " + current);
        }

        // 策略：在当前值末尾追加一个字符
        // 例如："a0" -> "a00", "b0" -> "b00"
        return current + CHARSET.charAt(0);
    }

    /**
     * 验证排序值是否合法
     *
     * @param rank 排序值
     * @return 是否合法
     */
    public static boolean isValidRank(String rank) {
        if (rank == null || rank.length() < 2) {
            return false;
        }

        for (char c : rank.toCharArray()) {
            if (CHARSET.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将排序值转换为数值表示（用于计算中间值）
     * 注意：这只适用于较短的排序值，长排序值会溢出
     */
    private static long toNumeric(String rank) {
        long result = 0;
        for (char c : rank.toCharArray()) {
            result = result * BASE + CHARSET.indexOf(c);
        }
        return result;
    }

    /**
     * 从数值生成排序值
     */
    private static String fromNumeric(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative: " + value);
        }

        StringBuilder sb = new StringBuilder();
        long v = value;

        do {
            int remainder = (int) (v % BASE);
            sb.insert(0, CHARSET.charAt(remainder));
            v = v / BASE;
        } while (v > 0);

        return sb.toString();
    }
}
