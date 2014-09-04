package org.jsoup.examples;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TradeShowScraper2 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		final WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		final HtmlPage page = webClient
				.getPage("http://s23.a2zinc.net/clients/TIA/ToyFair2015/Public/EventMap.aspx");
		List<?> byXPath = page
				.getByXPath("//*[@id=\"ctl00_ctl00_cph1_cph1_ucExhibitorList_dockExhibitorList_C_radExhibitorList_ctl00__4\"]/td[2]/div");
		// webClient.closeAllWindows();

	}
}
