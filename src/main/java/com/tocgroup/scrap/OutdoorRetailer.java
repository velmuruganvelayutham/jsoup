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

public class OutdoorRetailer {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		long startTime = System.currentTimeMillis();
		FileWriter writer = new FileWriter("/TOC/supplier_OutDoorRetailer.csv");
		String[] headers = { "Show", "Show Date", "Vendor name", "Booth No.",
				"Website", "Address", "Email", "Phone" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		String exhibitorUrl = "http://n2b.goexposoftware.com/events/ors2014/goExpo/public/listExhibitorsFrame.php";
		Document Showdoc = Jsoup.connect(exhibitorUrl).get();
		Elements links = Showdoc.select("a[href*=id]");
		int i = 0;
		for (Element link : links) {
			i++;
			writer.append("OUTDOOR RETAILER");
			writer.append(",");

			writer.append("August 6-9 2014");
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
			String boothNumber = "";
			while (m < 7) {
				List<?> boothNoList = page
						.getByXPath("/html/body/div[2]/div[1]/div[4]/div[4]/table/tbody/tr/td[1]/table[1]/tbody/tr/td[1]/div[2]/div[2]/a");
				Iterator<?> boothNoDetail = boothNoList.iterator();
				if (!boothNoDetail.hasNext()) {
					Thread.sleep(1000);
					m++;
				}
				while (boothNoDetail.hasNext()) {
					HtmlElement boothNo = (HtmlElement) boothNoDetail.next();
					boothNumber = StringUtils.replace(boothNo.asText(), ",",
							" ");
					flag = true;
				}
				if (flag) {
					break;
				}
			}

			writer.append(boothNumber);
			writer.append(",");

			// Address
			String websiteValue = "";
			String addresDetail = "";
			String emailValue = "";
			List<?> addressList = page
					.getByXPath("/html/body/div[2]/div[1]/div[4]/div[4]/table/tbody/tr/td[1]/table[1]/tbody/tr/td[2]/table");
			Iterator<?> addressDetail = addressList.iterator();
			while (addressDetail.hasNext()) {
				HtmlElement address = (HtmlElement) addressDetail.next();
				String addressValue = StringUtils.replace(address.asText(),
						",", " ");
				addressValue = StringUtils.remove(addressValue, "\r");
				addressValue = StringUtils.replace(addressValue, "Website: 	",
						"\t");
				addressValue = StringUtils.replace(addressValue, "Email: 	",
						"\t");
				addressValue = StringUtils.replace(addressValue, "Address: 	",
						"\t");
				addressValue = StringUtils.remove(addressValue, "\n");
				String[] split = addressValue.split("\t");
				for (int l = 1; l < split.length - 1; l++) {
					String addressInfo = StringUtils
							.replace(split[l], ",", " ");
					if (addressInfo.contains("www")) {
						websiteValue = addressInfo;
						System.out.println(websiteValue);
					} else if (addressInfo.contains("@")) {
						emailValue = StringUtils.replace(addressInfo, ",", " ");
						System.out.println(emailValue);
					} else {
						addresDetail = StringUtils.replace(addressInfo, "\n",
								" ");
						System.out.println(addresDetail);
					}
				}

			}
			String phoneValue = "";
			List<?> phoneList = page
					.getByXPath("/html/body/div[2]/div[1]/div[4]/div[4]/table/tbody/tr/td[2]/table/tbody/tr/td/div");
			Iterator<?> phoneDetail = phoneList.iterator();
			while (phoneDetail.hasNext()) {
				HtmlElement next = (HtmlElement) phoneDetail.next();
				phoneValue = StringUtils.replace(next.asText(), ",", " ");
				if (phoneValue.contains("@")) {
					emailValue += " " + phoneValue;
					phoneValue = "";
				}
			}
			// Website
			writer.append(websiteValue);
			writer.append(",");

			// Address
			writer.append(addresDetail);
			writer.append(",");

			// Email
			writer.append(emailValue);
			writer.append(",");

			// Phone
			writer.append(phoneValue);
			writer.append(",");

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
