package annotation;

import common.Student;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProcessCustomAnnotation {
    public String toString(Object object) throws Exception {
        Class<?> clazz = object.getClass();
        Map<String, Object> map = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(FieldName.class)) {
                FieldName annotation =  (FieldName) field.getAnnotation(FieldName.class);
                map.put(annotation.key(), field.get(object));
            } else {
                map.put(field.getName(), field.get(object));
            }
        }

        String jsonString = map.entrySet()
                .stream()
                .map(entry -> "\"" + entry.getKey() + "\":\""
                        + entry.getValue() + "\"")
                .collect(Collectors.joining(","));
        return "{" + jsonString + "}";
    }

    public static void main(String[] args) throws Exception {
        testCustomAnnotation();
    }

    private static void testCustomAnnotation() throws Exception {
        Student student = new Student("John Doe", 40);

        ProcessCustomAnnotation obj = new ProcessCustomAnnotation();
        String objAsString = obj.toString(student);
        System.out.println(objAsString);
    }
}

