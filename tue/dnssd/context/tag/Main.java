package tue;

import tue.dnssd.jmdns.JmDNS;
import tue.dnssd.jmdns.ServiceEvent;
import tue.dnssd.jmdns.ServiceInfo;
import tue.dnssd.jmdns.ServiceListener;

import tue.dnssd.jmdns.impl.DNSOutgoing;
import tue.dnssd.jmdns.impl.DNSRecord;
import tue.dnssd.jmdns.impl.JmDNSImpl;
import tue.dnssd.jmdns.impl.constants.DNSRecordClass;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    static JmDNS mdnsServer;
    public static void main(String[] args) {
        enableLogging();

        try {

            mdnsServer = JmDNS.create();
            System.out.println("created JmDNS");


            registerService();

//            registerTest();

//            sendPointer();

//            registerPointer();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void enableLogging(){
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        for (Enumeration<String> enumerator = LogManager.getLogManager().getLoggerNames(); enumerator.hasMoreElements();) {
            String loggerName = enumerator.nextElement();
            Logger logger = Logger.getLogger(loggerName);
            logger.addHandler(handler);
            logger.setLevel(Level.FINE);
        }
    }

    static void registerService() throws IOException
    {
        ServiceInfo testService = ServiceInfo.create("my-service-type", "TS", 6666,0,0, new  byte[1]);
        //Text explanation is not discoverable by avahi, hence
        //byte[1]
        mdnsServer.registerService(testService);
        System.out.println(testService.getKey()); //THE canonical specification-described representation of service
        System.out.println("service registered");
    }

    static DNSRecord.Pointer pointer=new DNSRecord.Pointer("tag1.", DNSRecordClass.CLASS_IN,false,600,"my-service-type");
    static void sendPointer() throws IOException {

        JmDNSImpl impl=((JmDNSImpl)mdnsServer);

        DNSOutgoing outgoing=new DNSOutgoing(0,true);
        outgoing.addAnswer(pointer,new Date().getTime());
        impl.send(outgoing);



        System.out.println("sent pointer");
    }

    static void registerPointer(){
        JmDNSImpl impl=((JmDNSImpl)mdnsServer);

        impl.getCache().addDNSEntry(pointer);
        System.out.println("put pointer to cache");

//        impl.updateRecord(new Date().getTime(), pointer, JmDNSImpl.Operation.Add);
//        System.out.println("updateRecord on pointer");
    }

    static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added   : " + event.getName() + "." + event.getType());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed : " + event.getName() + "." + event.getType());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
        }
    }
}
