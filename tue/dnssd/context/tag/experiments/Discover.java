package tue.dnssd.context.tag.experiments;

import tue.dnssd.context.tag.naming.NamingScheme;
import tue.dnssd.context.tag.naming.TagToPointerNamingScheme;
import tue.dnssd.context.tag.naming.formula.Conjunction;
import tue.dnssd.context.tag.naming.formula.Formula;
import tue.dnssd.context.tag.publish.DNSRecordListener;
import tue.dnssd.context.tag.publish.ServiceDiscoverer;
import tue.dnssd.jmdns.impl.DNSEntry;

import java.io.IOException;
import java.util.List;

/**
 * Created by nina on 5/26/14.
 */
public class Discover {
    public static void DiscoverService(String serviceType, String serviceProtocol, String serviceDomain){
        //Select the naming scheme to use
        NamingScheme namingScheme=new TagToPointerNamingScheme();

        Conjunction c=new Conjunction();
        c.nonNegatedTags.add("tag_1");
        c.nonNegatedTags.add("tag_2");
        c.negatedTags.add("tag_3");

        Conjunction c2=new Conjunction();
        c2.nonNegatedTags.add("tag_4");

        Formula f=new Formula();
        f.conjunctionList.add(c);
        f.conjunctionList.add(c2);

        //This listener specifies what we want to do when we find a service.
        DNSRecordListener listener=new DNSRecordListener() {
            @Override
            public void recordUpdated(DNSEntry record, List<String> context) {
                //We want to print it to the console!
                System.out.println(record);
            }
        };

        try {
            ServiceDiscoverer discoverer=new ServiceDiscoverer(namingScheme);
            discoverer.searchForServiceF(f,serviceType,serviceProtocol,serviceDomain,listener);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        DiscoverService("supertype","tcp","local");
    }
}
