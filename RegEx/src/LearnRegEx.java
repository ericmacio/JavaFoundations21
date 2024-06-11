import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LearnRegEx {
    public static void main(String[] args) {
        String transcript = """
                Student Number: 1234567890      Grade:    11
                Birthdate:     01/02/2000     Gender: M
                State ID:      3543463654
                """;
        String regex = """
                Student\\sNumber:\\s(?<studentNum>\\d{10}).* #Grab student number
                Grade:\\s+(?<studentGrade>\\d{1,2}).* #Grab student grade
                Birthdate:\\s+(?<birthMonth>\\d{2})/(?<birthDay>\\d{2})/(?<birthYear>\\d{4}).* #Grab student birthdate
                """;
        Pattern pat = Pattern.compile(regex, Pattern.DOTALL | Pattern.COMMENTS);
        Matcher mat = pat.matcher(transcript);
        if(mat.matches()) {
            System.out.println(mat.group("studentNum"));
            System.out.println(mat.group("studentGrade"));
            System.out.println(
                    mat.group("birthMonth") + "/" +
                            mat.group("birthDay") + "/" +
                            mat.group("birthYear")
            );
        } else
            System.out.println("Does not match");

    }
}