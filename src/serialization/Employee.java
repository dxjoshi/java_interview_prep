package serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class Employee implements Serializable {

    // This serialVersionUID field is necessary for Serializable as well as Externalizable to provide version control,
    // Compiler will provide this field if we do not provide it which might change if we modify the class structure of our class, and we will get InvalidClassException,
    // If we provide value to this field and do not change it, serialization-deserialization will not fail if we change our class structure.
    private static final long serialVersionUID = 2L;

    private final String firstName; // Serialization process do not invoke the constructor but it can assign values to final fields
    private transient String middleName; // transient variables will not be serialized, serialised object holds null
    private String lastName;
    private int age;
    private static String department; // static variables will not be serialized, serialised object holds null

    public Employee(String firstName, String middleName, String lastName, int age, String department) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.age = age;
        Employee.department = department;

        validateAge();
    }

    private void validateAge() {
        System.out.println("Validating age.");

        if (age < 18 || age > 70) {
            throw new IllegalArgumentException("Not a valid age to create an employee");
        }
    }

    @Override
    public String toString() {
        return String.format("Employee {firstName='%s', middleName='%s', lastName='%s', age='%s', department='%s'}", firstName, middleName, lastName, age, department);
    }

    // Custom serialization logic,
    // This will allow us to have additional serialization logic on top of the default one e.g. encrypting object before serialization
    private void writeObject(ObjectOutputStream oos) throws IOException {
        System.out.println("Custom serialization logic invoked.");
        oos.defaultWriteObject(); // Calling the default serialization logic
    }

    // Custom deserialization logic
    // This will allow us to have additional deserialization logic on top of the default one e.g. decrypting object after deserialization
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        System.out.println("Custom deserialization logic invoked.");

        ois.defaultReadObject(); // Calling the default deserialization logic

        // Age validation is just an example but there might some scenario where we might need to write some custom deserialization logic
        validateAge();
    }

}
