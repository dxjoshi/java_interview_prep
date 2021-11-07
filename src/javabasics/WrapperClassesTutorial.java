package javabasics;

public class WrapperClassesTutorial {
    public static void main(String[] args) {
        //public abstract class Number implements java.io.Serializable {
        //public final class Integer extends Number implements Comparable<java.lang.Integer> {
        //public final class Character implements java.io.Serializable, Comparable<Character> {

        //boolean	Boolean
        //byte	    Byte
        //char	    Character
        //float	    Float
        //int	    Integer
        //long	    Long
        //short	    Short
        //double	Double

        Boolean booleanWrapper = new Boolean(true);
        Byte byteWrapper = new Byte((byte) 1);
        Character characterWrapper = new Character('a');
        Float floatWrapper = new Float(1.0F);
        Integer integerWrapper = new Integer(1);
        Long longWrapper = new Long(1L);
        Short shortWrapper = new Short((short) 1);
        Double doubleWrapper = new Double(1.0D);

        /* Unboxing*/
        boolean unboxedBoolean = booleanWrapper; //compiler calls booleanWrapper.booleanValue();
        byte unboxedByte = byteWrapper;         //compiler calls byteWrapper.byteValue();
        char unboxedCharacter = characterWrapper;      //compiler calls characterWrapper.charValue();
        float unboxedFloat = floatWrapper;        //compiler calls floatWrapper.floatValue();
        int unboxedInteger = integerWrapper; //compiler calls integerWrapper.intValue();
        long unboxedLong = longWrapper; //compiler calls longWrapper.longValue();
        short unboxedShort = shortWrapper;         //compiler calls shortWrapper.shortValue();
        double unboxedDouble = doubleWrapper;     //compiler calls doubleWrapper.doubleValue();

        /*Autoboxing*/
        Boolean autoboxedBoolean = unboxedBoolean;    //Boolean.valueOf(unboxedBoolean);
        Byte autoboxedByte = unboxedByte; //Byte.valueOf(unboxedByte);
        Character autoboxedCharacter = unboxedCharacter;   //Character.valueOf(unboxedCharacter);
        Float autoboxedFloat = unboxedFloat;    //Float.valueOf(unboxedFloat);
        Integer autoboxedInteger = unboxedInteger;   //Integer.valueOf(unboxedInteger);
        Long autoboxedLong = unboxedLong;    //Long.valueOf(unboxedLong);
        Short autoboxedShort = unboxedShort;   //Short.valueOf(unboxedShort);
        Double autoboxedDouble = unboxedDouble;  //Double.valueOf(unboxedDouble);

    }
}
