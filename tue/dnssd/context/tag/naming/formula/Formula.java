package tue.dnssd.context.tag.naming.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The boolean formula in DNF.
 * Created by nina on 8/11/14.
 */
public class Formula {
    public Formula() {
        this.conjunctionList = new ArrayList<Conjunction>();
    }

    public Formula(List<Conjunction> conjunctionList) {
        this.conjunctionList = conjunctionList;
    }

    /**
     * @param tags
     * @return true if this formula evaluates to TRUE with provided context tags.
     */
    public boolean isSatisfiedBy(Collection<String> tags){
        if(conjunctionList.size()==0)return true;
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

    public List<Conjunction> conjunctionList=new ArrayList<Conjunction>();

    public String toString(){
        String ret="";

        for(Conjunction c:conjunctionList){
            ret=ret+c.toString()+"+";
        }
        ret=ret.substring(0,ret.length()-1);
        return ret;
    }

    public boolean equals(Object o){
        if(o instanceof  Formula){
            Formula otherFormula=(Formula)o;
//            if(otherFormula.conjunctionList.size()!=conjunctionList.size())return false;
            if(otherFormula.hashCode()!=hashCode())
                return false;

            return true;
        }else  return false;
    }

    public boolean isRedundant(){
        for(Conjunction c: conjunctionList){
            for(Conjunction c1:conjunctionList){
                if(c1.nonNegatedTags.containsAll(c.nonNegatedTags)&&!c.equals(c1))
                    return true;
            }
        }

        return false;
    }

    int hc=0;
    public int hashCode(){
        if(hc!=0)return hc;
        else{
            hc=toString().hashCode();
            return hc;
        }
    }
}
