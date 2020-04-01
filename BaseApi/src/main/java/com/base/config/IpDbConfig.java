package com.base.config;

import com.base.common.exception.SoException;

/**
 * ip database configuration class
 * @author zhangwei
 * @date 2020/4/1 13:20
 */
public class IpDbConfig {

    /**
     * total header data block size
     */
    private int totalHeaderSize;

    /**
     * max index data block size
     * u should always choice the fastest read block size
     */
    private int indexBlockSize;

    /**
     * construct method
     *
     * @param    totalHeaderSize
     * @throws SoException
     */
    public IpDbConfig(int totalHeaderSize ) throws SoException
    {
        if ( (totalHeaderSize % 8) != 0 ) {
            throw new SoException("totalHeaderSize must be times of 8");
        }

        this.totalHeaderSize = totalHeaderSize;
        this.indexBlockSize  = 8192; //4 * 2048
    }

    public IpDbConfig() throws SoException
    {
        this(8 * 2048);
    }

    public int getTotalHeaderSize()
    {
        return totalHeaderSize;
    }

    public IpDbConfig setTotalHeaderSize(int totalHeaderSize)
    {
        this.totalHeaderSize = totalHeaderSize;
        return this;
    }

    public int getIndexBlockSize()
    {
        return indexBlockSize;
    }

    public IpDbConfig setIndexBlockSize(int dataBlockSize)
    {
        this.indexBlockSize = dataBlockSize;
        return this;
    }
}
