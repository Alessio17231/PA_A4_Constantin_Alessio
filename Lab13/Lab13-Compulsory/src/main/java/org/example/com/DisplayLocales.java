package com;

import java.util.Locale;
import java.util.ResourceBundle;

public class DisplayLocales {

    public void execute(ResourceBundle messages) {
        System.out.println(messages.getString("locales"));

        Locale[] locales = Locale.getAvailableLocales();

        for (Locale locale : locales) {
            if (!locale.toLanguageTag().equals("und")) {
                System.out.println(locale.toLanguageTag() + " - " + locale.getDisplayName());
            }
        }
    }
}