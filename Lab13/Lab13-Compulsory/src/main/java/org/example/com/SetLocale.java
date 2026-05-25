package com;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class SetLocale {

    public Locale execute(String languageTag, ResourceBundle messages) {
        Locale locale = Locale.forLanguageTag(languageTag);

        System.out.println(
                MessageFormat.format(
                        messages.getString("locale.set"),
                        locale.toLanguageTag()
                )
        );

        return locale;
    }
}