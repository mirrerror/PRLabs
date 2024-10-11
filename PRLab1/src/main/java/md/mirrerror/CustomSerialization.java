package md.mirrerror;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CustomSerialization {

    public static void serializeFields(Object obj, StringBuilder serializedData, Class<?> clazz) {
        try {
            if (clazz.getSuperclass() != null) {
                serializeFields(obj, serializedData, clazz.getSuperclass());
            }

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
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

    public static void deserializeFields(Object obj, Map<String, String> fieldValues, Class<?> clazz) throws Exception {
        if (clazz.getSuperclass() != null) {
            deserializeFields(obj, fieldValues, clazz.getSuperclass());
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
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
