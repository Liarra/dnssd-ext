package tue.dnssd.context.tag.publish;

import tue.dnssd.jmdns.impl.DNSEntry;

import java.util.List;

/**
 * A listener to subscribe for DNS record searches results or updates.
 */
public interface DNSRecordListener {
    public void recordUpdated(DNSEntry record, List<String> PTRPointerName);
}
