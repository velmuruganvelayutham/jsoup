package com.tocgroup.scrap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlHeading2;
import com.gargoylesoftware.htmlunit.html.HtmlHeading4;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ABCKids14 implements Scraper {

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		File extract = new ABCKids14().extract();
		System.out.println(extract.getAbsolutePath());
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 */
	public File extract() throws IOException, FailingHttpStatusCodeException,
			MalformedURLException {
		long startTime = System.currentTimeMillis();
		File file = File.createTempFile(ABCKids14.class.getName(), "csv");
		FileWriter writer = new FileWriter(file);
		// FileWriter writer = new FileWriter(
		// "/home/velmuruganv/Downloads/abckids.csv");
		String[] headers = { "Show", "Show Date", "Vendor Name", "Booth No",
				"Address", "Phone", "Website", "Email", "Description",
				"Product Categories" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		final WebClient webClient = new WebClient();
		WebClientOptions options = webClient.getOptions();
		options.setJavaScriptEnabled(false);
		options.setUseInsecureSSL(true);
		HtmlPage page = null;
		HashSet<HtmlAnchor> links = new HashSet<HtmlAnchor>();
		for (int i = 1; i <= 11; i++) {
			page = webClient
					.getPage("http://abckids14.mapyourshow.com/5_0/exhibitor_results.cfm?alpha=@&type=alpha&page="
							+ i + "#GotoResults");
			boolean loaded = false;
			while (!loaded) {
				List<?> firstRecord = page
						.getByXPath("//*[@id=\"mys-main-content\"]/table[2]/tbody/tr[2]/td[2]/a");
				List<?> lastRecord = page
						.getByXPath("//*[@id=\"mys-main-content\"]/table[2]/tbody/tr[101]/td[2]/a");
				if ((firstRecord.size() > 0 && lastRecord.size() > 0)
						|| (firstRecord.size() > 0)) {
					loaded = true;
					System.out.println("Page is loaded !");
				}
			}
			for (int j = 2; j < 102; j++) {
				List<?> byXPath = null;
				byXPath = page
						.getByXPath("//*[@id=\"mys-main-content\"]/table[2]/tbody/tr["
								+ j + "]/td[2]/a");
				if (byXPath.size() == 0) {
					byXPath = page
							.getByXPath("//*[@id=\"mys-main-content\"]/table[2]/tbody/tr["
									+ j + "]/td[2]/strong/a");
				}
				if (byXPath != null && byXPath.size() > 0) {
					HtmlAnchor x = (HtmlAnchor) byXPath.get(0);
					links.add(x);
					System.out.println(i
							+ " Page Link is added to the hashset : " + j + " "
							+ x);
				}
			}
			System.out.println("Total number of links " + links.size());
		}
		// Iterate through hashset

		for (HtmlAnchor link : links) {

			// Show
			writer.append(" ABC Kids Expo ");
			writer.append(",");
			// Show date
			writer.append("September 7-10-2014- Las Vegas Convention Center ");
			writer.append(",");
			HtmlPage click = link.click();
			StringBuffer sb = new StringBuffer();
			List<?> vendorList = click
					.getByXPath("//*[@id=\"mys-exhibitorInfo\"]/h2");
			if (vendorList.size() > 0) {
				HtmlHeading2 htmlHeading = (HtmlHeading2) vendorList.get(0);
				String vendor = StringUtils.replace(htmlHeading.asText(), ",",
						"-");
				System.out.println("Vendor :-> " + vendor);
				// Vendor Name
				writer.append(vendor);
				writer.append(",");
			}
			List<?> floorName = click
					.getByXPath("//*[@id=\"mys-exhibitorInfo\"]/h4");
			if (floorName.size() > 0) {
				HtmlHeading4 htmlHeading = (HtmlHeading4) floorName.get(0);
				String boothNo = StringUtils.replace(htmlHeading.asText(), ",",
						"-");
				System.out.println("Booth No :-> " + boothNo);
				// Booth No
				writer.append(boothNo);
				writer.append(",");
			}
			for (int i = 1; i <= 5; i++) {
				List<?> addressList = click
						.getByXPath("// *[@id=\"mys-exhibitorInfo\"]/ul/li["
								+ i + "]");
				if (addressList.size() > 0) {
					HtmlListItem listItem = (HtmlListItem) addressList.get(0);
					String address = StringUtils.replace(listItem.asText(),
							",", "-");
					sb.append(address);
				}
			}
			// Address
			writer.append(sb.toString());
			writer.append("\n");
			System.out.println(" Address :-> " + sb);
		}
		writer.flush();
		writer.close();

		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to prepare csv "
				+ (endTime - startTime) / 1000 + " Seconds ");
		return file;
	}
}
