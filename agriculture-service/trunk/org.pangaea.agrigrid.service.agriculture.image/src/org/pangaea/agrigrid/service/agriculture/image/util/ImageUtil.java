package org.pangaea.agrigrid.service.agriculture.image.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageUtil {
	public static InputStream resize(InputStream imageBody, int maxLength, String outputFormat)
	throws IOException{
		BufferedImage image = ImageIO.read(imageBody);
		int w = image.getWidth();
		int h = image.getHeight();
		double scale = 1.0 * maxLength / ((w > h) ? w : h);
		w *= scale;
		h *= scale;
	    BufferedImage shrinkImage = new BufferedImage(w, h, image.getType());
	    Graphics2D g2d = shrinkImage.createGraphics();
	    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
	    RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	    RenderingHints.VALUE_ANTIALIAS_ON);
	    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
	    RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	    g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
	    RenderingHints.VALUE_DITHER_ENABLE);
	    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
	    RenderingHints.VALUE_RENDER_QUALITY);
	    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
	    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
	    RenderingHints.VALUE_STROKE_NORMALIZE);
	    g2d.drawImage(image, 0, 0, w, h, null);
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ImageIO.write(shrinkImage, outputFormat, out);
	    return new ByteArrayInputStream(out.toByteArray());
	}
}
