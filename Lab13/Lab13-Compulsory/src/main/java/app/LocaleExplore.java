package app;

import com.DisplayLocales;
import com.Info;
import com.SetLocale;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LocaleExplore {

    private static Locale currentLocale = Locale.ENGLISH;
    private static ResourceBundle messages = ResourceBundle.getBundle("res.Messages", currentLocale);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DisplayLocales displayLocales = new DisplayLocales();
        SetLocale setLocale = new SetLocale();
        Info info = new Info();

        while (true) {
            printAvailableCommands();

            System.out.print(messages.getString("prompt") + " ");
            String command = scanner.nextLine().trim();

            if (command.equalsIgnoreCase("exit")) {
                break;
            }

            if (command.equalsIgnoreCase("locales")) {
                displayLocales.execute(messages);
            } else if (command.startsWith("set ")) {
                String languageTag = command.substring(4).trim();

                currentLocale = setLocale.execute(languageTag, messages);
                messages = ResourceBundle.getBundle("res.Messages", currentLocale);
            } else if (command.equalsIgnoreCase("info")) {
                info.execute(currentLocale, messages);
            } else if (command.startsWith("info ")) {
                String languageTag = command.substring(5).trim();
                Locale locale = Locale.forLanguageTag(languageTag);

                info.execute(locale, messages);
            } else {
                System.out.println(messages.getString("invalid"));
            }

            System.out.println();
        }
    }

    private static void printAvailableCommands() {
        System.out.println("Comenzi disponibile:");
        System.out.println("locales");
        System.out.println("set ro-RO");
        System.out.println("set en-US");
        System.out.println("info");
        System.out.println("info ro-RO");
        System.out.println("exit");
        System.out.println();
    }
}