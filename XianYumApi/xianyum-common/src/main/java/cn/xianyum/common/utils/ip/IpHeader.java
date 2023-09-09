// Copyright 2022 The Ip2Region Authors. All rights reserved.
// Use of this source code is governed by a Apache2.0-style
// license that can be found in the LICENSE file.
// @Author Lion <chenxin619315@gmail.com>
// @Date   2022/06/23

package cn.xianyum.common.utils.ip;

public class IpHeader {
    public final int version;
    public final int indexPolicy;
    public final int createdAt;
    public final int startIndexPtr;
    public final int endIndexPtr;
    public final byte[] buffer;

    public IpHeader(byte[] buff) {
        assert buff.length >= 16;
        version = IpSearcher.getInt2(buff, 0);
        indexPolicy = IpSearcher.getInt2(buff, 2);
        createdAt = IpSearcher.getInt(buff, 4);
        startIndexPtr = IpSearcher.getInt(buff, 8);
        endIndexPtr = IpSearcher.getInt(buff, 12);
        buffer = buff;
    }

    @Override public String toString() {
        return "{" +
            "Version: " + version + ',' +
            "IndexPolicy: " + indexPolicy + ',' +
            "CreatedAt: " + createdAt + ',' +
            "StartIndexPtr: " + startIndexPtr + ',' +
            "EndIndexPtr: " + endIndexPtr +
        '}';
    }
}
