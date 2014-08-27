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
 * This is the class implementing Tag To Pointer naming scheme.
 * In this naming scheme, for each of input tags a name is created.
 * For example, for input of {"tag1", "tag2", "tag3"} this scheme will consider following names:
 * <li>
 *     <ul>"tag1."</ul>
 *     <ul>"tag2."</ul>
 *     <ul>"tag3."</ul>
 * <li/>
 *
 * All of the names point to the service name.
 * <br/>
 * This scheme only supports {@link tue.dnssd.context.tag.naming.ServiceSearchOperation#union} search operation.
 * Created by nina on 5/7/14.
 */
public class TagToPointerNamingScheme extends NamingScheme {

    @Override
    public List<DNSRecord> getRecordsForPublishing(ServiceInfo service) {
        serviceKey=service.getQualifiedName();
        List<DNSRecord> ret=new ArrayList<DNSRecord>();

        Collection<String> tags=service.getTags();
        for(String tag:tags){
            tag+="."+service.getTypeWithSubtype();
            DNSRecord tagRecord=new DNSRecord.Pointer(tag, DNSRecordClass.CLASS_ANY,false,ttl,serviceKey);
            ret.add(tagRecord);
        }

        return ret;
    }

    @Override
    public List<DNSQuestion> getRecordsForSearch(List<String> tagsForOperation, ServiceSearchOperation operation, String type, String protocol, String domain) {
        switch (operation){
            default:
                List<DNSQuestion> ret=new ArrayList<DNSQuestion>();
                for(String t:tagsForOperation){
                    t=t+"."+getJmDNSTypeStringFromIndividualServiceTypeFields(type,protocol,domain);
                    DNSQuestion oneRetEntry= DNSQuestion.newQuestion(t, DNSRecordType.TYPE_PTR, DNSRecordClass.CLASS_ANY, false);
                    ret.add(oneRetEntry);
                }
                return ret;
        }
    }

    /**
     * Returns a list of search operations that current naming scheme supports.
     *
     * @return
     */
    @Override
    public Set<ServiceSearchOperation> getAvailableSearchOperations() {
        Set<ServiceSearchOperation> ret=new HashSet<ServiceSearchOperation>(1);
        ret.add(ServiceSearchOperation.union);

        return ret;
    }

    @Override
    public Set<String> getTagsForString(String str, String fullServiceTypeWithDomain) {
        if(str.equals(fullServiceTypeWithDomain))
            return new HashSet<String>();
        if(str.endsWith(fullServiceTypeWithDomain))
            str=str.substring(0,str.lastIndexOf(fullServiceTypeWithDomain)-1);

        Set<String> ret=new HashSet<String>(1);
        ret.add(str);
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
        HashSet<String> tags = new HashSet<String>();

        for (Conjunction c : formula.conjunctionList) {
            for (String t : c.negatedTags)
                tags.add(t);

            for (String t : c.nonNegatedTags)
                tags.add(t);

        }

        List<String> tagsList=new ArrayList<String>(tags);

        return getRecordsForSearch(tagsList,ServiceSearchOperation.union,type,protocol,domain);
    }

    @Override
    public boolean isSatisfying(DNSMessage message, Formula formula) {
        Collection<? extends DNSRecord> answers=message.getAnswers();
        Set<String> serviceTags=new HashSet<String>();

        for(DNSRecord r:answers){
            if(r.getRecordType()==DNSRecordType.TYPE_PTR) {
                Set<String> answerTag = this.getTagsForString(r.getKey(), r.getType());
                serviceTags.addAll(answerTag);
            }
        }

        return formula.isSatisfiedBy(serviceTags);
    }

}
