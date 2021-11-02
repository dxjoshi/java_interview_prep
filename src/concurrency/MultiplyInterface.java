package concurrency;

public interface MultiplyInterface {
    int multiplyByTwo();

    // Default methods provide additional functionality to a given type without breaking down the implementing classes.(backward compatibility)
    default String asString(Integer val) {
        return String.valueOf(val);
    }

    //Static interface method is identical to defining one in a class. It can be invoked within other static and default methods.
    static void consoleOutput (Integer val) {
        System.out.println(val);
    }
}
