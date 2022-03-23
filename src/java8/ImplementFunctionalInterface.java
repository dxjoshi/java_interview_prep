package java8;

import java.util.function.Consumer;

public class ImplementFunctionalInterface implements Consumer {

    @Override
    public void accept(Object o) {
        System.out.println(o.toString());
    }

    @Override
    public Consumer andThen(Consumer after) {
        return null;
    }
}
