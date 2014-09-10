package org.jsoup.examples;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Example program to list links from a URL.
 */
public class Expotec {
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		FileWriter writer = new FileWriter("/TOC/supplier_tradeshow.csv");
		String[] headers = { "Show", "Show Dates", "Vendor name", "Booth No",
				"Address", "Phone", "Website", "Email", "Description" };
		String exhibitorUrl = "http://events.expotec.us/sgs/exhibitors";
		String showUrl = "http://events.expotec.us/sgs/#show";
		print("Fetching %s...", exhibitorUrl);

		// show information

		Document Showdoc = Jsoup.connect(showUrl).get();
		Elements showLinks = Showdoc.getElementsByTag("p");
		boolean oneMoreParagraph = false;
		String showLocation = "";
		String showHours = "";
		for (Element showLink : showLinks) {
			Elements elementsContainingText = null;
			if (oneMoreParagraph) {
				showHours = StringUtils.remove(
						StringUtils.replace(showLink.text(), ",", " "),
						"TEMPORARY EXHIBITS OPEN:");
				oneMoreParagraph = false;

			} else {
				elementsContainingText = showLink
						.getElementsContainingText("SHOW HOURS & LOCATION:");
				if (elementsContainingText.size() > 0) {
					oneMoreParagraph = true;
					showLocation = StringUtils.remove(StringUtils.replace(
							elementsContainingText.text(), ",", " "),
							"SHOW HOURS & LOCATION:");
					;

				}
			}

		}
		System.out.println("show location : " + showLocation + "show hours: "
				+ showHours);
		int i = 0;
		Document doc = Jsoup.connect(exhibitorUrl).get();
		Elements links = doc.select("a[href*=exhibitor]");
		print("\nLinks: (%d)", links.size());
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		for (Element link : links) {
			writer.append(showLocation);
			writer.append(',');
			writer.append(showHours);
			writer.append(',');

			Document document = Jsoup.connect(link.attr("abs:href")).get();//

			Elements vendorElement = document.getElementsByAttributeValue(
					"style",
					"padding:0px 0px 0 63px; color:#0093c9; font-size:16px;");
			String vendorExhibitionName = document
					.getElementsByClass("exhibitors").get(0).text();
			if (vendorElement.size() > 0) {
				String vendorName = StringUtils.replace(vendorElement.get(0)
						.text(), ",", " ");
				// System.out.println(vendorName);
				writer.append(StringUtils.replace(vendorExhibitionName, ",",
						" ") + " --> " + vendorName);
				writer.append(',');
			} else {
				writer.append(vendorExhibitionName);
				writer.append(',');
			}

			Elements boothElement = document.getElementsByClass("booth_num");
			if (boothElement.size() > 0) {
				writer.append(boothElement.get(0).text());
				writer.append(',');
			} else {
				writer.append(" ");
				writer.append(',');
			}

			Elements exhibitorDetails = document.getElementsByClass("exh_left");
			for (Element exhibitorDetail : exhibitorDetails) {

				String text = exhibitorDetail.text();
				System.out.println("exhibition details: " + text);
				String[] split = StringUtils.split(text, ':');
				for (String str : split) {
					str = StringUtils.replace(str, ",", " ");
					str = StringUtils.remove(str, "Address");
					str = StringUtils.remove(str, "Phone");
					str = StringUtils.remove(str, "Email");
					str = StringUtils.remove(str, "Website");
					str = StringUtils.remove(str, "Description");
					if (StringUtils.isNotBlank(str)) {
						writer.append(str);
						writer.append(',');
					}
				}
			}

			print(" * a: <%s>  (%s)", link.attr("abs:href"),
					trim(link.text(), 35));
			writer.append('\n');
			i++;
			if (i == -1)
				break;
		}
		writer.flush();
		writer.close();

		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to prepare csv "
				+ (endTime - startTime) / 1000 + " Seconds ");
	}

	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}
}
