package tue.dnssd.context.tag.gui;

import tue.dnssd.context.tag.naming.NamingScheme;
import tue.dnssd.context.tag.publish.ServicePublisher;
import tue.dnssd.jmdns.ServiceInfo;
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
public class PublishForm {
    private List<String> tags=new ArrayList<String>();

    private JTextField serviceTypeTextField;
    private JTextField textField2;
    private JButton publishButton;
    private JPanel tagContainer;
    private JPanel rootPanel;
    private JTextField serviceNameTextField;
    private JTextField servicePortTextField;
    private JComboBox serviceProtocolComboBox;

    private NamingScheme namingScheme = ExperimentConfig.namingScheme;

    private JmDNSImpl jmDNS;

    private KeyAdapter addTagOnEnterAdapter = new addTagOnAdapter(textField2, tagContainer, tags);
    private ActionListener publishListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            String serviceName = serviceNameTextField.getText();
            String jmDNSTypeString=getjmDNSTypeString();
            int servicePort = Integer.decode(servicePortTextField.getText());

            ServiceInfo service = ServiceInfo.create(jmDNSTypeString, serviceName, servicePort, 0, 0, new byte[1]);
            service.addTags(tags);

            try {
                ServicePublisher publisher = new ServicePublisher(namingScheme,jmDNS);
                publisher.publishService(service);
                publishButton.setEnabled(false);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    };

    private String getjmDNSTypeString(){
        String  serviceType = serviceTypeTextField.getText(),
                serviceProtocol=serviceProtocolComboBox.getSelectedItem().toString();

        String jmDNSTypeString="_"+serviceType+"._"+serviceProtocol+".local.";
        return  jmDNSTypeString;
    }

    public PublishForm() {
        textField2.addKeyListener(addTagOnEnterAdapter);
        publishButton.addActionListener(publishListener);

        try {
            jmDNS=new JmDNSImpl(null,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Main.enableLogging();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("PublishForm");
        frame.setContentPane(new PublishForm().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
