package tue.dnssd.context.tag.publish;

import tue.dnssd.context.tag.naming.NamingScheme;
import tue.dnssd.context.tag.naming.ServiceSearchOperation;
import tue.dnssd.context.tag.naming.formula.Conjunction;
import tue.dnssd.context.tag.naming.formula.Formula;
import tue.dnssd.jmdns.DNSListener;
import tue.dnssd.jmdns.impl.*;
import tue.dnssd.jmdns.impl.constants.DNSRecordClass;
import tue.dnssd.jmdns.impl.constants.DNSRecordType;

import java.io.IOException;
import java.util.*;

/**
 * A class for discovering services with respect to their context tags.
 * Due to the unpredictability and asynchronous nature of underlying technology, this class uses Listener pattern rather than return
 * values directly.
 * <p/>
 * This class uses an instance of {@link  tue.dnssd.jmdns.impl.JmDNSImpl} for dns-sd communication.
 * Created by nina on 5/12/14.
 */
public class ServiceDiscoverer {
    NamingScheme namingScheme;
    final JmDNSImpl jmDNS;


    /**
     * Creates an instance with default {@link tue.dnssd.jmdns.impl.JmDNSImpl}
     *
     * @param namingScheme a naming scheme to use for this service discovery
     * @throws IOException in the case such is thrown by {@link tue.dnssd.jmdns.impl.JmDNSImpl#JmDNSImpl(java.net.InetAddress, String)}
     *                     (null,null) constructor.
     */
    public ServiceDiscoverer(NamingScheme namingScheme) throws IOException {
        this(namingScheme, new JmDNSImpl(null, null));
    }

    /**
     * Creates instance with given {@link tue.dnssd.jmdns.impl.JmDNSImpl}.
     *
     * @param namingScheme a naming scheme to use for this service discovery
     * @param jmDNS
     */
    public ServiceDiscoverer(NamingScheme namingScheme, JmDNSImpl jmDNS) {
        this.namingScheme = namingScheme;
        this.jmDNS = jmDNS;
    }



private HashSet<DNSQuestion> askedQuestions=new HashSet<DNSQuestion>();
    /**
     * Search for the service with given context constraint formula.
     *
     * @param type                   The type of service  without dot and leading underscore. For example, "ipp" or "http".
     * @param protocol               The protocol of service without dot and leading underscore. For example, "tcp" or "upd".
     * @param domain                 The domain of service without dot. For example, "local"
     * @param onServiceFoundListener this listener's method {@link tue.dnssd.context.tag.publish.DNSRecordListener#recordUpdated(tue.dnssd.jmdns.impl.DNSEntry, java.util.List)}
     *                               will be called once the desired service is found. May be called several times!
     * @throws IOException
     */
    public void searchForServiceF(
            final Formula formula,

            String type, String protocol, String domain,

            final DNSRecordListener onServiceFoundListener
    ) throws IOException {
        askedQuestions.clear();
        jmDNS.removeListener(currentListener);
        jmDNS.getCache().clear();

        DNSMessageListener l = new DNSMessageListener() {
            @Override
            public void messageArrived(DNSMessage m) throws IOException {
                boolean inNames=false;
                for(DNSRecord r:m.getAnswers()){
                        for(DNSQuestion q:askedQuestions){
                            if(q.getKey().equals(r.getKey()) && q.getType().equals(r.getType()))
                                inNames=true;
                        }
                }

                if (namingScheme.isSatisfying(m, formula)||inNames) {
                    boolean hasSRV=false;
                    for (DNSRecord r : m.getAnswers()) {
                        if (r.getRecordType() == DNSRecordType.TYPE_SRV) {
                            hasSRV=true;
                            onServiceFoundListener.recordUpdated(r, new ArrayList<String>());
                        }
                    }

                    if(!hasSRV){
                        for(DNSRecord r:m.getAnswers()){
                            if (r.getRecordType() == DNSRecordType.TYPE_PTR) {
                                DNSRecord.Pointer p = (DNSRecord.Pointer) r;

                                    final DNSQuestion question = DNSQuestion.newQuestion(p.getAlias(), DNSRecordType.TYPE_SRV, DNSRecordClass.CLASS_ANY, false);
                                    final DNSQuestion question2 = DNSQuestion.newQuestion(p.getAlias(), DNSRecordType.TYPE_PTR, DNSRecordClass.CLASS_ANY, false);

                                    DNSOutgoing out = new DNSOutgoing(0, true);
                                    if(!askedQuestions.contains(question)) {
                                        out.addQuestion(question);
                                        askedQuestions.add(question);
                                    }
                                    if(!askedQuestions.contains(question2)) {
                                        out.addQuestion(question2);
                                        askedQuestions.add(question2);
                                    }
                                if(!out.isEmpty())
                                    jmDNS.send(out);


////                                if(!names.contains(question.getName())) {
//                                    names.add(question.getName());
//                                    followTheName(dnsQuestions, onServiceFoundListener);
////                                }
                            }
                        }
                    }
                }
            }

        };

        jmDNS.messageListener = l;
        DNSOutgoing out = new DNSOutgoing(0, true);
        List<DNSQuestion> questions = namingScheme.getRecordsForSearchF(formula, type, protocol, domain);
        for (DNSQuestion q : questions)
            out.addQuestion(q);
        jmDNS.send(out);
    }

