package common;

import annotation.FieldName;

public class Student {

    @FieldName(key = "Full Name")
    private String name;
/*    @FieldName(key = "Age")*/
    private int age;
    private static int val=0;
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Student(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}