package concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Student;

public class FunctionalInterfaces {
    public static void main(String[] args) {
        methodAndConstructorRefs();
        localVariableAccess();
        builtInInterfaces();
        customAnnotation();
    }

    private static void customAnnotation() {

    }

    private static void builtInInterfaces() {

    }

    private static void localVariableAccess() {
        int val = 1;
        Function<Integer, String> toString = num -> String.valueOf(num+ val);
        // val = 2; uncommenting this will throw compilation err as local variable used in lambda expression should be final or effectively final
        toString.apply(val);
    }

    private static void methodAndConstructorRefs() {
        List<Student> students = new ArrayList<>();
        List<Integer> ages = Arrays.asList(1,2,3,4,5)
                .stream()
                .map(Student::new)      // Constructor reference
                .map(Student::getAge)   // Method reference
                .collect(Collectors.toList());
    }

}
