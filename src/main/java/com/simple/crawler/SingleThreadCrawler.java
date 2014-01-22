package com.simple.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.simple.file.IOUtil;
import com.simple.httpclient.CustomerHttpClient;
import com.simple.jsoup.JsoupUtil;

public class SingleThreadCrawler {
	// 贴吧开头地址
	private static String TB = "http://tieba.baidu.com";

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) {
		// 贴吧地址
		String tiebaUrl = TB + "/p/2036977261?pn=19";
		try {
			Document doc = JsoupUtil.getDocByUrl(tiebaUrl);
			// 抓取帖子总页数、总帖子数
			Elements footer = doc.select(".th_footer_l .red");
			// 获取第一个元素，即帖子总数
			int tieziNum = Integer.parseInt(footer.get(0).text());
			// 获取分页数
			int pageNum = 0;
			if (tieziNum % 50 == 0) {
				pageNum = tieziNum / 50;
			} else {
				pageNum = 1 + tieziNum / 50;
			}
			System.out.println("本贴吧总页数：" + pageNum);
			// 存放所有帖子集合
			List<String> tieziList = new ArrayList<String>();

			for (int i = 0; i < pageNum; i++) {

				String pageUrl = tiebaUrl + "&pn=" + String.valueOf(i * 50);// 每一页url
				String pageHtml = CustomerHttpClient.get(pageUrl);
				Document pageDoc = Jsoup.parse(pageHtml);
				Elements ties = pageDoc.select("a.j_th_tit");
				for (Element link : ties) {
					String linkHref = link.attr("href");
					tieziList.add(linkHref);
				}

			}
			System.out.println("遍历到的有效帖子总数：" + tieziList.size());
			if (null != tieziList && tieziList.size() > 0) {
				for (int i = 0; i < tieziList.size(); i++) {
					String tieziUrl = TB + (String) tieziList.get(i);
					String nextTieziUrl = findTieziNextPageUrl(tieziUrl);
					while (!"nomore".equals(nextTieziUrl)) {
						nextTieziUrl = findTieziNextPageUrl(nextTieziUrl);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取帖子“下一页”地址，并将当页的邮箱地址写入文件
	 * 
	 * @param tieziPageUrl
	 * @return
	 */
	public static String findTieziNextPageUrl(String tieziPageUrl) {
		String nextUrl = "nomore";// 单页或者尾页
		Document doc = JsoupUtil.getDocByUrl(tieziPageUrl);
		if (null != doc) {
			System.out.println("进入帖子：" + tieziPageUrl);
			// 放入文件
			getMails(doc.toString());

			Elements l_pager = doc.select("li.l_pager a");
			for (Element link : l_pager) {
				String linkHref = link.attr("href");
				String linkText = link.text();
				if ("下一页".equals(linkText)) {
					nextUrl = TB + linkHref;
				}
			}
		}

		return nextUrl;
	}

	/**
	 * 获取所有邮箱地址，并写入文件
	 * 
	 * @param html
	 */
	public static void getMails(String html) {
		try {
			String regex = "\\w+@\\w+(\\.[a-zA-Z]+)+"; // 匹配邮箱地址
			Pattern p = Pattern.compile(regex); // 把正则封装成对象
			if (null != html) {
				Matcher m = p.matcher(html);
				while (m.find()) {
					IOUtil.writeTofile("mails.txt", m.group() + ";");// 把获取到的邮箱存到文件中
				}
			}
			System.out.println("收集邮箱地址完成……");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
