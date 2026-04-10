import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * A program that calculates student housing priority using a point system.
 *
 * Point scoring:
 * - Academic probation: -2 points
 * - Freshman: +2 points
 * - Campus faculty / TA / campus employee: +1 point
 * - Has children: +3 points
 * - Academic or merit scholarship: +1 point
 *
 * Higher scores should receive earlier housing selection.
 * If students tie, signup order is used as the tiebreaker.
 */
public class StudentHousingSorter {

    private static final int ACADEMIC_PROBATION_POINTS = -2; // Academic suspension or probation
    private static final int FRESHMAN_POINTS = 2; // Freshman status for the housing semester
    private static final int CAMPUS_EMPLOYEE_POINTS = 1; // Works on campus as a TA, faculty member, or employee
    private static final int CHILDREN_POINTS = 3; // Has children (student parent status)
    private static final int SCHOLARSHIP_POINTS = 1; // Currently receives an academic or merit scholarship

    /**
     * Stores one student's name, score, and signup order.
     */
    private static class StudentRecord {
        private final String name;
        private final int score;
        private final int signupOrder;

        private StudentRecord(String name, int score, int signupOrder) {
            this.name = name;
            this.score = score;
            this.signupOrder = signupOrder;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<StudentRecord> students = new ArrayList<>();
        int signupOrder = 1;

        System.out.println("University Housing Priority Calculator");
        System.out.println("Made by: Ryan Hoo");
        System.out.println("Answer each question with yes/no.");
        System.out.println("Higher scores get earlier housing selection.");
        System.out.println("If two students tie, the earlier signup stays ahead.");
        System.out.println("Type exit at any prompt to quit the program.");

        while (true) {
            System.out.println();
            System.out.println("Entering student #" + signupOrder);

            String studentName = askForName(scanner);
            int totalScore = calculateStudentScore(scanner, studentName);

            students.add(new StudentRecord(studentName, totalScore, signupOrder));

            System.out.println();
            System.out.println(studentName + "'s final housing score: " + totalScore);
            displayRankings(students);

            signupOrder++;

            if (!askQuestion(scanner, "Would you like to enter another student?")) {
                break;
            }
        }

        System.out.println();
        System.out.println("Final housing priority order:");
        displayRankings(students);

        scanner.close();
    }

    /**
     * Calculates one student's score by asking the algorithm questions.
     *
     * @param scanner the Scanner used for reading input
     * @param studentName the current student's name
     * @return the student's total score
     */
    private static int calculateStudentScore(Scanner scanner, String studentName) {
        int totalScore = 0;

        System.out.println();
        System.out.println("Now scoring " + studentName + ":");

        //Academic probation/suspension question
        if (askQuestion(scanner, "Are you currently on academic probation or suspension for the semester?")) {
            totalScore += ACADEMIC_PROBATION_POINTS;
            System.out.println("Academic probation/suspension: " + ACADEMIC_PROBATION_POINTS + " points");
        } 
        else {
            System.out.println("Academic probation/suspension: 0 points");
        }

        //Freshman question
        if (askQuestion(scanner, "Are you or will you be a freshman for the housing semester?")) {
            totalScore += FRESHMAN_POINTS;
            System.out.println("Freshman priority: +" + FRESHMAN_POINTS + " points");
        } 
        else {
            System.out.println("Freshman priority: 0 points");
        }

        //Campus employee question
        if (askQuestion(scanner, "Do you work on campus as a TA, faculty member, or campus employee?")) {
            totalScore += CAMPUS_EMPLOYEE_POINTS;
            System.out.println("Campus work priority: +" + CAMPUS_EMPLOYEE_POINTS + " points");
        } 
        else {
            System.out.println("Campus work priority: 0 points");
        }

        //Children question
        if (askQuestion(scanner, "Do you have any children?")) {
            totalScore += CHILDREN_POINTS;
            System.out.println("Student parent priority: +" + CHILDREN_POINTS + " points");
        } 
        else {
            System.out.println("Student parent priority: 0 points");
        }

        //Scholarship question  
        if (askQuestion(scanner, "Do you currently receive an academic or merit scholarship?")) {
            totalScore += SCHOLARSHIP_POINTS;
            System.out.println("Scholarship priority: +" + SCHOLARSHIP_POINTS + " points");
        } 
        else {
            System.out.println("Scholarship priority: 0 points");
        }

        return totalScore;
    }

    /**
     * Repeatedly asks for a student's name until a non-empty value is entered.
     *
     * @param scanner the Scanner used for reading input
     * @return the student's name
     */
    private static String askForName(Scanner scanner) {
        while (true) {
            System.out.print("Enter the student's name: ");
            String input = scanner.nextLine().trim();

            if (shouldExit(input)) {
                exitProgram(scanner);
            }

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Invalid name. Please try again.");
        }
    }

    /**
     * Displays the current ranking list.
     *
     * @param students the list of entered students
     */
    private static void displayRankings(List<StudentRecord> students) {
        List<StudentRecord> rankedStudents = new ArrayList<>(students);

        for (int i = 0; i < rankedStudents.size(); i++) {
            for (int j = i + 1; j < rankedStudents.size(); j++) {
                StudentRecord firstStudent = rankedStudents.get(i);
                StudentRecord secondStudent = rankedStudents.get(j);

                boolean shouldSwap = false;

                if (secondStudent.score > firstStudent.score) {
                    shouldSwap = true;
                } 
                else if (secondStudent.score == firstStudent.score && secondStudent.signupOrder < firstStudent.signupOrder) {
                    shouldSwap = true;
                }

                if (shouldSwap) {
                    rankedStudents.set(i, secondStudent);
                    rankedStudents.set(j, firstStudent);
                }
            }
        }

        System.out.println("Current housing rankings:");

        for (int i = 0; i < rankedStudents.size(); i++) {
            StudentRecord student = rankedStudents.get(i);
            System.out.println((i + 1) + ". " + student.name + " - " + student.score + " points (Signup Order: " + student.signupOrder + ")");
        }
    }

    /**
     * Repeatedly asks a yes/no question until the user enters valid input.
     * This accepts yes/no in any letter case, including caps lock input.
     *
     * @param scanner the Scanner used for reading input
     * @param question the question to ask the user
     * @return true for yes, false for no
     */
    private static boolean askQuestion(Scanner scanner, String question) {
        while (true) {
            System.out.print(question + " ");
            String input = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            if (shouldExit(input)) {
                exitProgram(scanner);
            }

            if (input.equals("yes") || input.equals("y")) {
                return true;
            }

            if (input.equals("no") || input.equals("n")) {
                return false;
            }

            System.out.println("Invalid input. Please enter yes or no.");
        }
    }

    /**
     * Checks whether the user wants to quit the program.
     *
     * @param input the user's input
     * @return true if the input means the program should exit
     */
    private static boolean shouldExit(String input) {
        return input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit");
    }

    /**
     * Ends the program.
     *
     * @param scanner the Scanner used for reading input
     */
    private static void exitProgram(Scanner scanner) {
        System.out.println("Exiting program.");
        scanner.close();
        System.exit(0);
    }
}
