package tue.dnssd.context.tag.naming.formula;

import java.util.ArrayList;
import java.util.List;

/**
 * The conjunction of tags. Includes negated and not-negated tags.
 * Created by nina on 8/11/14.
 */
public class Conjunction {
    public Conjunction(){
        negatedTags=new ArrayList<String>();
        nonNegatedTags=new ArrayList<String>();
    }

    public Conjunction(List<String> negatedTags, List<String> nonNegatedTags) {
        this.negatedTags = negatedTags;
        this.nonNegatedTags = nonNegatedTags;
    }

    public List<String> negatedTags;
    public List<String> nonNegatedTags;

}
