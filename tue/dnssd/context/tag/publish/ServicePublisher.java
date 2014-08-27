package tue.dnssd.context.tag.publish;

import tue.dnssd.context.tag.naming.NamingScheme;
import tue.dnssd.jmdns.ServiceInfo;
import tue.dnssd.jmdns.impl.DNSRecord;
import tue.dnssd.jmdns.impl.JmDNSImpl;

import java.io.IOException;
import java.util.List;

/**
 *  A class for discovering services with respect to their context tags.
 *  This class uses an instance of {@link  tue.dnssd.jmdns.impl.JmDNSImpl} for dns-sd communication.
 *
 * Created by nina on 5/12/14.
 */
public class ServicePublisher {
    private NamingScheme namingScheme;
    JmDNSImpl jmDNS;

    /**
     * Creates an instance with default {@link tue.dnssd.jmdns.impl.JmDNSImpl}
     * @param namingScheme a naming scheme to use for this service discovery
     * @throws IOException in the case such is thrown by {@link tue.dnssd.jmdns.impl.JmDNSImpl#JmDNSImpl(java.net.InetAddress, String)}
     * (null,null) constructor.
     */
    public ServicePublisher(NamingScheme namingScheme) throws IOException {
        this(namingScheme,new JmDNSImpl(null, null));
    }

    /**
     * Creates instance with given {@link tue.dnssd.jmdns.impl.JmDNSImpl}.
     * @param namingScheme a naming scheme to use for this service
     * @param jmDNS
     */
    public ServicePublisher(NamingScheme namingScheme, JmDNSImpl jmDNS){
        this.namingScheme = namingScheme;
        this.jmDNS=jmDNS;
    }

    /**
     * Publishes the service with given context tags.
     * @param serviceInfo a service to be published. Don't forget to include tags in advance, by calling
     *                    {@link tue.dnssd.jmdns.ServiceInfo#addTags(java.util.Collection)}
     * @throws IOException
     */
    public void publishService(ServiceInfo serviceInfo) throws IOException {
        serviceInfo.set_namingSchemePublishedUnder(namingScheme);
        jmDNS.registerService(serviceInfo);
        List<DNSRecord> additionalRecords=namingScheme.getRecordsForPublishing(serviceInfo);
        jmDNS.publishDNSRecords(additionalRecords);
    }

    /**
     * Publishes the service info, but not the tag records. This way tag records are created and sent on demand.
     * @param serviceInfo a service to be published. Don't forget to include tags in advance, by calling
     *                    {@link tue.dnssd.jmdns.ServiceInfo#addTags(java.util.Collection)}
     * @throws IOException
     */
    public void publishServiceLazy(final ServiceInfo serviceInfo) throws IOException {
        serviceInfo.set_namingSchemePublishedUnder(namingScheme);
        jmDNS.registerService(serviceInfo);
    }

    /**
     * Unpublishes the service.
     * @param serviceInfo
     */
    public void unpublishService(ServiceInfo serviceInfo){
        serviceInfo.set_namingSchemePublishedUnder(null);
        jmDNS.unregisterService(serviceInfo);
    }
}
