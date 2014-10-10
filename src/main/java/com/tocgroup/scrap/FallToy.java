package com.tocgroup.scrap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.tocgroup.Jsoup;

import com.tocgroup.nodes.Document;
import com.tocgroup.nodes.Element;
import com.tocgroup.select.Elements;

public class FallToy implements Scraper {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		File extract = new FallToy().extract();
		System.out.println(extract.getAbsolutePath());
	}

	public File extract() throws IOException {
		long startTime = System.currentTimeMillis();
		File file = File.createTempFile(FallToy.class.getName(), "csv");
		// FileWriter writer = new FileWriter("/TOC/FallToy.csv");
		FileWriter writer = new FileWriter(file);
		String[] headers = { "Show", "Show Date", "Vendor name", "Website",
				"Location" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');
		String showUrl = "http://10times.com/fall-toy-preview/exhibitors";
		Document showDoc = Jsoup.connect(showUrl).get();
		String showDateValue = "";
		Elements showDateDetail = showDoc.getElementsByAttributeValue(
				"itemprop", "startDate");
		for (Element showDate : showDateDetail) {
			showDateValue = showDate.text();
		}

		for (int i = 1; i < 6; i++) {
			String showUrl2 = "http://10times.com/fall-toy-preview/exhibitors?&ajax=1&page="
					+ i;
			Document showDoc2 = Jsoup.connect(showUrl2).get();
			Elements vendorDetail = showDoc2.getElementsByAttributeValue(
					"class", "10u name");

			for (Element vendorName : vendorDetail) {
				Elements link = vendorName.getElementsByTag("a");
				String linkUrl = "";
				for (Element linkValue : link) {
					linkUrl = linkValue.attr("href");
				}

				Elements locations = vendorName.getElementsByTag("p");
				String locationName = "";
				for (Element location : locations) {
					locationName = location.text();
				}

				// Show
				writer.append("Fall Toy Preview");
				writer.append(",");

				// Show Date
				writer.append(showDateValue);
				writer.append(",");

				// Vendor name
				String vendor = StringUtils
						.replace(vendorName.text(), ",", " ");
				writer.append(vendor);
				writer.append(",");

				// Website
				writer.append(linkUrl);
				writer.append(",");

				// Location
				writer.append(locationName);
				writer.append(",");
				writer.append("\n");
			}
		}

		writer.flush();
		writer.close();
		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to prepare csv "
				+ (endTime - startTime) / 1000 + " Seconds ");
		return file;
	}

}
