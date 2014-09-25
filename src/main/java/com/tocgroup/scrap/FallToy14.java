package com.tocgroup.scrap;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.tocgroup.Jsoup;
import org.tocgroup.Connection.Response;

import com.tocgroup.nodes.Document;
import com.tocgroup.nodes.Element;
import com.tocgroup.nodes.Node;
import com.tocgroup.select.Elements;

public class FallToy14 implements scraber {

	public static void main(String[] args) {
		new FallToy14().extract();
	}

	/**
	 * 
	 */
	public void extract() {
		FileWriter writer = null;
		try {
			// int count = 0;
			long startTime = System.currentTimeMillis();
			String[] schemes = { "http", "https" };
			UrlValidator urlValidator = new UrlValidator(schemes);
			writer = new FileWriter("/home/velmuruganv/Desktop/falltoy14.csv");
			String[] headers = { "Show", "Show Date", "Vendor name", "Address",
					"Phone", "Fax", "Website", "Booth No", "Category" };
			for (String header : headers) {
				writer.append(header);
				writer.append(',');
			}
			writer.append('\n');

			String showUrl = "http://toyfall14.mapyourshow.com/6_0/search/_search-results.cfm?searchType=exhibitor&exhibitor=*&getMoreResults=true&startRow=0&endRow=5000";
			Response execute = Jsoup.connect(showUrl).ignoreContentType(true)
					.timeout(1000000).execute();
			String body = execute.body();
			InputStream inputStream = IOUtils.toInputStream(body, "UTF-8");
			JsonReader createReader = Json.createReader(inputStream);
			JsonObject jsonValue = createReader.readObject().getJsonObject(
					"DATA");
			JsonString jsonObject = jsonValue.getJsonString("BODYHTML");
			Document showDoc = Jsoup.parse(jsonObject.toString());
			Elements links = showDoc.select("a[href*=exhibitor]");
			for (Element link : links) {
				// if (++count == 5)
				// break;
				writer.append("FallToy14");
				writer.append(',');
				writer.append("Oct-7-9:2014 Dallas Market Center");
				writer.append(',');
				String url = link.attr("href");
				System.out.println(url);

				String exhibitorID = StringUtils.substringBetween(url,
						"ExhID=", "&CFID");
				String correctUrl = "http://toyfall14.mapyourshow.com/6_0/includes/includePage.cfm?searchTime=1411559591504&CFID=52981985&CFTOKEN=c2ad7ceb4944a922-8B1472AB-5056-9271-4E8FE51AF8165EDF&loadpage=exhibitor/_exhDetails.cfm&exhid="
						+ exhibitorID;
				Document document = Jsoup.connect(correctUrl).timeout(1000000)
						.get();
				Elements elementsByTag = document.getElementsByTag("h1");
				String vendorName = StringUtils.trim(elementsByTag.text());
				writer.append(StringUtils.replace(vendorName, ",", "."));
				writer.append(',');
				System.out.println(vendorName);
				Elements elementsByTag2 = document.getElementsByTag("p");
				String fullAddress = "";
				String phone = "";
				String fax = "";
				String website = "";
				String boothNo = "";
				for (Element element : elementsByTag2) {
					List<Node> childNodesCopy = element.childNodesCopy();

					for (Node node : childNodesCopy) {
						if (node instanceof com.tocgroup.nodes.TextNode
								&& StringUtils.isNotBlank(node.toString())) {
							String address = StringUtils.replace(StringUtils
									.replace(StringUtils.trim(node.toString()),
											",", "."), "&nbsp;", "& ");
							if (StringUtils.startsWith(address, "P:")) {
								phone = address;
							} else if (StringUtils.startsWith(address, "F:")) {
								fax = address;
							} else {

								fullAddress += address + " ";
							}
							System.out
									.println(StringUtils.trim(node.toString()));
						} else if (node instanceof com.tocgroup.nodes.Element
								&& ((Element) node).tagName().equals("a")) {

							boothNo = StringUtils.replace(StringUtils.replace(
									StringUtils.trim(((Element) node).text()),
									",", "."), "&nbsp;", "& ");
							if (urlValidator.isValid(boothNo)) {
								website = boothNo;
							}
						}

					}

				}
				writer.append(fullAddress);
				writer.append(',');
				writer.append(phone);
				writer.append(',');
				writer.append(fax);
				writer.append(',');
				writer.append(website);
				writer.append(',');
				writer.append(boothNo);
				writer.append(',');
				Elements category = Jsoup
						.connect("http://toyfall14.mapyourshow.com" + url)
						.timeout(1000000).get()
						.getElementsByClass("mys-bullets");
				for (Element bullet : category) {
					String productCategories = StringUtils.trim(bullet.text());
					System.out.println(StringUtils.replace(productCategories,
							",", "."));
					writer.append(StringUtils.replace(productCategories, ",",
							"."));
					writer.append(',');
				}
				writer.append('\n');
			}
			writer.flush();
			writer.close();
			long endTime = System.currentTimeMillis();
			System.out.println("Total time taken to prepare csv "
					+ (endTime - startTime) / 1000 + " Seconds ");
		} catch (Exception e) {

			try {
				writer.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			e.printStackTrace();
		} finally {
		}
	}
}
