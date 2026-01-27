package dashboard;

import java.util.Scanner;

public class Dashboard {

    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- ড্যাশবোর্ড ---");
            System.out.println("1. দিক নির্দেশনা (জন্মের আগে)");
            System.out.println("2. চ্যাটবক্স");
            System.out.println("3. সাধারণ রোগ ও সমাধান");
            System.out.println("4. লগ আউট");
            System.out.print("পছন্দ করুন: ");

            // Input check to avoid Infinite Loop on wrong input
            if (!sc.hasNextInt()) {
                System.out.println("দয়া করে একটি সংখ্যা দিন।");
                sc.next(); 
                choice = 0;
                continue;
            }

            choice = sc.nextInt();

            switch (choice) {
                case 1 -> System.out.println("গাইড: সুষম খাদ্য ও নিয়মিত হালকা ব্যায়াম করুন।");
                case 2 -> System.out.println("AI চ্যাটবক্স: আপনার প্রশ্নটি লিখুন (Demo)।");
                case 3 -> System.out.println("টিপস: জ্বর বা মাথাব্যথা হলে বিশ্রাম নিন এবং ডাক্তার দেখান।");
                case 4 -> System.out.println("লগ আউট করা হচ্ছে...");
                default -> System.out.println("ভুল অপশন, আবার চেষ্টা করুন।");
            }
        } while (choice != 4);
        
        // sc.close(); // Don't close if System.in is needed elsewhere
    }
}