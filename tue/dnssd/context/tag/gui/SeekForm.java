package tue.dnssd.context.tag.gui;

import tue.dnssd.context.tag.naming.TagToPointerNamingScheme;
import tue.dnssd.context.tag.publish.DNSRecordListener;
import tue.dnssd.context.tag.naming.NamingScheme;
import tue.dnssd.context.tag.naming.ServiceSearchOperation;
import tue.dnssd.context.tag.publish.ServiceDiscoverer;

import tue.dnssd.jmdns.impl.DNSEntry;
import tue.dnssd.jmdns.impl.JmDNSImpl;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nina on 5/6/14.
 */
public class SeekForm {
    private JTextField typeTextField;
    private JTextField textField2;
    private JComboBox comboBox1;
    private JList<String> list1;
    private DefaultListModel<String> listModel;
    private JPanel rootPanel;
    private JButton seekButton;
    private JPanel tagContainer;
    private JComboBox serviceProtocolComboBox;
    private List<String> tags= new ArrayList<String>();

    private NamingScheme namingScheme = ExperimentConfig.namingScheme;
    private ServiceSearchOperation operation;

    private JmDNSImpl jmDNS;
    private ServiceDiscoverer serviceDiscoverer;

    private DNSRecordListener somethingFoundListener = new DNSRecordListener() {

        @Override
        public void recordUpdated(DNSEntry record, List<String> context) {
            String foundName = record.toString();
            if (foundName.length() > 0) {
                foundName=breakString(foundName,48);
                if(!listModel.contains(foundName)) {
                    listModel.addElement(foundName);
                    list1.revalidate();
                }
            }
        }
    };

    private DNSRecordListener somethingFoundT2PListener = new DNSRecordListener() {

        @Override
        public void recordUpdated(DNSEntry record, List<String> context) {
            if(operation==ServiceSearchOperation.intersection) {
                boolean b = true;

                for (String s : tags) {
                    if (!context.contains(s + "._" + stype + "._" + protocol + ".local.")) {
                        b = false;
                        break;
                    }
                }

                if (b == false)
                    return;

            }
            String foundName = record.toString();
            if (foundName.length() > 0) {
                foundName=breakString(foundName,48);
                if(!listModel.contains(foundName)) {
                    listModel.addElement(foundName);
                    list1.revalidate();
                }
            }
        }
    };

    private String breakString(String str, int length){
        int numberOfInsertPoints=str.length()/length;
        String[] strings=new String[numberOfInsertPoints+1];
        for(int i=0;i<numberOfInsertPoints;i++){
            strings[i]=str.substring(length*i,length*(i+1));
        }
        strings[numberOfInsertPoints]=str.substring(length*numberOfInsertPoints);

        String newstring="";
        for (String string : strings) {
            newstring += string + "<br/>";
        }

        newstring="<html>"+newstring+"</html>";
        return newstring;

    }

    private KeyAdapter addTagOnEnterAdapter = new addTagOnAdapter(textField2, tagContainer, tags);

    String stype="",protocol="";

    private ActionListener buttonHit = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            DNSRecordListener listener=(namingScheme instanceof TagToPointerNamingScheme)?somethingFoundT2PListener:somethingFoundListener;
            try {
                listModel.clear();
                operation=ServiceSearchOperation.values()[comboBox1.getSelectedIndex()];
                serviceDiscoverer.searchForService(
                        tags,
                        operation,
                        stype=typeTextField.getText(),protocol=serviceProtocolComboBox.getSelectedItem().toString(), "local",
                        listener);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public SeekForm() {
        listModel = new DefaultListModel<String>();
        list1.setModel(listModel);

        textField2.addKeyListener(addTagOnEnterAdapter);
        seekButton.addActionListener(buttonHit);

        try {
            jmDNS=new JmDNSImpl(null,null);
            serviceDiscoverer =new ServiceDiscoverer(namingScheme,jmDNS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("DiscoverForm");
        frame.setContentPane(new SeekForm().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
