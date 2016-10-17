package test;

import jp.go.nict.langrid.commons.io.StreamUtil;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

public class Base64Test {
	@Test
	public void size() throws Exception{
		byte[] base64 = Base64.encodeBase64(StreamUtil.readAsBytes(getClass().getResourceAsStream("thumbnail.jpg")));
		String base64String = new String(base64);
		System.out.println(base64String);
		System.out.println(base64String.length());
	}

	@Test
	public void encode_decode() throws Exception{
		byte[] original = StreamUtil.readAsBytes(getClass().getResourceAsStream("thumbnail.jpg"));
		byte[] encoded = Base64.encodeBase64(original);
		String encodedString = new String(encoded);
		byte[] decoded = Base64.decodeBase64(encodedString.getBytes());
		Assert.assertArrayEquals(original, decoded);
	}
}