    /**
     * Search for the service with given context tags.
     *
     * @param tags                   the desired service should have.
     * @param operation              the operation for service discovery. See more at {@link tue.dnssd.context.tag.naming.ServiceSearchOperation}.
     * @param type                   The type of service  without dot and leading underscore. For example, "ipp" or "http".
     * @param protocol               The protocol of service without dot and leading underscore. For example, "tcp" or "upd".
     * @param domain                 The domain of service without dot. For example, "local"
     * @param onServiceFoundListener this listener's method {@link tue.dnssd.context.tag.publish.DNSRecordListener#recordUpdated(tue.dnssd.jmdns.impl.DNSEntry, java.util.List)}
     *                               will be called once the desired service is found. May be called several times!
     * @throws IOException
     */
    public void searchForService(
            List<String> tags,
            ServiceSearchOperation operation,
            String type, String protocol, String domain,

            final DNSRecordListener onServiceFoundListener
    ) throws IOException {


        Formula f = OpToF(tags, operation);
        searchForServiceF(f, type, protocol, domain, onServiceFoundListener);
//        names.clear();
//        jmDNS.removeListener(currentListener);
//        jmDNS.getCache().clear();
//
//        List<DNSQuestion> questions=namingScheme.getRecordsForSearch(tags, operation, type,protocol,domain);
//
//        for(DNSQuestion question:questions) {
//            names.add(question.getName());
//        }
//
//        followTheName(questions,onServiceFoundListener);
////            followTheName(question.getName(),onServiceFoundListener);
////        }
    }

    private Formula OpToF(List<String> tags,
                          ServiceSearchOperation operation) {
        Formula ret = new Formula();
        switch (operation) {
            case union:
                for (String s : tags) {
                    Conjunction c = new Conjunction();
                    c.nonNegatedTags.add(s);
                    ret.conjunctionList.add(c);
                }
                break;
            case intersection:
                Conjunction c = new Conjunction();
                for (String s : tags) {
                    c.nonNegatedTags.add(s);
                }
                if(c.nonNegatedTags.size()>0)
                    ret.conjunctionList.add(c);
                break;
            default:
                break;
        }

        return ret;
    }

    private final List<String> names = new ArrayList<String>();
    private final Map<String, List<String>> servicesContext = new HashMap<String, List<String>>();
    private DNSListener currentListener;

    private void followTheName(List<DNSQuestion> questions, final DNSRecordListener onServiceFoundListener) throws IOException {

        currentListener = new DNSListener() {

            @Override
            public void updateRecord(DNSCache Cache, long now, DNSEntry record) {
                if (!names.contains(record.getName())) {
                    return;
                }

                if (record.getRecordType() == DNSRecordType.TYPE_SRV) {
                    onServiceFoundListener.recordUpdated(record, servicesContext.get(record.getName()));
                } else if (record.getRecordType() == DNSRecordType.TYPE_PTR) {
                    final DNSRecord.Pointer pointer = (DNSRecord.Pointer) record;
                    String ptrAlias = pointer.getAlias();


                    if (servicesContext.containsKey(ptrAlias))
                        servicesContext.get(ptrAlias).add(pointer.getName());
                    else
                        servicesContext.put(ptrAlias, new ArrayList<String>() {{
                            add(pointer.getName());
                        }});

                    if (!names.contains(ptrAlias)) {
                        names.add(ptrAlias);
                        DNSQuestion newQuestionPTR = DNSQuestion.newQuestion(ptrAlias, DNSRecordType.TYPE_PTR, DNSRecordClass.CLASS_ANY, false);
                        DNSQuestion newQuestionSRV = DNSQuestion.newQuestion(ptrAlias, DNSRecordType.TYPE_SRV, DNSRecordClass.CLASS_ANY, false);

                        DNSOutgoing out = new DNSOutgoing(0, true);
                        try {
                            out.addQuestion(newQuestionPTR);
                            out.addQuestion(newQuestionSRV);

                            jmDNS.send(out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

//        DNSQuestion q=DNSQuestion.newQuestion(name, DNSRecordType.TYPE_PTR, DNSRecordClass.CLASS_ANY, false);

        jmDNS.addListener(currentListener, null);
        DNSOutgoing out = new DNSOutgoing(0, true);
        for (DNSQuestion q : questions)
            out.addQuestion(q);
        jmDNS.send(out);
    }
}
