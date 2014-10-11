package tue.dnssd.context.tag.naming;

import tue.dnssd.context.tag.naming.formula.Formula;
import tue.dnssd.jmdns.ServiceInfo;
import tue.dnssd.jmdns.impl.DNSMessage;
import tue.dnssd.jmdns.impl.DNSQuestion;
import tue.dnssd.jmdns.impl.DNSRecord;
import tue.dnssd.jmdns.impl.constants.DNSRecordClass;
import java.util.*;

/**
 * This is the class implementing Sorted Tags Nested naming scheme.
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
 * Only the most discriminating name (that is: the one containing all the tags from input) will point to the service name.
 * Other names will point to the most discriminating one.
 * Created by nina on 5/11/14.
 */
public class SortedTagsNestedNamingScheme extends NamingScheme {
    private String[] limitingTags; //Starting from this set of tags, everything will point to the service itself

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

        if(ptrNames.size()==0)
            return ret;

        //Select the longest one
        String mostExpressivePtrName=ptrNames.get(0)+service.getTypeWithSubtype();
        DNSRecord allRecord = new DNSRecord.Pointer(mostExpressivePtrName, DNSRecordClass.CLASS_ANY, false, ttl, serviceKey);
        ret.add(allRecord);

        //Point everyone else to it
        for (int i=1;i<ptrNames.size();i++) {
            String ptrName=ptrNames.get(i);
            ptrName=ptrName+service.getTypeWithSubtype();
            DNSRecord record = new DNSRecord.Pointer(ptrName, DNSRecordClass.CLASS_ANY, false, ttl, mostExpressivePtrName);
            ret.add(record);
        }
        return ret;
    }

    @Override
    public List<DNSQuestion> getRecordsForSearch(List<String> tagsForOperation, ServiceSearchOperation operation,
                                                 String type, String protocol, String domain) {
        return new SortedTagsConcatNamingScheme().getRecordsForSearch(tagsForOperation,operation,type,protocol,domain);
    }

    @Override
    public Set<ServiceSearchOperation> getAvailableSearchOperations() {
        Set<ServiceSearchOperation> ret=new HashSet<ServiceSearchOperation>(2);
        ret.add(ServiceSearchOperation.intersection);
        ret.add(ServiceSearchOperation.union);

        return ret;
    }

    @Override
    public Set<String> getTagsForString(String str, String fullServiceTypeWithDomain) {
        Set<String> ret=new HashSet<String>(20);
        if(str.endsWith(fullServiceTypeWithDomain) && !str.equals(fullServiceTypeWithDomain)) {
            str = str.substring(0, str.lastIndexOf(fullServiceTypeWithDomain) - 1);


            String[] tagsStrings = str.split(".");

            Collections.addAll(ret, tagsStrings);
        }

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
        return new SortedTagsConcatNamingScheme().getRecordsForSearchF(formula,type,protocol,domain);
    }

    @Override
    public boolean isSatisfying(DNSMessage message, Formula formula) {
        return new SortedTagsConcatNamingScheme().isSatisfying(message,formula);
    }
}
