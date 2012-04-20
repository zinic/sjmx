package net.jps.jx;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import net.jps.jx.mapping.DefaultObjectConstructor;
import net.jps.jx.mapping.reflection.DefaultClassMapper;

/**
 *
 * @author zinic
 */
public class Controls {

    private static final Controls INSTANCE = new Controls();
    
    public static JxControls getJxControls() {
        return INSTANCE.jxControls;
    }
    
    private final JxControls jxControls;

    private Controls() {
        try {
            final DatatypeFactory dtf = DatatypeFactory.newInstance();
            jxControls = new JxControlsImpl(new DefaultObjectConstructor(dtf), DefaultClassMapper.getInstance());
        } catch (DatatypeConfigurationException dce) {
            throw new RuntimeException(dce.getMessage(), dce);
        }
    }
}
