package com.tocgroup.scrap;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.tocgroup.Jsoup;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.tocgroup.nodes.Document;
import com.tocgroup.nodes.Element;
import com.tocgroup.select.Elements;

public class ASDOnline {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		long startTime = System.currentTimeMillis();
		FileWriter writer = new FileWriter("/TOC/supplier_ASDOnline.csv");
		String[] headers = { "Show", "Show Date", "Vendor name", "Booth No.",
				"Website", "Address", "Phone", "Email", "Product Categories" };
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
			writer.append("LAS VEGAS");
			writer.append(",");

			writer.append("August 3-6,2014");
			writer.append(",");

			// Vendor Name
			String vendorName = StringUtils.replace(link.text(), ",", " ");
			writer.append(vendorName);
			writer.append(",");

			// Link to URL
			String linkUrl = link.attr("href");
			System.out.println(linkUrl);

			final WebClient webClient = new WebClient();
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			final HtmlPage page = webClient.getPage(linkUrl);

			// Booth No.
			int m = 1;
			boolean flag = false;
			while (m < 7) {
				List<?> boothNoList = page
						.getByXPath("/html/body/div[2]/div[1]/div[4]/div[2]/table/tbody/tr/td[1]/table[1]/tbody/tr/td[1]/div[2]/div[2]/a");
				Iterator<?> boothNoDetail = boothNoList.iterator();
				if (!boothNoDetail.hasNext()) {
					Thread.sleep(1000);
					m++;
				}
				while (boothNoDetail.hasNext()) {
					HtmlElement boothNo = (HtmlElement) boothNoDetail.next();
					String boothNumber = StringUtils.replace(boothNo.asText(),
							",", " ");
					writer.append(boothNumber);
					writer.append(",");
					flag = true;
				}
				if (flag) {
					break;
				}
			}

			// Address
			String websiteValue = "";
			String addresDetail = "";
			List<?> addressList = page
					.getByXPath("/html/body/div[2]/div[1]/div[4]/div[2]/table/tbody/tr/td[1]/table[1]/tbody/tr/td[2]/table");
			Iterator<?> addressDetail = addressList.iterator();
			while (addressDetail.hasNext()) {
				HtmlElement address = (HtmlElement) addressDetail.next();
				String addressValue = StringUtils.replace(address.asText(),
						",", " ");
				addressValue = StringUtils.remove(addressValue, "\r");
				addressValue = StringUtils.remove(addressValue, "Website: 	");
				addressValue = StringUtils.remove(addressValue, "\n");
				String[] split = addressValue.split("Address: 	");
				for (int l = 0; l < split.length; l++) {
					String addressInfo = StringUtils
							.replace(split[l], ",", " ");
					if (addressInfo.contains("www")) {
						websiteValue = addressInfo;
						System.out.println(websiteValue);
					} else {
						addresDetail = StringUtils.replace(addressInfo, "\n",
								" ");
						System.out.println(addresDetail);
					}
				}

			}
			// Website
			writer.append(websiteValue);
			writer.append(",");

			// Address
			writer.append(addresDetail);
			writer.append(",");

			String emailValue = "";
			String phoneValue = "";
			List<?> contactList = page
					.getByXPath("/html/body/div[2]/div[1]/div[4]/div[2]/table/tbody/tr/td[2]/table/tbody/tr/td");
			Iterator<?> contactDetail = contactList.iterator();
			while (contactDetail.hasNext()) {
				HtmlElement next = (HtmlElement) contactDetail.next();
				String contact = next.asText();
				contact = StringUtils.remove(contact, "\r");
				String[] split = contact.split("\n");
				for (int l = 0; l < split.length; l++) {
					String contactValue = StringUtils.replace(split[l], ",",
							" ");
					if (contactValue.contains("@")) {
						emailValue = contactValue;
					} else {
						phoneValue = contactValue;
					}
				}
			}

			// Phone
			writer.append(phoneValue);
			writer.append(",");

			// Email
			writer.append(emailValue);
			writer.append(",");

			// Category
			int count = 0;
			List<?> categoryList = page
					.getByXPath("/html/body/div[2]/div[1]/div[4]/div[2]/table/tbody/tr/td[1]/table[3]/tbody/tr/td[2]/div/div[5]");
			Iterator<?> categoryDetail = categoryList.iterator();
			while (categoryDetail.hasNext()) {
				count++;
				HtmlElement next = (HtmlElement) categoryDetail.next();
				String categoryDetails = "\"";
				String category = next.asText();
				category = StringUtils.remove(category, "\r");
				String categoryValue = StringUtils.replace(category, ",", " ");
				categoryValue = StringUtils.remove(categoryValue,
						"Product Categories:\n");
				categoryDetails += categoryValue + "\"";
				writer.append(categoryDetails);
			}

			// Category
			if (count == 0) {
				writer.append("");
				writer.append(",");
			}

			System.out.println("count" + i);
			writer.append("\n");
		}
		writer.flush();
		writer.close();
		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to prepare csv "
				+ (endTime - startTime) / 1000 + " Seconds ");
	}
}
