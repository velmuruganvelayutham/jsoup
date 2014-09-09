package org.jsoup.examples;

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

public class ABCKids14 {

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {

		FileWriter writer = new FileWriter(
				"/home/velmuruganv/Downloads/abckids.csv");
		String[] headers = { "Show", "Show Dates", "Vendor name", "Booth No",
				"Address", "Phone", "Website", "Email", "Description",
				"Categories" };
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
		for (int i = 1; i <= 1; i++) {
			page = webClient
					.getPage("http://abckids14.mapyourshow.com/5_0/exhibitor_results.cfm?alpha=@&type=alpha&page="
							+ i + "#GotoResults");
			Thread.sleep(2000);
			for (int j = 3, k = 2; j < 56; j++) {
				List<?> byXPath = null;
				if (j < 46) {
					// byXPath = page
					// .getByXPath("//*[@id=\"mys-main-content\"]/table[1]/tbody/tr["
					// + j + "]/td[2]/a");
					// if (byXPath.size() == 0) {
					// byXPath = page
					// .getByXPath("//*[@id=\"mys-main-content\"]/table[1]/tbody/tr["
					// + j + "]/td[2]/strong/a");
					// }
				} else {
					byXPath = page
							.getByXPath("//*[@id=\"mys-main-content\"]/table[2]/tbody/tr["
									+ k + "]/td[2]/a");
					if (byXPath.size() == 0) {
						byXPath = page
								.getByXPath("//*[@id=\"mys-main-content\"]/table[2]/tbody/tr["
										+ k + "]/td[2]/strong/a");
					}
					k++;
				}
				if (byXPath != null && byXPath.size() > 0) {
					HtmlAnchor x = (HtmlAnchor) byXPath.get(0);
					links.add(x);
					System.out.println(i + " Page Link is found : " + j + " "
							+ x);
				}

			}

			System.out.println("Total number of links " + links.size());
		}

		// Iterate through hashset

		for (HtmlAnchor link : links) {
			// Show
			writer.append(" ");
			writer.append(",");

			// Show date
			writer.append(" ");
			writer.append(",");
			HtmlPage click = link.click();
			StringBuffer sb = new StringBuffer();
			List<?> vendorList = click
					.getByXPath("//*[@id=\"mys-exhibitorInfo\"]/h2");
			if (vendorList.size() > 0) {
				HtmlHeading2 htmlHeading = (HtmlHeading2) vendorList.get(0);

				String vendor = StringUtils.replace(htmlHeading.asText(), ",",
						"-");
				System.out.println("html heading 2 " + vendor);
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
				System.out.println("html heading 4 " + boothNo);
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
			System.out.println(" address " + sb);
		}
		writer.flush();
		writer.close();
	}
}
