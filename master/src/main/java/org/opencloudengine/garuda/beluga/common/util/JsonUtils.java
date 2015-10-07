/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencloudengine.garuda.beluga.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Jackson JSON Utility.
 *
 * @author Byoung Gon, Kim
 * @author Sang Wook, Song
 * @since 2.0
 */
public class JsonUtils {

    /**
     * Jackson JSON Object Mapper
     */
    private static ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper() {
			{
				configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
		};
		objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
	}

    /**
     * 지정한 Object를 Jackson JSON Object Mapper를 이용하여 JSON으로 변환한다.
     *
     * @param obj JSON으로 변환할 Object
     * @return JSON String
     * @throws java.io.IOException JSON으로 변환할 수 없는 경우
     */
    public static String marshal(Object obj) {
	    try {
		    return objectMapper.writeValueAsString(obj);
	    } catch (JsonProcessingException e) {
		    throw new RuntimeException(e);
	    }
    }

    public static Map unmarshal(String json) {
	    try {
		    return objectMapper.readValue(json, Map.class);
	    } catch (IOException e) {
		    throw new RuntimeException(e);
	    }
    }

    public static <T> T unmarshal(String jsonData, TypeReference valueTypeRef) {

	    try {
		    return objectMapper.readValue(jsonData, valueTypeRef);
	    } catch (IOException e) {
		    throw new RuntimeException(e);
	    }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 지정한 Object를 Jackson JSON Object Mapper를 이용하여 JSON으로 변환한다.
     *
     * @param obj JSON으로 변환할 Object
     * @return JSON String
     * @throws java.io.IOException JSON으로 변환할 수 없는 경우
     */
    public static String format(Object obj) {
	    String json = null;
	    try {
		    json = objectMapper.writeValueAsString(obj);
	    } catch (JsonProcessingException e) {
		    throw new RuntimeException(e);
	    }
	    return JsonFormatterUtils.prettyPrint(json);
    }

}
