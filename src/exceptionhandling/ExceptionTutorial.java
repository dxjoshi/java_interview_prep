package exceptionhandling;

import java.io.IOException;

public class ExceptionTutorial {
    public static void main(String[] args) {
        //public class Throwable implements Serializable {
        //public class Error extends Throwable {
        //public class Exception extends Throwable {
        //public class RuntimeException extends Exception {


        try {
            throw new IOException();
        } catch (IndexOutOfBoundsException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }
}
