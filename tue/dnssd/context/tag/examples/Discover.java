package tue.dnssd.context.tag.examples;

import tue.dnssd.context.tag.naming.NamingScheme;
import tue.dnssd.context.tag.naming.ServiceSearchOperation;
import tue.dnssd.context.tag.naming.TagToPointerNamingScheme;
import tue.dnssd.context.tag.publish.DNSRecordListener;
import tue.dnssd.context.tag.publish.ServiceDiscoverer;
import tue.dnssd.jmdns.impl.DNSEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nina on 5/26/14.
 */
public class Discover {
    public static void DiscoverService(String serviceType, String serviceProtocol, String serviceDomain){
        //Select the naming scheme to use
        NamingScheme namingScheme=new TagToPointerNamingScheme();

        //Do we need services with all tags specified or with any of them? We choose the latter here.
        ServiceSearchOperation operation=ServiceSearchOperation.union;

        //Tags to be applied to our service
        List<String> tags=new ArrayList<String>(){{
            add("tag1");
            add("tag2");
            add("tag100500");
        }};

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
            discoverer.searchForService(tags,operation,serviceType,serviceProtocol,serviceDomain,listener);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        DiscoverService("supertype","tcp","local");
    }
}
