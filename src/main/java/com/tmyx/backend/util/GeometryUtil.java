package com.tmyx.backend.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class GeometryUtil {

    // 将scope_path转换为WKT格式
    public static String parseScopePathToWkt(String scopePath) {
        if (scopePath == null || scopePath.isEmpty()) {
            return null;
        }

        try {
            // 使用 JSONArray 通用解析，避免强转 Double
            JSONArray outerArray = JSON.parseArray(scopePath);
            if (outerArray == null || outerArray.isEmpty()) return null;

            StringBuilder wkt = new StringBuilder("POLYGON((");

            for (int i = 0; i < outerArray.size(); i++) {
                JSONArray point = outerArray.getJSONArray(i);
                // 使用 doubleValue() 兼容 BigDecimal 或其他数值类型
                double lng = point.getDoubleValue(0);
                double lat = point.getDoubleValue(1);

                wkt.append(lng).append(" ").append(lat);

                if (i < outerArray.size() - 1) {
                    wkt.append(", ");
                }
            }

            // 闭合处理：取出第一个和最后一个点
            JSONArray first = outerArray.getJSONArray(0);
            JSONArray last = outerArray.getJSONArray(outerArray.size() - 1);

            if (first.getDoubleValue(0) != last.getDoubleValue(0) ||
                    first.getDoubleValue(1) != last.getDoubleValue(1)) {
                wkt.append(", ").append(first.getDoubleValue(0))
                        .append(" ").append(first.getDoubleValue(1));
            }

            wkt.append("))");
            return wkt.toString();
        } catch (Exception e) {
            // 这里建议打印更详细的堆栈，方便排查
            e.printStackTrace();
            return null;
        }
    }
}
