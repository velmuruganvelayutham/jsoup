package com.tocgroup.scrap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ExpocadWeb implements Scraper {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		new ExpocadWeb().extract();
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws InterruptedException
	 */
	public File extract() throws IOException, FailingHttpStatusCodeException,
			MalformedURLException, InterruptedException {
		long startTime = System.currentTimeMillis();
		File file = File.createTempFile(ExpocadWeb.class.getName(), "csv");
		FileWriter writer = new FileWriter(file);
		// FileWriter writer = new FileWriter("/TOC/supplier_web.csv");
		String previousCompanyName = "";
		String previousBoothNumber = "";

		String[] headers = { "Show", "Show Date", "Vendor name", "Booth No.",
				"Address", "Phone", "Fax", "Website", "Product Category" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		String showUrl = "http://www.expocadweb.com/2015tw/ec/forms/attendee/indexTab.aspx";
		final WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		final HtmlPage page = webClient.getPage(showUrl);

		String show = "";
		List<?> showList = page.getByXPath("//*[@id=\"eventNameLabel\"]");
		Iterator<?> showIterate = showList.iterator();
		while (showIterate.hasNext()) {
			HtmlElement next = (HtmlElement) showIterate.next();
			show = next.asText();
		}

		String showDate = "";
		List<?> showDateList = page.getByXPath("//*[@id=\"eventDatesLabel\"]");
		Iterator<?> showDateIterate = showDateList.iterator();
		while (showDateIterate.hasNext()) {
			HtmlElement next = (HtmlElement) showDateIterate.next();
			showDate = next.asText();
		}

		int n = 1;
		while (n < 15) {
			for (int i = 3; i < 23; i++) {

				List<?> nextLinkList = null;
				String companyValue = null;
				String boothNumber = null;
				if (i < 10) {
					nextLinkList = page
							.getByXPath("//*[@id=\"indexTabContainer_ExhibListTab_list1_listGrid_ctl0"
									+ i + "_nameHyperLink\"]");
				} else {
					nextLinkList = page
							.getByXPath("//*[@id=\"indexTabContainer_ExhibListTab_list1_listGrid_ctl"
									+ i + "_nameHyperLink\"]");
				}

				Iterator<?> iterator = nextLinkList.iterator();
				while (iterator.hasNext()) {
					HtmlElement next = (HtmlElement) iterator.next();
					next.click();
					// Company Name
					int m = 1;
					boolean flags = false;
					while (m < 60000) {

						// Company List
						List<?> companyList = page
								.getByXPath("//*[@id=\"indexTabContainer_DbTab_vbnoframe1_div1\"]/table/tbody/tr/td/table[1]/tbody/tr[1]/td[2]/span");
						Iterator<?> companyName = companyList.iterator();

						// Booth Number
						List<?> boothNoList = page
								.getByXPath("//*[@id=\"indexTabContainer_DbTab_vbnoframe1_div1\"]/table/tbody/tr/td/table[1]/tbody/tr[2]/td[1]/span[2]/a");
						Iterator<?> boothNoDetail = boothNoList.iterator();

						while (companyName.hasNext()) {
							HtmlElement company = (HtmlElement) companyName
									.next();
							companyValue = StringUtils.replace(
									company.getTextContent(), ",", " ");
							while (boothNoDetail.hasNext()) {
								HtmlElement boothNo = (HtmlElement) boothNoDetail
										.next();
								boothNumber = StringUtils.replace(
										boothNo.getTextContent(), ",", " ");

								if (previousCompanyName.equals(companyValue)
										&& previousBoothNumber
												.equals(boothNumber)) {
									Thread.sleep(1000);
									m++;
								} else {
									// Show
									writer.append(show);
									writer.append(",");

									// Show Date
									writer.append(showDate);
									writer.append(",");

									// Comapny
									writer.append(companyValue);
									writer.append(",");

									// booth Number
									writer.append(boothNumber);
									writer.append(",");
									System.out.println("Count Companies::" + i);
									flags = true;
								}
							}
						}
						if (flags) {
							break;
						}
					}

					// Address
					List<?> addressList = page
							.getByXPath("//*[@id=\"indexTabContainer_DbTab_vbnoframe1_div1\"]/table/tbody/tr/td/table[1]/tbody/tr[2]/td[1]/span[1]");
					Iterator<?> addressDetail = addressList.iterator();
					while (addressDetail.hasNext()) {
						HtmlElement address = (HtmlElement) addressDetail
								.next();

						String addressDetails = "";
						String addresses = StringUtils.remove(address.asText(),
								"\r");

						String[] split = addresses.split("\n");

						int k = 0;
						// Address
						if (split.length != 0) {
							for (int l = 0; l < split.length; l++) {
								String addressName = StringUtils.replace(
										split[l], ",", "");
								addressDetails += addressName + " ";
								if (split[l].equals("")) {
									break;
								}
								k++;
							}
							writer.append(addressDetails);
							writer.append(",");
						} else {
							writer.append("");
							writer.append(",");
						}

						// Phone
						if (split.length > (k + 1)) {
							if ((!split[k + 1].equals(""))) {
								String phone = StringUtils.replace(
										split[k + 1], ",", "");
								phone = StringUtils.remove(phone, "Phone:");
								writer.append(phone);
								writer.append(",");
							}
						} else {
							writer.append("");
							writer.append(",");
						}

						if (split.length > (k + 2)) {
							if (split[k + 2].contains("com")
									&& (!split[k + 2].equals(""))) {

								writer.append("");
								writer.append(",");

								String website = StringUtils.replace(
										split[k + 2], ",", "");
								writer.append(website);
								k++;
								writer.append(",");
							} else {
								String fax = StringUtils.replace(split[k + 2],
										",", "");
								fax = StringUtils.remove(fax, "Fax:");
								writer.append(fax);
								writer.append(",");
							}
						} else {
							writer.append("");
							writer.append(",");
						}

						// website
						if (split.length > (k + 3)) {
							String website = StringUtils.replace(split[k + 3],
									",", "");
							writer.append(website);
							writer.append(",");
						} else {
							writer.append("");
							writer.append(",");
						}

					}

					// Product Category
					List<?> productCategoryList = page
							.getByXPath("//*[@id=\"indexTabContainer_DbTab_vbnoframe1_div1\"]/table/tbody/tr/td/table[2]/tbody/tr[2]/td/table/tbody/tr/td");
					Iterator<?> productCategoryDetail = productCategoryList
							.iterator();
					while (productCategoryDetail.hasNext()) {
						HtmlElement productCategory = (HtmlElement) productCategoryDetail
								.next();

						String productCategoryDetails = "\"";
						String productCategories = StringUtils.remove(
								productCategory.asText(), "\r");
						productCategoryDetails += productCategories + "\"";
						writer.append(productCategoryDetails);
					}

				}
				writer.append("\n");
				previousCompanyName = companyValue;
				previousBoothNumber = boothNumber;
			}
			boolean flag = false;
			List<HtmlAnchor> navigationList = page.getAnchors();
			Iterator<?> navigationDetail = navigationList.iterator();
			while (navigationDetail.hasNext()) {
				HtmlElement navigation = (HtmlElement) navigationDetail.next();
				if (navigation.getAttribute("title").equals("Next Page")) {
					navigation.click();
					Thread.sleep(5000);
					flag = true;
				}
				if (flag) {
					break;
				}
			}
			System.out.print("Count Of Link::" + n);
			n++;
		}
		writer.flush();
		writer.close();
		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to prepare csv "
				+ (endTime - startTime) / 1000 + " Seconds ");
		return file;
	}
}
