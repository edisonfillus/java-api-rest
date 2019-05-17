package org.project.example.util;

import org.junit.Test;

public class XmlFormatterTest {
    @Test
    public void testFetchAll() {

    	String formatted = XmlFormatter.prettyFormat("<?xml version=\"1.0\" encoding=\"UTF-8\"?><auditedTable><PNLTB038_TOTEM operation=\"UPDATE\" schema=\"pnl\"><CO_TOTEM>45</CO_TOTEM><NU_VERSAO/><NO_TOTEM>PR0650TO703</NO_TOTEM><IC_APPLET>false</IC_APPLET><NO_DNS>10.96.20.108</NO_DNS><CO_UNIDADE>7445</CO_UNIDADE><NU_IP>10.96.20.108</NU_IP></PNLTB038_TOTEM><PNLTB038_TOTEM operation=\"UPDATE\" schema=\"pnl\"><CO_TOTEM>47</CO_TOTEM><NU_VERSAO/><NO_TOTEM>PR0650TO701</NO_TOTEM><IC_APPLET>false</IC_APPLET><NO_DNS>10.105.240.86</NO_DNS><CO_UNIDADE>7445</CO_UNIDADE><NU_IP>10.105.240.86</NU_IP></PNLTB038_TOTEM><PNLTB038_TOTEM operation=\"UPDATE\" schema=\"pnl\"><CO_TOTEM>48</CO_TOTEM><NU_VERSAO/><NO_TOTEM>PR0650TO702</NO_TOTEM><IC_APPLET>false</IC_APPLET><NO_DNS>10.105.240.130</NO_DNS><CO_UNIDADE>7445</CO_UNIDADE><NU_IP>10.105.240.130</NU_IP></PNLTB038_TOTEM></auditedTable>");
    	System.out.println(formatted);
    
    }
}
 