package javabasics;

public class StringTutorial {
    public static void main(String[] args) {
        Integer integer = 1;
        String asString = String.valueOf(integer);   //Also Integer.toString(integer)
        int toInteger = Integer.parseInt(asString); //Also Integer.valueOf(integerAsString);

        //public final class StringBuilder extends AbstractStringBuilder implements java.io.Serializable, CharSequence
        StringBuilder builder = new StringBuilder("Helllo World!!");
        builder.insert(6, " Sweet");
        System.out.println(builder.toString());

        // public final class StringBuffer extends AbstractStringBuilder implements java.io.Serializable, CharSequence
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Hello");
    }
}
