package org.jsoup.examples;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TradeShowScraper2 {

	public static void main(String[] args) throws IOException,
			InterruptedException {

		FileWriter writer = new FileWriter("/TOC/supplier_tradeshowNew.csv");
		String[] headers = { "Show", "Show Dates", "Vendor name", "Booth No",
				"Address", "Phone", "Website", "Email", "Description",
				"Categories" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		final WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		final HtmlPage page = webClient
				.getPage("http://s23.a2zinc.net/clients/TIA/ToyFair2015/Public/EventMap.aspx");
		Thread.sleep(30000);
		for (int i = 1; i < 200; i++) {
			List<?> byXPath = page
					.getByXPath("//*[@id=\"ctl00_ctl00_cph1_cph1_ucExhibitorList_dockExhibitorList_C_radExhibitorList_ctl00__"
							+ i + "\"]/td[2]/div");
			if (byXPath.size() > 0) {
				String substringBetween = StringUtils.substringBetween(byXPath
						.get(0).toString(), "(", ")");
				String[] split = StringUtils.split(substringBetween, ",");
				String showUrl = "http://s23.a2zinc.net/clients/TIA/ToyFair2015/Public/eBooth.aspx?Nav=false&BoothID="
						+ split[0]
						+ "&EventID="
						+ split[1]
						+ "&CoID="
						+ split[2] + "&Source=" + split[3];
				Document document = Jsoup.connect(showUrl).get();
				Elements vendorName = document
						.getElementsByClass("BoothExhibitorName");
				for (Element vendors : vendorName) {

					// Show
					writer.append(" ");
					writer.append(",");

					// Show date
					writer.append(" ");
					writer.append(",");

					// Vendor Name
					String vendor = StringUtils.replace(vendors.text(), ",",
							" ");
					writer.append(vendor);
					writer.append(",");

					// Booth No.
					Elements boothNo = document
							.getElementsByClass("BoothLabelContainer");
					if (boothNo.size() > 0) {
						String splitBoothNo = boothNo.text();
						String[] boothNumber = splitBoothNo.split(": ");
						String booth = StringUtils.replace(boothNumber[1], ",",
								":");
						writer.append(booth);
						writer.append(",");
					} else {
						writer.append(" ");
						writer.append(",");
					}

					// Address
					String address = " ";
					Elements city = document
							.getElementsByClass("BoothContactCity");
					if (city.size() > 0) {
						String cityHtml = city.html();
						String cityName = StringUtils.replace(cityHtml, ",",
								"-");
						cityName = StringUtils.replace(cityName, "&nbsp;", " ");
						address = cityName;
					}

					Elements state = document
							.getElementsByClass("BoothContactState");
					if (state.size() > 0) {
						String stateHtml = state.html();
						String stateName = StringUtils.replace(stateHtml,
								"&nbsp;", " ");
						address += stateName;
					}

					Elements country = document
							.getElementsByClass("BoothContactCountry");
					if (country.size() > 0) {
						String countryName = country.text();
						address += " " + countryName;
					}

					writer.append(address);
					writer.append(",");

					// Phone
					writer.append(" ");
					writer.append(",");

					// Website
					Elements website = document
							.getElementsByClass("BoothContactUrl");
					if (website.size() > 0) {
						writer.append(website.text());
						writer.append(",");
					} else {
						writer.append(" ");
						writer.append(",");
					}

					// Email
					writer.append(" ");
					writer.append(",");

					// Description
					Elements profile = document
							.getElementsByClass("BoothPrintProfile");
					if (profile.size() > 0) {
						String profiles = StringUtils.replace(profile.text(),
								",", " ");
						writer.append(profiles);
						writer.append(",");
					} else {
						writer.append(" ");
						writer.append(",");
					}

					// Categories
					int j = 0;
					String categoryList = " ";
					Elements categories = document
							.getElementsByClass("ProductCategoryLi");
					for (Element category : categories) {
						j++;
						String categoryText = StringUtils.replace(
								category.text(), ",", " ");
						categoryList += categoryText;
						if (categories.size() > j) {
							categoryList += " ";
						}
					}
					writer.append(categoryList);

				}
			}
			writer.append('\n');
		}

		writer.flush();
		writer.close();
		// webClient.closeAllWindows();

	}
}
