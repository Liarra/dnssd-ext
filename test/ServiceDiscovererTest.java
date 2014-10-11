package tue.dnssd.context.tag.publish;

import org.junit.Assert;
import org.junit.Test;
import tue.dnssd.context.tag.experiments.CountPackets;
import tue.dnssd.context.tag.naming.*;
import tue.dnssd.jmdns.JmDNS;
import tue.dnssd.jmdns.ServiceInfo;
import tue.dnssd.jmdns.impl.DNSEntry;
import tue.dnssd.jmdns.impl.JmDNSImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDiscovererTest {
    private final String sName="SuperService",sType="printer",sProtocol="udp", domain="local";
    JmDNSImpl testImpl, testImplR;
    FormulaNamingScheme fip=new FormulaNamingScheme();
    SortedTagsConcatNamingScheme cip=new SortedTagsConcatNamingScheme();
    TagToPointerNamingScheme t2p=new TagToPointerNamingScheme();
    SortedTagsNestedNamingScheme nip=new SortedTagsNestedNamingScheme();

    int wait=10000, timeout=40;

    private JmDNSImpl newJmDNSImpl() throws IOException {
        if(testImpl!=null) {
            testImpl.unregisterAllServices();
            testImpl.cleanCache();
            testImpl.close();
            while(!testImpl.isClosed());
            testImpl=null;
            System.gc();
        }
        testImpl= (JmDNSImpl) JmDNS.create();
        return testImpl;
    }

    private JmDNSImpl newJmDNSImplReq() throws IOException {
        if(testImplR!=null) {
            testImplR.unregisterAllServices();
            testImpl.cleanCache();
            testImplR.close();
            while(!testImplR.isClosed());
            testImplR=null;
            System.gc();
        }
        testImplR= (JmDNSImpl) JmDNS.create();
        return testImplR;
    }

    static int numberTest=0;

    private ServiceInfo getServiceInfo(){
        ServiceInfo s=ServiceInfo.create("._"+sType+"._"+sProtocol+"."+domain, sName+(numberTest++), 8080, 0, 0, new byte[1]);
        return s;
    }

    private void PublishServiceWithTags(NamingScheme scheme, List<String> tags) throws IOException {
        ServicePublisher s=new ServicePublisher(scheme, newJmDNSImpl());
        ServiceInfo info=getServiceInfo();
        info.addTags(tags);
        s.publishService(info);
    }

    private List<String> getTags(){
        return new CountPackets().generateTagsSet(5);
    }


    static boolean found;
    @Test
    public void TestOneTagFormula() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(fip,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(fip,newJmDNSImplReq());

        for (final String s : tags) {
            found = false;
            List<String> searchTags = new ArrayList<String>() {{
                add(s);
            }};
            formulaDiscoverer.searchForService(
                    searchTags, ServiceSearchOperation.union, sType, sProtocol, domain,
                    new DNSRecordListener() {
                        @Override
                        public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                            found = true;
                        }
                    });

            Thread.sleep(wait);
            Assert.assertTrue(found);
        }
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
    }

    @Test
    public void TestOneTagT2P() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(t2p,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(t2p,newJmDNSImplReq());

        for (final String s : tags) {
            found = false;
            List<String> searchTags = new ArrayList<String>() {{
                add(s);
            }};
            formulaDiscoverer.searchForService(
                    searchTags, ServiceSearchOperation.union, sType, sProtocol, domain,
                    new DNSRecordListener() {
                        @Override
                        public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                            found = true;
                        }
                    });

            Thread.sleep(wait);
            Assert.assertTrue(found);
        }
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
    }

    @Test
    public void TestOneTagConj() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(cip,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(cip,newJmDNSImplReq());

        for (final String s : tags) {
            found = false;
            List<String> searchTags = new ArrayList<String>() {{
                add(s);
            }};
            formulaDiscoverer.searchForService(
                    searchTags, ServiceSearchOperation.union, sType, sProtocol, domain,
                    new DNSRecordListener() {
                        @Override
                        public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                            found = true;
                        }
                    });

            Thread.sleep(wait);
            Assert.assertTrue(found);
        }
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
    }

    @Test
    public void TestOneTagNest() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(nip,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(nip,newJmDNSImplReq());

        for (final String s : tags) {
            found = false;
            List<String> searchTags = new ArrayList<String>() {{
                add(s);
            }};
            formulaDiscoverer.searchForService(
                    searchTags, ServiceSearchOperation.union, sType, sProtocol, domain,
                    new DNSRecordListener() {
                        @Override
                        public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                            found = true;
                        }
                    });

            Thread.sleep(wait);
            Assert.assertTrue(found);
        }
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
    }


    ///////////All tags at once////////////////////////////////
    @Test
    public void TestAllTagsFormula() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(fip,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(fip,newJmDNSImplReq());

            found = false;

            formulaDiscoverer.searchForService(
                    tags, ServiceSearchOperation.intersection, sType, sProtocol, domain,
                    new DNSRecordListener() {
                        @Override
                        public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                            found = true;
                        }
                    });

            Thread.sleep(wait);
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
            Assert.assertTrue(found);
    }

    @Test
    public void TestAllTagsT2P() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(t2p,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(t2p,newJmDNSImplReq());

        found = false;

        formulaDiscoverer.searchForService(
                tags, ServiceSearchOperation.intersection, sType, sProtocol, domain,
                new DNSRecordListener() {
                    @Override
                    public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                        found = true;
                    }
                });

        Thread.sleep(wait);
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
        Assert.assertTrue(found);
    }

    @Test
    public void TestAllTagsConj() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(cip,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(cip,newJmDNSImplReq());

        found = false;

        formulaDiscoverer.searchForService(
                tags, ServiceSearchOperation.intersection, sType, sProtocol, domain,
                new DNSRecordListener() {
                    @Override
                    public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                        found = true;
                    }
                });

        Thread.sleep(wait);
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
        Assert.assertTrue(found);
    }

    @Test
    public void TestAllTagsNest() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(nip,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(nip,newJmDNSImplReq());

        found = false;

        formulaDiscoverer.searchForService(
                tags, ServiceSearchOperation.intersection, sType, sProtocol, domain,
                new DNSRecordListener() {
                    @Override
                    public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                        found = true;
                    }
                });

        Thread.sleep(wait);
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
        Assert.assertTrue(found);
    }

    //////////////////////No tags////////////////////////////
    @Test
    public void TestNoTagsNest() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(nip,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(nip,newJmDNSImplReq());

        found = false;

        formulaDiscoverer.searchForService(
                new ArrayList<String>(), ServiceSearchOperation.intersection, sType, sProtocol, domain,
                new DNSRecordListener() {
                    @Override
                    public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                        found = true;
                    }
                });

        Thread.sleep(wait);
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
        Assert.assertTrue(found);
    }

    @Test
    public void TestNoTagsConj() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(cip,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(cip,newJmDNSImplReq());

        found = false;

        formulaDiscoverer.searchForService(
                new ArrayList<String>(), ServiceSearchOperation.intersection, sType, sProtocol, domain,
                new DNSRecordListener() {
                    @Override
                    public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                        found = true;
                    }
                });

        Thread.sleep(wait);
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
        Assert.assertTrue(found);
    }


    @Test
    public void TestNoTagsTagToPointer() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(t2p,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(t2p,newJmDNSImplReq());

        found = false;

        formulaDiscoverer.searchForService(
                new ArrayList<String>(), ServiceSearchOperation.intersection, sType, sProtocol, domain,
                new DNSRecordListener() {
                    @Override
                    public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                        found = true;
                    }
                });

        Thread.sleep(wait);
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
        Assert.assertTrue(found);
    }

    @Test
    public void TestNoTagsFormula() throws IOException, InterruptedException {
        List<String> tags=getTags();
        PublishServiceWithTags(fip,tags);

        Thread.sleep(timeout);

        ServiceDiscoverer formulaDiscoverer = new ServiceDiscoverer(fip,newJmDNSImplReq());

        found = false;

        formulaDiscoverer.searchForService(
                new ArrayList<String>(), ServiceSearchOperation.intersection, sType, sProtocol, domain,
                new DNSRecordListener() {
                    @Override
                    public void recordUpdated(DNSEntry record, List<String> PTRPointerName) {
                        found = true;
                    }
                });

        Thread.sleep(wait);
        formulaDiscoverer.jmDNS.cleanCache();
        formulaDiscoverer.jmDNS.close();
        Assert.assertTrue(found);
    }



    @Test
    public void testSearchForServiceAllTagsCombinations() throws Exception {

    }

    @Test
    public void testSearchForServiceBefore() throws Exception {

    }

    @Test
    public void testSearchForServiceDelayed() throws Exception {

    }

    @Test
    public void testSearchForServiceWrongType() throws Exception {

    }

    @Test
    public void testSearchForServiceOneBadTagInConjunction() throws Exception {

    }

    @Test
    public void testSearchForServiceOneWrongTag() throws Exception {

    }

}