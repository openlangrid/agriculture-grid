package org.pangaea.agrigrid.service.agriculture.image.util;

import java.io.FileOutputStream;

import jp.go.nict.langrid.commons.io.StreamUtil;
import junit.framework.TestCase;

public class ImageUtilTest extends TestCase{
	public void test() throws Exception{
		FileOutputStream fo = new FileOutputStream("resized.png");
		try{
			StreamUtil.transfer(
					ImageUtil.resize(
							getClass().getResourceAsStream("infrastructure_ja.gif")
							, 512, "PNG")
					, fo);
		} finally{
			fo.close();
		}
	}
}
