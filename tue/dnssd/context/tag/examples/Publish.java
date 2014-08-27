package tue.dnssd.context.tag.examples;

import tue.dnssd.context.tag.naming.NamingScheme;
import tue.dnssd.context.tag.naming.TagToPointerNamingScheme;
import tue.dnssd.context.tag.publish.ServicePublisher;
import tue.dnssd.jmdns.ServiceInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Publish {

    public static void PublishService(String serviceName, String serviceType, int port){
        //Select the naming scheme to use
        NamingScheme namingScheme=new TagToPointerNamingScheme();

        //Create a serviceInfo object, representing our service.
        ServiceInfo serviceInfo=ServiceInfo.create(serviceType,serviceName,port,0, 0, new byte[1]);

        //Tags to be applied to our service
        List<String> tags=new ArrayList<String>(){{
            add("tag1");
            add("tag2");
            add("tag100500");
        }};

        //Adding tags to the service description
        serviceInfo.addTags(tags);

        try {
            ServicePublisher publisher=new ServicePublisher(namingScheme);
            publisher.publishService(serviceInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        //You can use full qualified name...
        PublishService("SuperService","_supertype._tcp.local.",9090);

         //Or just an arbitrary type name, in this case we'll automatically add "_tcp.local" to the end
        PublishService("MegaService","megatype",9090);
    }
}
