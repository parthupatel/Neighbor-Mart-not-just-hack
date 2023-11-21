package com.apps.neighbormart.network.api_request;

import com.apps.neighbormart.parser.Parser;

import java.util.Map;

public interface ApiRequestListeners {
    void onSuccess(Parser parser);
    void onFail(Map<String, String> errors);
}
