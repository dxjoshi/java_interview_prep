package java8;

import common.Student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public class FunctionalInterfaces {
    public static void main(String[] args) {
        methodAndConstructorRefs();
        localVariableAccess();
        builtInInterfaces();
    }

    private static void builtInInterfaces() {
        //public interface Function<T, R> { R apply(T t); }
        //Represents a function that accepts one argument and produces a result.
        Function<Object, String> convertToString = (val) -> String.valueOf(val);

        //public interface Consumer<T> { void accept(T t); }
        //Represents an operation that accepts a single input argument and returns no result.
        Consumer<Object> printToConsole = (val) -> System.out.println(val);

        //public interface Supplier<T> { T get(); }
        //Represents a supplier of results. Doesn't need distinct result be returned each time.
        Supplier<Integer> supplyAnInteger = () -> 1;

        //public interface Predicate<T> { boolean test(T t); }
        //Represents a predicate (boolean-valued function) of one argument.
        Predicate<Integer> isEvenInteger = (integer) -> (integer % 2)==0;

        // public interface UnaryOperator<T> extends Function<T, T> { static <T> UnaryOperator<T> identity() { return t -> t; }}
        //Represents an operation on a single operand that produces a result of the same type as its operand.
        UnaryOperator<Integer> multiplyByTwo = (integer) -> integer*2;

        //public interface BiFunction<T, U, R> { R apply(T t, U u); }
        //Represents a function that accepts two arguments and produces a result.
        BiFunction<Integer, Integer, Integer> sumOfIntegers = (integerOne, integerTwo) -> integerOne + integerTwo;

        //public interface BiConsumer<T, U> { void accept(T t, U u);}
        //Represents an operation that accepts two input arguments and returns no result.
        BiConsumer<Integer, Integer> printSumToConsole = (integerOne, integerTwo) -> System.out.println(sumOfIntegers.apply(integerOne, integerTwo));

        //public interface BiPredicate<T, U> { boolean test(T t, U u); }
        //Represents a predicate (boolean-valued function) of two arguments.
        BiPredicate<Integer, Integer> areBothEven = (integerOne, integerTwo) -> isEvenInteger.test(integerOne) && isEvenInteger.test(integerTwo);

        //public interface IntBinaryOperator { int applyAsInt(int left, int right);}
        //Represents an operation upon two int valued operands and producing an int result.
        IntBinaryOperator mutliply = (integerOne, integerTwo) -> integerOne * integerTwo;

        //public interface IntConsumer { void accept(int value); }
        //Represents an operation that accepts a single int and returns no result
        IntConsumer printIntToConsole = (integerOne) -> printToConsole.accept(integerOne);

        //public interface IntFunction<R> { R apply(int value);}
        //Represents a function that accepts an int-valued argument and produces a result;
        IntFunction<Double> toDouble = (val) -> Double.valueOf(String.valueOf(val));


        //public interface IntPredicate { boolean test(int value); }
        //Represents a predicate (boolean-valued function) of one int valued  argument.
        IntPredicate isOddNumber = (val) -> val%2 != 1;

        //public interface IntSupplier { int getAsInt(); }
        //Represents a supplier of int valued results.
        IntSupplier returnZero = () -> 0;

        //public interface IntToDoubleFunction { double applyAsDouble(int value);}
        //Represents a function that accepts an int-valued argument and produces a double-valued result.
        IntToDoubleFunction intToDoubleFunction = value -> toDouble.apply(value);

        //public interface IntToLongFunction { long applyAsLong(int value);}
        //Represents a function that accepts an int-valued argument and produces a long-valued result.
        IntToLongFunction intToLongFunction = value -> Long.valueOf(convertToString.apply(value));

        //public interface IntUnaryOperator { int applyAsInt(int operand); }
        //Represents an operation on a single int valued operand that produces an int valued result.
        IntUnaryOperator addTen = value -> value + 10;
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
