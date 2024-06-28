package com.seulmae.seulmae.global.util;

import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;

public class JsonPagination {
    public static JSONObject buildPageResponse(Page<?> page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalPages", page.getTotalPages());
        jsonObject.put("totalElements", page.getTotalElements());
        jsonObject.put("data", page.getContent());
        return jsonObject;
    }

}
