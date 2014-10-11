package tue.dnssd.context.tag.gui;

import tue.dnssd.context.tag.naming.NamingScheme;
import tue.dnssd.context.tag.naming.TagToPointerNamingScheme;

/**
 * Created by nina on 5/13/14.
 */
public class ExperimentConfig {
    public static NamingScheme namingScheme = new TagToPointerNamingScheme();
//    public static NamingScheme namingScheme = new SortedTagsConcatNamingScheme();
//    public static NamingScheme namingScheme = new FormulaNamingScheme();
}
