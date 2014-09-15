package org.jsoup.examples;

import java.io.PrintWriter;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class MyTextExtractionStrategy extends SimpleTextExtractionStrategy {

	/** The print writer to which the information will be written. */
	protected PrintWriter out;

	/**
	 * Creates a RenderListener that will look for text.
	 */
	public MyTextExtractionStrategy(PrintWriter out) {
		this.out = out;
	}

	/**
	 * @see com.itextpdf.text.pdf.parser.RenderListener#beginTextBlock()
	 */
	public void beginTextBlock() {
		out.print("<");
	}

	/**
	 * @see com.itextpdf.text.pdf.parser.RenderListener#endTextBlock()
	 */
	public void endTextBlock() {
		out.println(">");
	}

	/**
	 * @see com.itextpdf.text.pdf.parser.RenderListener#renderImage(com.itextpdf.text.pdf.parser.ImageRenderInfo)
	 */
	public void renderImage(ImageRenderInfo renderInfo) {
	}

	/**
	 * @see com.itextpdf.text.pdf.parser.RenderListener#renderText(com.itextpdf.text.pdf.parser.TextRenderInfo)
	 */
	public void renderText(TextRenderInfo renderInfo) {
		out.print("<");
		out.print(renderInfo.getText());
		out.print(">");
	}
}