package tue.dnssd.context.tag.publish;

import tue.dnssd.jmdns.impl.DNSMessage;

import java.io.IOException;

/**
 * Created by nina on 8/11/14.
 */
public interface DNSMessageListener {
    public void messageArrived(DNSMessage m) throws IOException;
}
