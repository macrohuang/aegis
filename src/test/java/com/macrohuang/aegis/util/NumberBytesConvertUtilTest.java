package com.macrohuang.aegis.util;

import org.junit.Assert;
import org.junit.Test;

import com.macrohuang.aegis.util.NumberBytesConvertUtil;


public class NumberBytesConvertUtilTest {
	private static final int INT_TEST = 0x01234567;
	private static final byte[] BYTES_INT = new byte[]{1,35,69,103};
	private static final long LONG_TEST = 0x01234567890a0b0cL;
	private static final byte[] BYTES_LONG = new byte[]{1,35,69,103,(byte) 137,10,11,12};
	@Test
	public void testConvertInt2Bytes(){
		Assert.assertArrayEquals(BYTES_INT, NumberBytesConvertUtil.int2ByteArr(INT_TEST));
	}
	
	@Test
	public void testConvertBytes2Int(){
		Assert.assertEquals(INT_TEST, NumberBytesConvertUtil.byteArr2Int(BYTES_INT));
	}
	
	@Test
	public void testConvertLong2Bytes(){
		Assert.assertArrayEquals(BYTES_LONG, NumberBytesConvertUtil.long2ByteArr(LONG_TEST));
	}
	
	@Test
	public void testConvertBytes2Long(){
		Assert.assertEquals(LONG_TEST, NumberBytesConvertUtil.byteArr2Long(BYTES_LONG));
	}

    @Test(expected = IllegalArgumentException.class)
    public void testConvertByte2LongException() {
        NumberBytesConvertUtil.byteArr2Long(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertByte2LongException2() {
        NumberBytesConvertUtil.byteArr2Long(new byte[] {});
    }
    @Test(expected = IllegalArgumentException.class)
    public void testConvertByte2LongException3() {
        NumberBytesConvertUtil.byteArr2Long(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertByte2IntException() {
        NumberBytesConvertUtil.byteArr2Int(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertByte2IntException2() {
        NumberBytesConvertUtil.byteArr2Int(new byte[] {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertByte2IntException3() {
        NumberBytesConvertUtil.byteArr2Int(new byte[] { 1, 2, 3, 4, 5 });
    }

    @Test
    public void testNew() {
        NumberBytesConvertUtil util = new NumberBytesConvertUtil();
        Assert.assertNotNull(util);
    }
}
