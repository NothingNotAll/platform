package nna.base.protocol.xml;

import nna.base.protocol.dispatch.AbstractDispatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-05-30 15:03
 **/

public class Xml extends AbstractDispatch<InputStream, OutputStream> {

    public OutputStream getOutPutStream(OutputStream response) throws IOException {
        return null;
    }

    public Map<String, String[]> getReqColMap(InputStream request) {
        return null;
    }

    public String[] getPlatformEntryId(InputStream request) {
        return new String[0];
    }

}
