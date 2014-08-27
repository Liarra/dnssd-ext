package tue.dnssd.context.tag.naming.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The boolean formula in DNF.
 * Created by nina on 8/11/14.
 */
public class Formula {
    public Formula(List<Conjunction> conjunctionList) {
        this.conjunctionList = conjunctionList;
    }

    public List<Conjunction> conjunctionList=new ArrayList<Conjunction>();

    /**
     * @param tags
     * @return true if this formula evaluates to TRUE with provided context tags.
     */
    public boolean isSatisfiedBy(Collection<String> tags){
        for(Conjunction c:conjunctionList){
            if(tags.containsAll(c.nonNegatedTags)){
                boolean b=true;
                for(String s:c.negatedTags){
                    if(tags.contains(s)) b=false;
                }

                if(b)
                    return true;
            }
        }
        return false;
    }
}
