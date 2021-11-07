package javabasics;

public class HelloWorldAnonymousClasses {
    interface HelloWorld {
        void greet();
        void greetSomeone(String someone);
    }

    public void sayHello() {
        // local class
        class EnglishGreeting implements HelloWorld {
            String name = "world";
            public void greet() { greetSomeone("world"); }
            public void greetSomeone(String someone) { name = someone;System.out.println("Hello " + name); }
        }
        HelloWorld englishGreeting = new EnglishGreeting();
        //anonymous class
        HelloWorld frenchGreeting = new HelloWorld() {
            String name = "tout le monde";
            public void greet() { greetSomeone("tout le monde"); }
            public void greetSomeone(String someone) { name = someone;System.out.println("Salut " + name); }
        };

        //anonymous class
        HelloWorld spanishGreeting = new HelloWorld() {
            String name = "mundo";
            public void greet() { greetSomeone("mundo"); }
            public void greetSomeone(String someone) { name = someone;System.out.println("Hola, " + name); }
        };

        englishGreeting.greet();
        frenchGreeting.greetSomeone("Fred");
        spanishGreeting.greet();
    }

    public static void main(String... args) {
        HelloWorldAnonymousClasses myApp = new HelloWorldAnonymousClasses();
        myApp.sayHello();
    }
}
