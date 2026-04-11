import dashboard.Dashboard;
import frontend.Login;
import frontend.Register;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Register reg = new Register();
        Login log = new Login();
        Dashboard dash = new Dashboard();

        while (true) {
            System.out.println("\n--- মাতৃ দিশা (Console Test) ---");
            System.out.println("1. রেজিস্ট্রেশন");
            System.out.println("2. লগইন");
            System.out.println("3. বের হও");
            System.out.print("পছন্দ করুন: ");

            // Handling non-integer inputs
            if (!sc.hasNextInt()) {
                System.out.println("দয়া করে সংখ্যা দিন।");
                sc.next();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Name: "); String name = sc.nextLine();
                    System.out.print("Age: "); int age = sc.nextInt();
                    System.out.print("Weight: "); double weight = sc.nextDouble();
                    System.out.print("Weeks: "); int weeks = sc.nextInt();
                    sc.nextLine(); // Clear buffer
                    System.out.print("Username: "); String user = sc.nextLine();
                    System.out.print("Password: "); String pass = sc.nextLine();
                    
                    // Parameters match our new Register.java method
                    boolean isReg = reg.registerUser(name, age, weight, weeks, "no", "normal", user, pass);
                    if(isReg) System.out.println("Registration Successful!");
                    else System.out.println("Registration Failed!");
                }
                case 2 -> {
                    System.out.print("Username: "); String user = sc.nextLine();
                    System.out.print("Password: "); String pass = sc.nextLine();
                    
                    // Login.java now returns String array [name, weeks]
                    String[] userData = log.loginUser(user, pass);
                    if (userData != null) {
                        System.out.println("স্বাগতম " + userData[0] + "!");
                        dash.showMenu();
                    } else {
                        System.out.println("ভুল ইউজারনেম অথবা পাসওয়ার্ড!");
                    }
                }
                case 3 -> {
                    System.out.println("ধন্যবাদ");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("ভুল অপশন, আবার চেষ্টা করুন।");
            }
        }
    }
}