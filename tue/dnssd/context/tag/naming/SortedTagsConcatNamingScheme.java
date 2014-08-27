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
 * This is the class implementing Sorted Tags Concatenated naming scheme.
 * In this naming scheme, input tags are sorted; then they are concatenated together with dots. Also possible combinations
 * of input tags are also considered.
 *
 * For example, for input of {"tag1", "tag2", "tag3"} this scheme will consider following names:
 * <li>
 *     <ul>"tag1.tag2.tag3"</ul>
 *     <ul>"tag1.tag2."</ul>
 *     <ul>"tag1.tag3"</ul>
 *     <ul>"tag1."</ul>
 *     <ul>"tag2.tag3"</ul>
 *     <ul>"tag2."</ul>
 *     <ul>"tag3."</ul>
 * <li/>
 *
 * All of resulting records will point to the service name.
 * Created by nina on 5/7/14.
 */
public class SortedTagsConcatNamingScheme extends NamingScheme {
    @Override
    public List<DNSRecord> getRecordsForPublishing(ServiceInfo service) {

        serviceKey=service.getQualifiedName();

        List<DNSRecord> ret = new ArrayList<DNSRecord>();

        Collection<String> tagsColl=service.getTags();
        List<String> tags=new ArrayList<String>();
        tags.addAll(tagsColl);

        Collections.sort(tags);
        List<String> ptrNames = new ArrayList<String>();
        TagsCombiner.combination(tags, ptrNames);

        for (String ptrName : ptrNames) {
            ptrName=ptrName+service.getTypeWithSubtype();
            DNSRecord allRecord = new DNSRecord.Pointer(ptrName, DNSRecordClass.CLASS_ANY, false, ttl, serviceKey);
            ret.add(allRecord);
        }

        return ret;
    }

    @Override
    public List<DNSQuestion> getRecordsForSearch(List<String> tagsForOperation,
                                                 ServiceSearchOperation operation,
                                                 String type, String protocol, String domain) {
        String jmDNSTypeString=getJmDNSTypeStringFromIndividualServiceTypeFields(type,protocol,domain);
        List<DNSQuestion> ret=new ArrayList<DNSQuestion>();
        switch (operation){
            case intersection:
                Collections.sort(tagsForOperation);
                String key= TagsCombiner.concatenateListWithCharacter(tagsForOperation, ".")+jmDNSTypeString;
                DNSQuestion retEntry=DNSQuestion.newQuestion(key, DNSRecordType.TYPE_PTR,DNSRecordClass.CLASS_ANY,false);
                ret.add(retEntry);
                return ret;

            case union:
                for(String t:tagsForOperation){
                    t=t+"."+jmDNSTypeString;
                    DNSQuestion oneRetEntry=DNSQuestion.newQuestion(t, DNSRecordType.TYPE_PTR,DNSRecordClass.CLASS_ANY,false);
                    ret.add(oneRetEntry);
                }
                return ret;
            default:
                throw new IllegalArgumentException("operation");
        }
    }

    @Override
    public Set<ServiceSearchOperation> getAvailableSearchOperations() {
        Set<ServiceSearchOperation> ret=new HashSet<ServiceSearchOperation>(2);
        ret.add(ServiceSearchOperation.intersection);
        ret.add(ServiceSearchOperation.union);

        return ret;
    }

    @Override
    public Set<String> getTagsForString(String str,String fullServiceTypeWithDomain) {
        if(str.endsWith(fullServiceTypeWithDomain))
            str=str.substring(0,str.lastIndexOf(fullServiceTypeWithDomain)-1);

        String[] tagsStrings=str.split("\\.");

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
        HashSet<String> negatedTags=new HashSet<String>();
        List<DNSQuestion> ret=new ArrayList<DNSQuestion>();

        for(Conjunction c :formula.conjunctionList){
            ret.addAll(getRecordsForSearch(c.nonNegatedTags, ServiceSearchOperation.intersection, type, protocol, domain));
            negatedTags.addAll(c.negatedTags);
        }

        List<String> negTagsList=new ArrayList<String>(negatedTags);
        ret.addAll(getRecordsForSearch(negTagsList,ServiceSearchOperation.union,type,protocol,domain));

        return ret;
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
