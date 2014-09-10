package org.jsoup.examples;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ASDOnline {

	public static void main(String[] args) throws IOException,
			InterruptedException {

		FileWriter writer = new FileWriter("/TOC/supplier_tradeshow3.csv");
		String[] headers = { "Vendor name", "Booth No.", "Categories" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		String exhibitorUrl = "http://n1b.goexposoftware.com/events/asda14/goExpo/public/listExhibitorsFrame.php";
		Document Showdoc = Jsoup.connect(exhibitorUrl).get();
		Elements links = Showdoc.select("a[href*=id]");
		int i = 0;
		for (Element link : links) {
			i++;

			// Vendor Name
			String vendorName = StringUtils.replace(link.text(), ",", " ");
			writer.append(vendorName);
			writer.append(",");

			// Link to URL
			String linkUrl = link.attr("href");
			Document doc = Jsoup.connect(linkUrl).get();

			// Booth No:
			Elements boothNo = doc.select("a[href*=ei]");
			for (Element booth : boothNo) {
				String boothNumber = StringUtils
						.replace(booth.text(), ",", " ");
				writer.append(boothNumber);
				writer.append(",");
			}

			if (i % 200 == 0) {
				Thread.sleep(30000);
			}

			// Category
			String categoryList = " ";
			Elements categories = doc.select("a[href*=category]");
			for (Element category : categories) {
				categoryList += category.text() + " ";
			}
			writer.append(categoryList);
			writer.append(",");

			writer.append('\n');
		}
		writer.flush();
		writer.close();
	}
}
