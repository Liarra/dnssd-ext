package tue.dnssd.context.tag.naming;

import java.util.ArrayList;
import java.util.List;

/**
 * Just a helper class containing tags combination and concatenating functions for {@link tue.dnssd.context.tag.naming.SortedTagsConcatNamingScheme}
 *  and {@link tue.dnssd.context.tag.naming.SortedTagsNestedNamingScheme}.
 *
 * Created by nina on 5/21/14.
 */
public class TagsCombiner {
    /**
     * Glues strings together with given character in-between them. The input of {tag1,tag2,tag3} and character "@" will
     * math to the output "tag1@tag2@tag3@"
     * @param items
     * @param character
     * @return
     */
    public static String concatenateListWithCharacter(List<String> items, String character){
        String tagsString = "";
        for (String s : items) {
            tagsString += s + character;
        }

//        tagsString=tagsString.substring(0,tagsString.length()-1);

        return tagsString;
    }

    /**
     * Produces all possible combinations of input strings, glued together with some character.
     * Note that we produce only one item of output list for a subset set of tags:
     * for input strings {"a","b","c"} there will be either "ab" or "ba" in the output, but not both.
     * @param tags
     * @param ret
     */
    public static void combination(List<String> tags, List<String> ret) {
        //Make a combination of all
        String tagsString= concatenateListWithCharacter(tags, ".");

        if(tagsString.length()>0){
            if(!ret.contains(tagsString))
                ret.add(tagsString);
        }

        //Try all combinations less on 1 tag
        for (String s : tags) {
            List<String> tagsWithout = new ArrayList<String>();
            tagsWithout.addAll(tags);

            tagsWithout.remove(s);
            combination(tagsWithout, ret);
        }
    }
}
