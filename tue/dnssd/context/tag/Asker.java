package tue.dnssd.context.tag;

import tue.dnssd.jmdns.JmDNS;
import tue.dnssd.jmdns.impl.DNSOutgoing;
import tue.dnssd.jmdns.impl.DNSQuestion;
import tue.dnssd.jmdns.impl.JmDNSImpl;
import tue.dnssd.jmdns.impl.constants.DNSRecordClass;
import tue.dnssd.jmdns.impl.constants.DNSRecordType;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//import tue.dnssd.jmdns.impl.TaggedServiceInfoImpl;

/**
 * Created by nina on 5/2/14.
 */
public class Asker {

    public static void main(String[] args){
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        for (Enumeration<String> enumerator = LogManager.getLogManager().getLoggerNames(); enumerator.hasMoreElements();) {
            String loggerName = enumerator.nextElement();
            Logger logger = Logger.getLogger(loggerName);
            logger.addHandler(handler);
            logger.setLevel(Level.FINE);
        }

        try {
            JmDNS asker=JmDNS.create();

            JmDNSImpl impl=((JmDNSImpl)asker);

//            ServicePublisher p=new ServicePublisher(new TagToPointerNamingScheme());
//            TaggedServiceInfoImpl serviceInfo=new TaggedServiceInfoImpl("type","Name","",5050,0,0,false,"");
//            serviceInfo.addTags(new ArrayList<String>(){{add("tag1");}});
//
//            p.publishServiceLazy(serviceInfo);

            DNSQuestion q=DNSQuestion.newQuestion("tag1.", DNSRecordType.TYPE_PTR,DNSRecordClass.CLASS_ANY,false);

            DNSOutgoing outgoing=new DNSOutgoing(0,true);
            outgoing.addQuestion(q);
            impl.send(outgoing);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
