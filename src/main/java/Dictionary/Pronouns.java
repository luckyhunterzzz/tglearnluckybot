package Dictionary;

import java.util.HashMap;
import java.util.Map;

public class Pronouns extends PartOfWord {
    public Map<String, String> dictionary() {
        Map<String, String> pronounsDictionary = new HashMap<>();
        pronounsDictionary.put("io", "I");
        pronounsDictionary.put("io sono", "I am");
        pronounsDictionary.put("io ho", "I have");
        pronounsDictionary.put("tu", "You");
        pronounsDictionary.put("tu sei", "You are");
        pronounsDictionary.put("tu hai", "You have");
        pronounsDictionary.put("con te", "with you");
        pronounsDictionary.put("lui", "he");
        pronounsDictionary.put("lei", "she");
        pronounsDictionary.put("lui e", "he is");
        pronounsDictionary.put("lei e", "she is");
        pronounsDictionary.put("lui ha", "he has");
        pronounsDictionary.put("lei ha", "she has");
        pronounsDictionary.put("noi", "we");
        pronounsDictionary.put("noi sono", "we are");
        pronounsDictionary.put("noi abbiamo", "we have");
        pronounsDictionary.put("suo", "his");
        pronounsDictionary.put("tuo", "your");
        pronounsDictionary.put("voi", "you");
        pronounsDictionary.put("voi avete", "You have");
        pronounsDictionary.put("voi siete", "you are");
        return pronounsDictionary;
    }
}
