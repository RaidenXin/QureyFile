package com.huihuang.queryfile.utils;

import java.util.List;
import java.util.Objects;

/**
 * @author: JiangJi
 * @Descriotion:
 * @Date:Created in 2023/2/18 11:24
 */
public final class ListUtil {

    public static boolean isEmpty(List list) {
        return Objects.isNull(list) || list.isEmpty();
    }

    public static boolean isNotEmpty(List list) {
        return !isEmpty(list);
    }
}
