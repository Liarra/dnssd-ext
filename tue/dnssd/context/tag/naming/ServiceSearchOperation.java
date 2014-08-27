package tue.dnssd.context.tag.naming;

/**
 * Created by nina on 5/12/14.
 * This enum describes types of operations on tags that can be used during service discovery.
 *
 * For example, the "{@link #union}" ServiceSearchOperation means that for a set of tags, the service that has ANY of these tags
 * is discovered.
 */
public enum ServiceSearchOperation {

    /**
     * For a set of tags in the request, the service that has ANY of these tags is discovered.
     */
    union,

    /**
     * For a set of tags in the request. the service that has ALL of these tags is discovered.
     */
    intersection
}
