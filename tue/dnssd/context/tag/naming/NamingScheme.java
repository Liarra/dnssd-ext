package tue.dnssd.context.tag.naming;

import tue.dnssd.context.tag.naming.formula.Formula;
import tue.dnssd.jmdns.ServiceInfo;
import tue.dnssd.jmdns.impl.DNSMessage;
import tue.dnssd.jmdns.impl.DNSQuestion;
import tue.dnssd.jmdns.impl.DNSRecord;

import java.util.List;
import java.util.Set;

/**
 * Created by nina on 5/7/14.
 * This class describes a naming scheme for a tagged service. It can produce list of records that you need to
 * publish alongside with service records to make it discoverable with tags. It also can produce a list of questions
 *  that you send out in order to discover service with tags.
 */
public abstract class NamingScheme {
    int ttl=600;
    protected String serviceKey;
    protected  String type, protocol,domain;

    /**
     * Returns the set of records that user need to publish alongside with service records to make a service discoverable
     *  with tags.
     * @param service The service to which tags should be applied. Be sure to add tags to this instance beforehand
     *                with {@link tue.dnssd.jmdns.ServiceInfo#addTags(java.util.Collection)} method.
     * @return The list of records representing the tags mapping to the service.
     */
    public abstract List<DNSRecord> getRecordsForPublishing(ServiceInfo service);

    /**
     * Returns a set of DNS questions the user needs to send out in order to discover the service with given tags set.
     * @param tagsForOperation List of tags to search for
     * @param operation The type of search operation to perform. See {@link tue.dnssd.context.tag.naming.ServiceSearchOperation} for more information.
     * @param type The type of service to search for without dot and leading underscore. For example, "ipp" or "http".
     * @param protocol The protocol of service to search for without dot and leading underscore. For example, "tcp" or "upd"
     * @param domain The domain of service to search for without dot. For example, "local"
     * @return The list of questions to send out in order to discover a service.
     */
    public abstract List<DNSQuestion> getRecordsForSearch(
            List<String> tagsForOperation,
            ServiceSearchOperation operation,
            String type, String protocol, String domain);

    /**
     * Returns a JmDNS-compatible type string, consisting of service type, protocol and domain.
     * @param type  The type of service  without dot and leading underscore. For example, "ipp" or "http".
     * @param protocol The protocol of service without dot and leading underscore. For example, "tcp" or "upd".
     * @param domain The domain of service without dot. For example, "local"
     * @return A JmDNS-compatible type string, for example, "_ipp._tcp.local".
     */
    protected String getJmDNSTypeStringFromIndividualServiceTypeFields(String type, String protocol, String domain){
        String jmDNSTypeString="_"+type+"._"+protocol+"."+domain+".";
        return  jmDNSTypeString;
    }

    /**
     * Returns a list of search operations that current naming scheme supports.
     * @return
     */
    public abstract Set<ServiceSearchOperation> getAvailableSearchOperations();

    /**
     * Returns set of tags that given name contains.
     * @param str
     * @param fullServiceTypeWithDomain Full service type with subtype in the form of <code>_type._protocol.domain</code>.
     *                                  We need it to make sure we don't include any of type components to the tags list.
     * @return
     */
    public abstract Set<String> getTagsForString(String str, String fullServiceTypeWithDomain);


    /**
     * Returns a set of DNS questions the user needs to send out in order to discover the service with given tags set.
     * @param formula The boolean formula on tags that represents a desired context.
     * @param type The type of service to search for without dot and leading underscore. For example, "ipp" or "http".
     * @param protocol The protocol of service to search for without dot and leading underscore. For example, "tcp" or "upd"
     * @param domain The domain of service to search for without dot. For example, "local"
     * @return The list of questions to send out in order to discover a service.
     */
    public abstract List<DNSQuestion> getRecordsForSearchF(
            Formula formula,
            String type, String protocol, String domain);

    /**
     * Checks that records in this record represent the set of tags satisfying the given formula.
     * It is assumed that one DNS message corresponds to one service name.
     * @param formula The boolean formula on tags that represents a desired context.
     * @param message The message containing context information for service name.
     * @return true if the context in given message satisfies the given formula.
     */
    public abstract boolean isSatisfying(DNSMessage message, Formula formula);
}
