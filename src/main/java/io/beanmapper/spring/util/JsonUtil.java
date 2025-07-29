package io.beanmapper.spring.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilities for working with JSON.
 *
 * @author Jeroen van Schagen
 * @since Nov 13, 2015
 */
public class JsonUtil {

    /**
     * Retrieve the property names mentioned in a JSON content.
     *
     * @param json the JSON content
     * @param objectMapper the object mapper
     * @return set of the property names
     */
    public static Set<String> getPropertyNamesFromJson(String json, ObjectMapper objectMapper) {
        try {
            JsonNode tree = objectMapper.readTree(json);
            return getPropertyNames(tree, "");
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not retrieve property names from JSON.", e);
        }
    }

    private static Set<String> getPropertyNames(JsonNode node, String base) {
        Set<String> propertyNames = new HashSet<>();
        Iterator<String> iterator = node.fieldNames();
        while (iterator.hasNext()) {
            String fieldName = iterator.next();

            String propertyName = isEmpty(base) ? fieldName : base + "." + fieldName;
            propertyNames.add(propertyName);
            propertyNames.addAll(getPropertyNames(node.get(fieldName), propertyName));
        }
        return propertyNames;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}