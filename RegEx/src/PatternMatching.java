import static java.lang.StringTemplate.STR;

public class PatternMatching {
    public static void main(String[] args) {
        record Person(String firstName, String lastName, int age) {
        }
        String var1 = "Hello World";
        Integer var2 = 34;
        String[] var3 = {"hello", "world"};
        Person var4 = new Person("Jake", "Johnson", 35);
        Person var5 = new Person("Abe", "Johnson", 32);
        Object obj = null;
        switch(obj) {
            case null -> System.out.println("It's null");
            case String msg -> System.out.println(msg);
            case Integer num -> System.out.println(STR."your number is \{num}");
            case Person p when p.firstName().length() > 3 -> System.out.println("Looks like Jake");
            case String[] arr -> System.out.println(STR."Looks like an array with \{arr.length} elements");
            default -> System.out.println("No idea");
        }
    }
}
