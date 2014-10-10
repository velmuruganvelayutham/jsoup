package com.tocgroup.scrap;

import java.io.File;
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

public class ToyFair implements Scraper {

	static int l = 1;
	static String previousVendor = "";

	public static void main(String[] args) throws Exception,
			InterruptedException {
		new ToyFair().extract();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tocgroup.scrap.scraber#extract()
	 */
	public File extract() throws Exception {
		long startTime = System.currentTimeMillis();
		// FileWriter writer = new FileWriter("/TOC/supplier_tradeshowNew.csv");
		File file = File.createTempFile(ToyFair.class.getName(), "csv");
		FileWriter writer = new FileWriter(file);
		String[] headers = { "Show", "Show Dates", "Vendor name", "Booth No",
				"Address", "Phone", "Website", "Email", "Description",
				"Categories" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		// Show Date
		String showDateDetails = " ";
		String showUrl = "http://www.toyfairny.com/ToyFair/ShowInfo/About_the_Show/Toy_Fair/Show_Info/About_the_Show.aspx";
		Document showDateDoc = Jsoup.connect(showUrl).get();

		Elements showDateDetail = showDateDoc
				.getElementsByClass("iMIS-WebPart");

		for (Element showDate : showDateDetail) {
			Elements tagName = showDate.getElementsByTag("table");
			for (Element tableData : tagName) {
				String showDates = StringUtils.replace(tableData
						.getElementsByTag("td").html(), " – ", "-");
				showDates = StringUtils.replace(showDates, ",", "-");
				showDates = StringUtils.replace(showDates, "&nbsp;", " ");
				String[] split = showDates.split("\n");
				showDateDetails = "\"" + split[0] + " " + split[1] + "\n"
						+ split[2] + " " + split[3] + "\n" + split[4] + " "
						+ split[5] + "\"";
				System.out.println(showDateDetails);
				break;
			}
			break;
		}

		final WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		final HtmlPage page = webClient
				.getPage("http://s23.a2zinc.net/clients/TIA/ToyFair2015/Public/EventMap.aspx");

		generateCSV(writer, page, showDateDetails);

		for (int k = 1; k < 4; k++) {
			List<?> nextButtonList = page
					.getByXPath("//*[@id=\"ctl00_ctl00_cph1_cph1_ucExhibitorList_dockExhibitorList_C_radExhibitorList_ctl00_Pager\"]/tbody/tr/td/table/tbody/tr/td/div[3]/input[1]");
			Iterator<?> iterator = nextButtonList.iterator();
			while (iterator.hasNext()) {
				HtmlElement next = (HtmlElement) iterator.next();
				next.click();
				generateCSV(writer, page, showDateDetails);
			}
		}

		writer.flush();
		writer.close();

		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to prepare csv "
				+ (endTime - startTime) / 1000 + " Seconds ");
		return file;
	}

	private static void generateCSV(FileWriter writer, final HtmlPage page,
			String showDates) throws IOException, InterruptedException {
		int k = 0;

		for (int i = 0; i < 200; i++) {
			l++;
			int m = 1;
			boolean flag = false;

			while (m < 60000) {
				List<?> byXPath = page
						.getByXPath("//*[@id=\"ctl00_ctl00_cph1_cph1_ucExhibitorList_dockExhibitorList_C_radExhibitorList_ctl00__"
								+ i + "\"]/td[2]/div");
				if (byXPath.size() > 0) {
					String substringBetween = StringUtils.substringBetween(
							byXPath.get(0).toString(), "(", ")");
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
						String vendor = StringUtils.replace(vendors.text(),
								",", " ");

						if (previousVendor.equals(vendor)) {
							Thread.sleep(500);
							m++;
							break;
						}
						if (k == 0) {
							previousVendor = vendor;
							k++;
						}
						// Show
						writer.append("Toy Fair NY15");
						writer.append(",");

						// Show date
						writer.append(showDates);
						writer.append(",");

						// Vendor Name
						writer.append(vendor);
						writer.append(",");

						// Booth No.
						Elements boothNo = document
								.getElementsByClass("BoothLabelContainer");
						if (boothNo.size() > 0) {
							String splitBoothNo = boothNo.text();
							String[] boothNumber = splitBoothNo.split(": ");
							String booth = StringUtils.replace(boothNumber[1],
									",", ":");
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
							String cityName = StringUtils.replace(cityHtml,
									",", "-");
							cityName = StringUtils.replace(cityName, "&nbsp;",
									" ");
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
							String profiles = StringUtils.replace(
									profile.text(), ",", " ");
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
									category.text(), ",", "");
							categoryList += categoryText;
							if (categories.size() > j) {
								categoryList += " ";
							}
						}
						writer.append(categoryList);
						flag = true;
					}
				} else {
					Thread.sleep(500);
					m++;
				}
				if (flag) {
					break;
				}
			}
			writer.append("\n");
			System.out.println(l);
			if (l > 732) {
				break;
			}
		}
	}
}
