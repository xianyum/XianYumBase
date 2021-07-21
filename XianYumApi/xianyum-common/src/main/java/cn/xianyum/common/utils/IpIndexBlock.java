package cn.xianyum.common.utils;

/**
 * @author zhangwei
 * @date 2020/4/1 13:25
 */
public class IpIndexBlock {
    private static int LENGTH = 12;

    /**
     * start ip address
     */
    private long startIp;

    /**
     * end ip address
     */
    private long endIp;

    /**
     * data ptr and data length
     */
    private int dataPtr;

    /**
     * data length
     */
    private int dataLen;

    public IpIndexBlock(long startIp, long endIp, int dataPtr, int dataLen)
    {
        this.startIp = startIp;
        this.endIp = endIp;
        this.dataPtr = dataPtr;
        this.dataLen = dataLen;
    }

    public long getStartIp()
    {
        return startIp;
    }

    public IpIndexBlock setStartIp(long startIp)
    {
        this.startIp = startIp;
        return this;
    }

    public long getEndIp()
    {
        return endIp;
    }

    public IpIndexBlock setEndIp(long endIp)
    {
        this.endIp = endIp;
        return this;
    }

    public int getDataPtr()
    {
        return dataPtr;
    }

    public IpIndexBlock setDataPtr(int dataPtr)
    {
        this.dataPtr = dataPtr;
        return this;
    }

    public int getDataLen()
    {
        return dataLen;
    }

    public IpIndexBlock setDataLen(int dataLen)
    {
        this.dataLen = dataLen;
        return this;
    }

    public static int getIndexBlockLength()
    {
        return LENGTH;
    }

    /**
     * get the bytes for storage
     *
     * @return    byte[]
     */
    public byte[] getBytes()
    {
        /*
         * +------------+-----------+-----------+
         * | 4bytes        | 4bytes    | 4bytes    |
         * +------------+-----------+-----------+
         *  start ip      end ip      data ptr + len
         */
        byte[] b = new byte[12];

        IPUtils.writeIntLong(b, 0, startIp);    //start ip
        IPUtils.writeIntLong(b, 4, endIp);        //end ip

        //write the data ptr and the length
        long mix = dataPtr | ((dataLen << 24) & 0xFF000000L);
        IPUtils.writeIntLong(b, 8, mix);

        return b;
    }
}
