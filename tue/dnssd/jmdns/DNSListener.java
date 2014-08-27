// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package tue.dnssd.jmdns;

// REMIND: Listener should follow Java idiom for listener or have a different
// name.

import tue.dnssd.jmdns.impl.DNSCache;
import tue.dnssd.jmdns.impl.DNSEntry;

/**
 * DNSListener. Listener for record updates.
 * 
 * @author Werner Randelshofer, Rick Blair
 * @version 1.0 May 22, 2004 Created.
 */
public interface DNSListener {
    /**
     * Update a DNS record.
     * 
     * @param dnsCache
     *            record cache
     * @param now
     *            update date
     * @param record
     *            DNS record
     */
    void updateRecord(DNSCache dnsCache, long now, DNSEntry record);
}
