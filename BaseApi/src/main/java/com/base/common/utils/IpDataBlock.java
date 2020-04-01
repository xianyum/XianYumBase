package com.base.common.utils;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2020/4/1 13:27
 */
@Data
public class IpDataBlock {
    /**
     * city id
     */
    private int city_id;

    /**
     * region address
     */
    private String region;

    /**
     * region ptr in the db file
     */
    private int dataPtr;

    public IpDataBlock( int city_id, String region, int dataPtr )
    {
        this.city_id = city_id;
        this.region  = region;
        this.dataPtr = dataPtr;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(city_id).append('|').append(region).append('|').append(dataPtr);
        return sb.toString();
    }
}
