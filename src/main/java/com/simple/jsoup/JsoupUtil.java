package com.simple.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.simple.httpclient.CustomerHttpClient;

public class JsoupUtil {

	public static Document getDocByUrl(String url) {
		String tiebaIndexHtml;
		try {
			tiebaIndexHtml = CustomerHttpClient.get(url);
			Document doc = Jsoup.parse(tiebaIndexHtml);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
