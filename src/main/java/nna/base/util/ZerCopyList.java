package nna.base.util;

import java.util.LinkedList;

/**
 * Created by NNA-SHUAI on 2017/7/8.
 */
public class ZerCopyList {
    private LinkedList<byte[]> segments=new LinkedList<byte[]>();
    private Integer segmentSize=1024;//1024 byte
    private Integer currentIndex;
    private Integer currentSegmentIndex;
}
