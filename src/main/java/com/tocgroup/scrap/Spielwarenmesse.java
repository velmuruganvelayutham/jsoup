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

public class Spielwarenmesse implements Scraper {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		new Spielwarenmesse().extract();
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
		File file = File.createTempFile(Spielwarenmesse.class.getName(), "csv");
		FileWriter writer = new FileWriter(file);
		// FileWriter writer = new FileWriter("/TOC/Spielwarenmesse.csv");

		String[] headers = { "Show", "Show Date", "Vendor Name", "Booth No",
				"Address", "Website", "Phone", "Fax", "Email" };
		for (String header : headers) {
			writer.append(header);
			writer.append(',');
		}
		writer.append('\n');

		String showUrl = "http://mediaservices.spielwarenmesse.de/2014/en/exhibitors.php";
		final WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setTimeout(60000);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		final HtmlPage page = webClient.getPage(showUrl);

		List<?> linkList = null;
		Iterator<?> linkDetail = null;
		int r = 0;
		int n = 1;
		int c = 0;
		int count = 0;
		String companyName = "";
		String boothNumber = "";
		String id = "";
		while (n < 180) {
			for (int i = 0; i < 15; i++) {
				count++;
				while (r < 60) {
					linkList = page
							.getByXPath("//*[@id=\"aussteller_"
									+ i
									+ "\"]/div/table/tbody/tr/td[1]/table/tbody/tr/td[2]/a[1]");
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
					id = StringUtils.substringBetween(linkList.get(0)
							.toString(), "('", "')");

					String url = "http://mediaservices.spielwarenmesse.de/2014/en/exhibitors.php?opi%5Bsuche%5D%5Banr%5D="
							+ id + "&master%5Bbuchstfilter%5D=&suche=los";

					final WebClient webClient1 = new WebClient();
					webClient1.getOptions().setUseInsecureSSL(true);
					webClient1.getOptions().setJavaScriptEnabled(true);
					webClient1.getOptions()
							.setThrowExceptionOnFailingStatusCode(false);
					webClient1.getOptions().setTimeout(60000);
					final HtmlPage page1 = webClient1.getPage(url);
					int m = 0;
					boolean flag = false;
					String addressName = "";
					while (m < 60) {
						List<?> addressList = page1
								.getByXPath("//*[@id=\"aussteller_detail\"]/div/table/tbody/tr/td/div/div[2]");
						Iterator<?> addressDetail = addressList.iterator();
						if (!addressDetail.hasNext()) {
							Thread.sleep(1000);
							m++;
						}
						while (addressDetail.hasNext()) {
							HtmlElement address = (HtmlElement) addressDetail
									.next();
							addressName = StringUtils.replace(address.asText(),
									",", " ");
							addressName = StringUtils.replace(addressName,
									"\n", " ");
							addressName = StringUtils.replace(addressName,
									"\r", " ");
							flag = true;
						}
						if (flag) {
							break;
						}
					}

					List<?> companyList = page1
							.getByXPath("//*[@id=\"aussteller_detail\"]/div/table/tbody/tr/td/div/div[1]/a");
					Iterator<?> companyDetail = companyList.iterator();
					while (companyDetail.hasNext()) {
						HtmlElement company = (HtmlElement) companyDetail
								.next();
						companyName = StringUtils.replace(company.asText(),
								",", " ");
						companyName = StringUtils.replace(companyName, "\n",
								" ");
						companyName = StringUtils.replace(companyName, "\r",
								" ");
						System.out.println("Company::::" + companyName);

					}

					List<?> boothNoList = page1
							.getByXPath("//*[@id=\"aussteller_detail\"]/div/table/tbody/tr/td/div/div[1]/nobr/a");
					Iterator<?> boothNoDetail = boothNoList.iterator();
					while (boothNoDetail.hasNext()) {
						HtmlElement boothNo = (HtmlElement) boothNoDetail
								.next();
						boothNumber = StringUtils.replace(boothNo.asText(),
								",", " ");
						boothNumber = StringUtils.replace(boothNumber, "\n",
								" ");
						boothNumber = StringUtils.replace(boothNumber, "\r",
								" ");
						System.out.println("Company::::" + boothNumber);

					}

					String phoneValue = "";
					String emailValue = "";
					String websiteValue = "";
					String faxValue = "";
					List<?> comunicationList = page1
							.getByXPath("//*[@id=\"aussteller_detail\"]/div/table/tbody/tr/td/div/div[3]");
					Iterator<?> comunicationDetail = comunicationList
							.iterator();
					while (comunicationDetail.hasNext()) {
						HtmlElement comunication = (HtmlElement) comunicationDetail
								.next();
						String comunicationName = StringUtils.replace(
								comunication.asText(), ",", " ");
						String[] split = comunicationName.split("\n");
						for (int l = 0; l < split.length; l++) {
							if (split[l].contains("Tel:")) {
								phoneValue = StringUtils.remove(split[l],
										"Tel:	 	");
								phoneValue = StringUtils.replace(phoneValue,
										",", " ");
								phoneValue = StringUtils.remove(phoneValue,
										"\r");
							} else if (split[l].contains("E-Mail:")) {
								emailValue = StringUtils.remove(split[l],
										"E-Mail:	 	");
								emailValue = StringUtils.replace(emailValue,
										",", " ");
								emailValue = StringUtils.remove(emailValue,
										"\r");
							} else if (split[l].contains("Website:")) {
								websiteValue = StringUtils.remove(split[l],
										"Website:	 	");
								websiteValue = StringUtils.replace(
										websiteValue, ",", " ");
								websiteValue = StringUtils.remove(websiteValue,
										"\r");
							} else if (split[l].contains("Fax:")) {
								faxValue = StringUtils.remove(split[l],
										"Fax:	 ");
								faxValue = StringUtils.replace(faxValue, ",",
										" ");
								faxValue = StringUtils.remove(faxValue, "\r");
							}
						}
					}

					writer.append("Nuremberg");
					writer.append(",");

					writer.append("28 Jan - 2 Feb 2015");
					writer.append(",");

					writer.append(companyName);
					writer.append(",");

					writer.append(boothNumber);
					writer.append(",");

					// Address
					writer.append(addressName);
					writer.append(",");

					// Website
					writer.append(websiteValue);
					writer.append(",");

					// Phone Value
					writer.append(phoneValue);
					writer.append(",");

					// Fax Value
					writer.append(faxValue);
					writer.append(",");

					// Email Value
					writer.append(emailValue);
					writer.append(",");
				}
				writer.append("\n");
			}

			System.out.println("Count Of Vendors:" + count);
			boolean flag = false;
			List<HtmlAnchor> navigationList = page.getAnchors();
			Iterator<?> navigationDetail = navigationList.iterator();
			while (navigationDetail.hasNext()) {
				HtmlElement navigation = (HtmlElement) navigationDetail.next();
				if (navigation.getAttribute("title").equals("Next page")) {
					navigation.click();
					Thread.sleep(10000);
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
