package tue.dnssd.context.tag.naming.formula;

import java.util.ArrayList;
import java.util.Collections;
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

    public boolean equals(Object o){
        if(o instanceof  Conjunction){
            Conjunction otherConjunction=(Conjunction)o;

//            if(!(otherConjunction.nonNegatedTags.size()==nonNegatedTags.size()))return false;
            if(!(otherConjunction.hashCode()==hashCode()))
                return false;

//            if(!(otherConjunction.negatedTags.containsAll(negatedTags)&&negatedTags.containsAll(otherConjunction.negatedTags)))
//                return false;
            return true;
        }else  return false;
    }

    public String toString(){
        Collections.sort(nonNegatedTags);
        String ret="";
        ret+="(";
        for(String s:nonNegatedTags)
            ret+=s+"*";
        ret=ret.substring(0,ret.length()-1);
        ret+=")";
        return ret;
    }

    private int hashCode=0;
    public int hashCode(){
        if(hashCode!=0)return hashCode;
        else {
            hashCode=toString().hashCode();
            return hashCode;
        }
    }

}
