package javabasics;

public class Cat extends Animal {
    public static void testClassMethod() {
        System.out.println("The static method in Cat");
    }
    public void testInstanceMethod() {
        System.out.println("The instance method in Cat");
    }

    public static void main(String[] args) {
        Animal myAnimal = new Animal();
        Animal myAnimalTwo = new Cat();
        Cat myCat = new Cat();

        Animal.testClassMethod();
        Cat.testClassMethod();

        myAnimal.testClassMethod();
        myAnimalTwo.testClassMethod();
        myCat.testClassMethod();

        myAnimal.testInstanceMethod();
        myAnimalTwo.testInstanceMethod();
        myCat.testInstanceMethod();
    }

}