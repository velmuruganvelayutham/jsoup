package com.tocgroup;

/**
 Text utils to ease testing

 @author Jonathan Hedley, jonathan@hedley.net */
public class TextUtil {
    public static String stripNewlines(String text) {
        text = text.replaceAll("\\n\\s*", "");
        return text;
    }
}
