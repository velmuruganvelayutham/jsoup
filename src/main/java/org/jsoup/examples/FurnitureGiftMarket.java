package org.jsoup.examples;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FurnitureGiftMarket {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		long startTime = System.currentTimeMillis();
		FileWriter writer = new FileWriter("/TOC/FurnitureGiftMarket.csv");

		String[] headers = { "Show", "Show Date", "Vendor name", "Booth No.",
				"Website", "Description", "Address", "City", "State",
				"Country", "Postal Code", "Phone", "Categories" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		String vendorId = "";
		int i = 0;
		int n = 1;
		while (n < 84) {
			String pageNo = String.valueOf(n);
			Connection.Response res = Jsoup
					.connect(
							"http://www.lasvegasmarket.com/market-information/exhibitor-directory.html")
					.data("hidden_submit", "submit", "page", pageNo)
					.timeout(60000).method(Method.POST).execute();
			Document showDoc = res.parse();
			Elements vendorDetail = showDoc.getElementsByAttributeValue(
					"class", "resultMaster close");
			for (Element vendor : vendorDetail) {
				i++;
				// Show
				writer.append("Las Vegas Market");
				writer.append(",");

				// Show Date
				writer.append("January 18-22");
				writer.append(",");

				// Vendor Name
				Elements vendorNameDetail = vendor.getElementsByTag("span");
				String vendorName = "";
				String boothNumber = "";
				String website = "";
				for (Element vendorNames : vendorNameDetail) {
					Elements vendorValue = vendorNames
							.getElementsByAttributeValue("class",
									"d1 showDirectoryDetails closed clip");
					vendorName = StringUtils.replace(vendorValue.text(), ",",
							" ");
					vendorId = vendorValue.attr("id");
					if (!vendorName.isEmpty()) {
						break;
					}
				}

				for (Element boothNo : vendorNameDetail) {
					Elements boothNoValue = boothNo
							.getElementsByAttributeValue("class",
									"d5 Booth clip");
					boothNumber = StringUtils.replace(boothNoValue.text(), ",",
							" ");
					if (!boothNumber.isEmpty()) {
						break;
					}
				}

				for (Element websites : vendorNameDetail) {
					Elements websiteValue = websites
							.getElementsByAttributeValue("class", "d4 clip");
					website = StringUtils
							.replace(websiteValue.text(), ",", " ");
					if (!website.isEmpty()) {
						break;
					}
				}

				writer.append(vendorName);
				writer.append(",");

				writer.append(boothNumber);
				writer.append(",");

				writer.append(website);
				writer.append(",");

				String description = "";
				String companyName = "";
				String address = "";
				String city = "";
				String state = "";
				String country = "";
				String postalCode = "";
				String phone = "";
				String categories = "";
				String id = StringUtils.remove(vendorId, "companyId_");
				String[] splitId = id.split("_");

				Document showVendorDetail = Jsoup.connect(
						"http://www.lasvegasmarket.com/directory/general.php?companyid="
								+ splitId[0]).get();

				Elements companyDetail = showVendorDetail
						.getElementsByTag("table");
				for (Element company : companyDetail) {
					String companyInfo = company.text();
					if (companyInfo.contains("Company Description:")) {
						description = StringUtils.remove(companyInfo,
								"Company Description:");
						description = StringUtils
								.replace(description, ",", " ");
						description = "\"" + description + "\"";
					} else if (companyInfo.contains("Company Name:")) {
						companyName = StringUtils.remove(companyInfo,
								"Company Name:");
						String[] splitAddress = companyName.split("Address:  ");
						companyName = splitAddress[0];
						if (splitAddress.length > 1) {
							String[] splitCity = splitAddress[1]
									.split("City:   ");
							address = StringUtils.replace(splitCity[0], ",",
									" ");
							address = StringUtils.replace(address, "\n", " ");
							if (splitCity.length > 1) {
								String[] splitState = splitCity[1]
										.split("State/Province:   ");
								city = StringUtils.replace(splitState[0], ",",
										" ");
								city = StringUtils.remove(city, "\r");
								city = StringUtils.replace(city, "\n", " ");
								if (splitState.length > 1) {
									String[] splitCountry = splitState[1]
											.split("Country:   ");
									state = StringUtils.replace(
											splitCountry[0], ",", " ");
									state = StringUtils.remove(state, "\r");
									state = StringUtils.replace(state, "\n",
											" ");
									if (splitCountry.length > 1) {
										String[] splitPostalCode = splitCountry[1]
												.split("Postal Code:   ");
										country = StringUtils.replace(
												splitPostalCode[0], ",", " ");
										country = StringUtils.remove(country,
												"\r");
										country = StringUtils.replace(country,
												"\n", " ");
										if (splitPostalCode.length > 1) {
											String[] splitPhone = splitPostalCode[1]
													.split("Phone:   ");
											postalCode = StringUtils.replace(
													splitPhone[0], ",", " ");
											postalCode = StringUtils.remove(
													postalCode, "\r");
											postalCode = StringUtils.replace(
													postalCode, "\n", " ");
											if (splitPhone.length > 1) {
												String[] splitToll = splitPhone[1]
														.split("Toll-Free:  ");
												phone = StringUtils.replace(
														splitToll[0], ",", " ");
												phone = StringUtils.remove(
														phone, "\r");
												phone = StringUtils.replace(
														phone, "\n", " ");
											}

										}
									}

								}

							}
						}

					} else if (companyInfo.contains("Product Category:")) {

						categories = StringUtils.remove(companyInfo,
								"Product Category:");
						categories = StringUtils.replace(categories, ",", "\n");
						categories = "\"" + categories + "\"";
					}
				}

				// Description
				writer.append(description);
				writer.append(",");

				// Address
				writer.append(address);
				writer.append(",");

				// City
				writer.append(city);
				writer.append(",");

				// State
				writer.append(state);
				writer.append(",");

				// Country
				writer.append(country);
				writer.append(",");

				// Postal code
				writer.append(postalCode);
				writer.append(",");

				// Phone
				writer.append(phone);
				writer.append(",");

				// Product Category
				writer.append(categories);
				writer.append(",");

				System.out.println("Count::::" + i);

				writer.append("\n");

			}

			n++;

		}

		writer.flush();
		writer.close();

		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to prepare csv "
				+ (endTime - startTime) / 1000 + " Seconds ");
	}
}
