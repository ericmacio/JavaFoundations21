import business.Company;

public class Person {
    private final double PI = 3.14;
    private final String MY_BIG_CONSTANT = "Hi this is my big constant";
    private String firstname = "Jerry";
    private int age;
    private long id;
    private char middleInitial = 'J';
    private byte myByte;
    static private Company company = new Company();
    private NewsAgency agency;
    public static String[] words;

    static {
        words = new String[2];
        words[0] = "Hello";
        words[1] = "Bob";
    }
    public void test(String... strArray) {
        for(int i=0; i<strArray.length; i++)
            System.out.print(strArray[i]);
        System.out.println();
    }

    public static void displayWords() {
        for(int i=0; i<words.length; i++)
            System.out.println(words[i]);
    }
    public static void main(String[] args) {
        System.out.println(company);
        Person p1 = new Person();
        p1.test("hello", " ", "tom", " ", "How are you doing ?");
        Person.displayWords();
    }
}