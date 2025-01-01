package com.ws.rpc.core.enums;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 22:28
 */
public class CompressTypeTest {
    @Test
    public void testCompressType() {
        CompressType nonE = CompressType.fromString("NonE");
        assertEquals(CompressType.NONE, nonE);
        System.out.println(nonE.getType());
    }

}