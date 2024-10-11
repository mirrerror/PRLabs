package md.mirrerror;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CustomSerialization {

    public static byte[] serialize(Object object, Class<?> clazz) {
        StringBuilder serializedData = new StringBuilder();
        serializeFields(object, serializedData, clazz);
        serializedData.append("|");
        return serializedData.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static Object deserialize(Object object, Class<?> clazz, byte[] data) {
        Map<String, String> fieldValues = new HashMap<>();
        String[] fields = new String(data, StandardCharsets.UTF_8).split(";");

        for (String field : fields) {
            if (!field.equals("|") && field.contains("=")) {
                String[] keyValue = field.split("=", 2);
                if (keyValue.length == 2) {
                    fieldValues.put(keyValue[0], keyValue[1]);
                }
            }
        }

        try {

            deserializeFields(object, fieldValues, clazz);
            return object;

        } catch (Exception e) {
            throw new RuntimeException("Error during deserialization: " + e.getMessage(), e);
        }
    }

    private static void serializeFields(Object obj, StringBuilder serializedData, Class<?> clazz) {
        try {
            if (clazz.getSuperclass() != null) {
                serializeFields(obj, serializedData, clazz.getSuperclass());
            }

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(obj);
                if (value instanceof LocalDateTime) {
                    value = ((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                serializedData.append(field.getName()).append("=").append(value).append(";");
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error during serialization: " + e.getMessage(), e);
        }
    }

    private static void deserializeFields(Object obj, Map<String, String> fieldValues, Class<?> clazz) throws Exception {
        if (clazz.getSuperclass() != null) {
            deserializeFields(obj, fieldValues, clazz.getSuperclass());
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldValue = fieldValues.get(fieldName);

            if (fieldValue != null) {
                if (field.getType() == double.class) {
                    field.set(obj, Double.parseDouble(fieldValue));
                } else if (field.getType() == LocalDateTime.class) {
                    field.set(obj, LocalDateTime.parse(fieldValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } else {
                    field.set(obj, fieldValue);
                }
            }
        }
    }

}
