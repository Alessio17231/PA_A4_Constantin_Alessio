package com;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class Info {

    public void execute(Locale locale, ResourceBundle messages) {
        System.out.println(
                MessageFormat.format(
                        messages.getString("info"),
                        locale.toLanguageTag()
                )
        );

        System.out.println("Country: "
                + locale.getDisplayCountry(Locale.ENGLISH)
                + " (" + locale.getDisplayCountry(locale) + ")");

        System.out.println("Language: "
                + locale.getDisplayLanguage(Locale.ENGLISH)
                + " (" + locale.getDisplayLanguage(locale) + ")");

        printCurrency(locale);
        printWeekDays(locale);
        printMonths(locale);
        printToday(locale);
    }

    private void printCurrency(Locale locale) {
        try {
            Currency currency = Currency.getInstance(locale);

            System.out.println("Currency: "
                    + currency.getCurrencyCode()
                    + " (" + currency.getDisplayName(locale) + ")");
        } catch (Exception exception) {
            System.out.println("Currency: -");
        }
    }

    private void printWeekDays(Locale locale) {
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        String[] weekDays = symbols.getWeekdays();

        System.out.print("Week Days: ");

        for (int i = 2; i < weekDays.length; i++) {
            System.out.print(weekDays[i]);

            if (i < weekDays.length - 1) {
                System.out.print(", ");
            }
        }

        System.out.println(", " + weekDays[1]);
    }

    private void printMonths(Locale locale) {
        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        String[] months = symbols.getMonths();

        System.out.print("Months: ");

        boolean first = true;

        for (String month : months) {
            if (month == null || month.isEmpty()) {
                continue;
            }

            if (!first) {
                System.out.print(", ");
            }

            System.out.print(month);
            first = false;
        }

        System.out.println();
    }

    private void printToday(Locale locale) {
        Date today = new Date();

        DateFormat englishFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
        DateFormat localFormat = DateFormat.getDateInstance(DateFormat.LONG, locale);

        System.out.println("Today: "
                + englishFormat.format(today)
                + " (" + localFormat.format(today) + ")");
    }
}