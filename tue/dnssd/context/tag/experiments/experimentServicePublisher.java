package tue.dnssd.context.tag.experiments;

import tue.dnssd.context.tag.naming.NamingScheme;
import tue.dnssd.context.tag.naming.ServiceSearchOperation;
import tue.dnssd.context.tag.naming.SortedTagsConcatNamingScheme;
import tue.dnssd.context.tag.publish.DNSRecordListener;
import tue.dnssd.context.tag.publish.ServiceDiscoverer;
import tue.dnssd.context.tag.publish.ServicePublisher;
import tue.dnssd.jmdns.ServiceInfo;
import tue.dnssd.jmdns.impl.DNSEntry;
import tue.dnssd.jmdns.impl.ServiceInfoImpl;

import java.io.*;
import java.util.*;

/**
 * Needs to take a random words form the dictionary as tags
 * Created by nina on 5/27/14.
 */
public class experimentServicePublisher {
    static final String dictionaryFilePath="/home/nina/john.txt";

    public static List<String> getTagNames(int howMany) throws IOException {
        List<String> ret=new ArrayList<String>();
        Random r=new Random(new Date().getTime());

        int maxLine=getLinesCount();

        File dictionaryFile=new File(dictionaryFilePath);
        FileReader reader=new FileReader(dictionaryFile);
        BufferedReader bufferedReader=new BufferedReader(reader);

        int[] wordLines=new int[howMany];

        for(int i=0;i<howMany;i++) {
            int lineNum = r.nextInt(maxLine);
            wordLines[i] = lineNum;
        }

        Arrays.sort(wordLines);

        String word="";
        for(int i=0,j=0;i<howMany;j++){
            word=bufferedReader.readLine();

            if(j==wordLines[i]) {
                i++;
                ret.add(word);
            }
        }

        bufferedReader.close();

        return ret;
    }

    public static List<String> getTagNamesFixSize(int howmany, int length){
        List<String> ret=new ArrayList<String>();
        for(int i=0;i<howmany*2;i+=2){
            String s="";
                char c1 = (char) ('0' + i);
                char c2 = (char) ('0' + i+1);
            s+=c1;
            s+=c2;
            s+=c1;
            ret.add(s);
        }

        return ret;
    }

    private static int getLinesCount() throws IOException {
        File dictionaryFile=new File(dictionaryFilePath);
        FileReader reader=new FileReader(dictionaryFile);
        LineNumberReader  lnr = new LineNumberReader(reader);

        lnr.skip(Long.MAX_VALUE);
        int maxLine=lnr.getLineNumber();
        lnr.close();
        return maxLine;
    }

    static int TagSize=3;
    private static void experimentPublish(int num){
        try {
            System.out.println("Publish service with "+num+" tags...");
            ServiceInfo info=ServiceInfo.create("extype","Experimental Service"+num,7070+num,"Experiment with "+num+" tags");
            NamingScheme scheme=new SortedTagsConcatNamingScheme();

            List<String> tags=getTagNamesFixSize(num, 0);
            info.addTags(tags);

            ServicePublisher publisher=new ServicePublisher(scheme);
            publisher.publishService(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void experimentDiscover(int numSrv,int numReq){
//        String type="extype";
        String type="_lgt._udp.local";
        NamingScheme scheme=new SortedTagsConcatNamingScheme();
        ServiceSearchOperation operation=ServiceSearchOperation.intersection;

        try {
            System.out.println("Publish and Discover service with "+numSrv+" tags, "+numReq+" tags in request...");

            ServiceInfoImpl info= (ServiceInfoImpl) ServiceInfo.create(type,"NodeB",7070+numSrv,"");
            ServiceInfoImpl info1= (ServiceInfoImpl) ServiceInfo.create(type,"NodeB2",7071+numSrv,"");

            List<String> tags=new ArrayList<String>(){{add("tag_1=100"); add("tag_2"); }};
//            List<String> tags=getTagNamesFixSize(numSrv, 0);
//            info.addTags(tags);

            info.addTags(tags.subList(0,1));
            info1.addTags(tags);

            ServicePublisher publisher=new ServicePublisher(scheme);
            publisher.publishService(info);
            publisher.publishServiceLazy(info1);

            ServiceDiscoverer discoverer=new ServiceDiscoverer(scheme);
            discoverer.searchForService(tags.subList(0,numReq),operation,"lgt","udp","local",new DNSRecordListener() {
                @Override
                public void recordUpdated(DNSEntry record, List<String> context) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args){
//        experimentPublish(3);
//        experimentPublish(5);
//        experimentPublish(10);
//        experimentPublish(15);
//        experimentPublish(20);
//        experimentPublish(25);

        experimentDiscover(4,2);
//        experimentDiscover(5,5);
    }
}
