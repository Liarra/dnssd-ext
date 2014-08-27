package tue.dnssd.context.tag.naming;

import tue.dnssd.context.tag.naming.formula.Conjunction;
import tue.dnssd.context.tag.naming.formula.Formula;
import tue.dnssd.jmdns.ServiceInfo;
import tue.dnssd.jmdns.impl.DNSMessage;
import tue.dnssd.jmdns.impl.DNSQuestion;
import tue.dnssd.jmdns.impl.DNSRecord;
import tue.dnssd.jmdns.impl.constants.DNSRecordClass;
import tue.dnssd.jmdns.impl.constants.DNSRecordType;

import java.util.*;

/**
 * This naming scheme sends a whole request formula in one DNS message. Resolution is on the server side.
 * Created by nina on 8/11/14.
 */
public class FormulaNamingScheme extends NamingScheme {
    /**
     * Returns the set of records that user need to publish alongside with service records to make a service discoverable
     * with tags.
     *
     * @param service The service to which tags should be applied. Be sure to add tags to this instance beforehand
     *                with {@link tue.dnssd.jmdns.ServiceInfo#addTags(java.util.Collection)} method.
     * @return The list of records representing the tags mapping to the service.
     */
    @Override
    public List<DNSRecord> getRecordsForPublishing(ServiceInfo service) {
        return new ArrayList<DNSRecord>();
    }

    /**
     * Returns a set of DNS questions the user needs to send out in order to discover the service with given tags set.
     *
     * @param tagsForOperation List of tags to search for
     * @param operation        The type of search operation to perform. See {@link tue.dnssd.context.tag.naming.ServiceSearchOperation} for more information.
     * @param type             The type of service to search for without dot and leading underscore. For example, "ipp" or "http".
     * @param protocol         The protocol of service to search for without dot and leading underscore. For example, "tcp" or "upd"
     * @param domain           The domain of service to search for without dot. For example, "local"
     * @return The list of questions to send out in order to discover a service.
     */
    @Override
    public List<DNSQuestion> getRecordsForSearch(List<String> tagsForOperation, ServiceSearchOperation operation, String type, String protocol, String domain) {
        String N="";
        String jmDNSTypeString=getJmDNSTypeStringFromIndividualServiceTypeFields(type,protocol,domain);
        List<DNSQuestion> ret=new ArrayList<DNSQuestion>();

        switch (operation){
            case union:
                N=TagsCombiner.concatenateListWithCharacter(tagsForOperation,".");
                break;
            case intersection:
                N=TagsCombiner.concatenateListWithCharacter(tagsForOperation,"*");
                break;
            default:
                N="";
        }
        N=N+"."+jmDNSTypeString;
        DNSQuestion retEntry=DNSQuestion.newQuestion(N, DNSRecordType.TYPE_PTR, DNSRecordClass.CLASS_ANY,false);
        ret.add(retEntry);
        return ret;
    }

    /**
     * Returns a list of search operations that current naming scheme supports.
     *
     * @return
     */
    @Override
    public Set<ServiceSearchOperation> getAvailableSearchOperations() {
        return new HashSet<ServiceSearchOperation>(){{add(ServiceSearchOperation.union); add(ServiceSearchOperation.intersection);}};
    }

    /**
     * Returns set of tags that given name contains.
     *
     * @param str
     * @param fullServiceTypeWithDomain Full service type with subtype in the form of <code>_type._protocol.domain</code>.
     *                                  We need it to make sure we don't include any of type components to the tags list.
     * @return
     */
    @Override
    public Set<String> getTagsForString(String str, String fullServiceTypeWithDomain) {
        if(str.endsWith(fullServiceTypeWithDomain))
            str=str.substring(0,str.lastIndexOf(fullServiceTypeWithDomain)-1);

        String[] tagsStrings=str.split("\\.*");

        Set<String> ret=new HashSet<String>(tagsStrings.length);
        Collections.addAll(ret, tagsStrings);

        return ret;
    }

    /**
     * Returns a set of DNS questions the user needs to send out in order to discover the service with given tags set.
     *
     * @param formula  The boolean formula on tags that represents a desired context.
     * @param type     The type of service to search for without dot and leading underscore. For example, "ipp" or "http".
     * @param protocol The protocol of service to search for without dot and leading underscore. For example, "tcp" or "upd"
     * @param domain   The domain of service to search for without dot. For example, "local"
     * @return The list of questions to send out in order to discover a service.
     */
    @Override
    public List<DNSQuestion> getRecordsForSearchF(Formula formula, String type, String protocol, String domain) {
        List<DNSQuestion> ret=new ArrayList<DNSQuestion>();
        String name="";

        for(Conjunction c :formula.conjunctionList){
            name+=TagsCombiner.concatenateListWithCharacter(c.nonNegatedTags,"*");
            for(String s:c.negatedTags)
                name+="-"+s;
            name+=".";
        }

        String jmDNSTypeString=getJmDNSTypeStringFromIndividualServiceTypeFields(type,protocol,domain);
        name+=jmDNSTypeString;

        DNSQuestion retEntry=DNSQuestion.newQuestion(name, DNSRecordType.TYPE_PTR, DNSRecordClass.CLASS_ANY,false);
        ret.add(retEntry);
        return ret;
    }

    @Override
    public boolean isSatisfying(DNSMessage message, Formula formula) {
        Collection<? extends DNSRecord> answers=message.getAnswers();
        Set<String> formulaParts=new HashSet<String>();

        List<DNSQuestion> formulaQuestions=this.getRecordsForSearchF(formula, "", "", "");
        for(DNSQuestion q:formulaQuestions)
            formulaParts.add(q.getKey());


        for(DNSRecord r:answers){
            if(r.getRecordType()==DNSRecordType.TYPE_PTR) {
                String justName=(r.getKey().replaceFirst(r.getType(),""));
                if(formulaParts.contains(justName))
                    return  true;
            }
        }

        return false;
    }

    public boolean isSatisfying(String formulaFromPTR,String fullServiceTypeWithDomain, Collection<String> tags){
        if(formulaFromPTR.endsWith(fullServiceTypeWithDomain))
            formulaFromPTR=formulaFromPTR.substring(0,formulaFromPTR.lastIndexOf(fullServiceTypeWithDomain)-1);

         Formula f=getFormulaFromString(formulaFromPTR);
        return f.isSatisfiedBy(tags);
    }

    private Formula getFormulaFromString(String str){
        Formula ret=new Formula();
        String[] conjunctions=str.split("//.");

        for(String s:conjunctions){
            Conjunction c=new Conjunction();
            String[] nonNeg=s.split("//*");
            c.nonNegatedTags.addAll(Arrays.asList(nonNeg).subList(0, nonNeg.length - 1));
            String n=nonNeg[nonNeg.length-1];
            if(n.contains("-")){
                String[] Neg=n.split("-");
                c.nonNegatedTags.add(Neg[0]);
                c.negatedTags.addAll(Arrays.asList(Neg).subList(1,Neg.length));
            }
            else{
                c.nonNegatedTags.add(n);
            }

            ret.conjunctionList.add(c);
        }

        return ret;
    }
}
