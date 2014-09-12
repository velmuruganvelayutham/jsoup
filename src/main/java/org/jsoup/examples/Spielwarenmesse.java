package org.jsoup.examples;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Spielwarenmesse {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		long startTime = System.currentTimeMillis();
		FileWriter writer = new FileWriter("/TOC/Spielwarenmesse.csv");

		String[] headers = { "Show", "Show Date", "Vendor name", "Booth No.",
				"Address", "Phone", "Fax", "Website", "Product Category" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		String showUrl = "http://mediaservices.spielwarenmesse.de/2014/en/exhibitors.php";
		final WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		final HtmlPage page = webClient.getPage(showUrl);

		List<?> linkList = null;
		Iterator<?> linkDetail = null;
		int r = 0;
		while (r < 60) {
			linkList = page
					.getByXPath("//*[@id=\"aussteller_1\"]/div/table/tbody/tr/td[1]/table/tbody/tr/td[2]/a[1]");
			linkDetail = linkList.iterator();
			if (!linkDetail.hasNext()) {
				Thread.sleep(1000);
				r++;
			} else {
				break;
			}
		}

		while (linkDetail.hasNext()) {
			HtmlElement link = (HtmlElement) linkDetail.next();
			String companyName = StringUtils.replace(link.asText(), ",", " ");

			String id = StringUtils.substringBetween(
					linkList.get(0).toString(), "('", "')");
			writer.append(companyName);
			System.out.println("Company::::" + companyName);

			String url = "http://mediaservices.spielwarenmesse.de/2014/en/exhibitors.php?opi%5Bsuche%5D%5Banr%5D="
					+ id + "&master%5Bbuchstfilter%5D=&suche=los";

			final WebClient webClient1 = new WebClient();
			webClient1.getOptions().setUseInsecureSSL(true);
			webClient1.getOptions().setJavaScriptEnabled(true);
			webClient1.getOptions().setThrowExceptionOnFailingStatusCode(false);
			final HtmlPage page1 = webClient1.getPage(url);
			System.out.println(url);
			int m = 0;
			boolean flag = false;
			while (m < 60) {
				List<?> addressList = page1
						.getByXPath("//*[@id=\"aussteller_detail\"]/div/table/tbody/tr/td/div/div[2]");
				Iterator<?> addressDetail = addressList.iterator();
				if (!addressDetail.hasNext()) {
					Thread.sleep(1000);
					m++;
				}
				while (addressDetail.hasNext()) {
					HtmlElement address = (HtmlElement) addressDetail.next();
					String addressName = StringUtils.replace(address.asText(),
							",", " ");
					addressName = StringUtils.replace(addressName, "\n", " ");
					System.out.println("Address::::" + addressName);
					writer.append(addressName);
					writer.append(",");
					flag = true;
				}
				if (flag) {
					break;
				}
			}
			String phoneValue = "";
			String emailValue = "";
			String websiteValue = "";
			List<?> comunicationList = page1
					.getByXPath("//*[@id=\"aussteller_detail\"]/div/table/tbody/tr/td/div/div[3]");
			Iterator<?> comunicationDetail = comunicationList.iterator();
			while (comunicationDetail.hasNext()) {
				HtmlElement comunication = (HtmlElement) comunicationDetail
						.next();
				String comunicationName = StringUtils.replace(
						comunication.asText(), ",", " ");
				String[] split = comunicationName.split("\n");
				for (int l = 0; l < split.length; l++) {
					if (split[l].contains("Tel:")) {
						phoneValue = StringUtils.remove(split[l], "Tel:	 	");
						phoneValue = StringUtils.replace(phoneValue, ",", " ");
					} else if (split[l].contains("E-Mail:")) {
						emailValue = StringUtils.remove(split[l], "E-Mail:	 	");
						emailValue = StringUtils.replace(emailValue, ",", " ");
					} else if (split[l].contains("Website:")) {
						websiteValue = StringUtils.remove(split[l],
								"Website:	 	");
						websiteValue = StringUtils.replace(websiteValue, ",",
								" ");

					}
				}

				writer.append(websiteValue);
				writer.append(",");

				writer.append(phoneValue);
				writer.append(",");

				writer.append(emailValue);
				writer.append(",");
			}

			writer.append("\n");
		}
		writer.flush();
		writer.close();
		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to prepare csv "
				+ (endTime - startTime) / 1000 + " Seconds ");
	}

}
