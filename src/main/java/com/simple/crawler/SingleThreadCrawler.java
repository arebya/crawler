package com.simple.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.simple.file.IOUtil;
import com.simple.jsoup.JsoupUtil;

public class SingleThreadCrawler {
	// 贴吧开头地址
	private static String TB = "http://tieba.baidu.com";

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) {
		String id = "2189702487";
		// 贴吧地址
		String tiebaUrl = TB + "/p/" + id;
		try {
			Document doc = JsoupUtil.getDocByUrl(tiebaUrl);
			// 抓取帖子总页数、总帖子数
			Elements header = doc.select(".l_posts_num .red");
			// 页数
			int pageNum = Integer.parseInt(header.get(0).text());
			System.out.println("本贴吧总页数：" + pageNum);
			// 帖子数
			int postNum = Integer.parseInt(header.get(1).text());
			System.out.println("本贴吧总页数：" + postNum);
			for (int i = 1; i <= pageNum; i++) {
				String pageUrl = tiebaUrl + "?pn=" + String.valueOf(i);// 每一页url
				String next = findTieziNextPageUrl(pageUrl, pageNum, id);
				while (!"nomore".equals(next)) {
					next = findTieziNextPageUrl(next, pageNum, id);
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
	public static String findTieziNextPageUrl(String tieziPageUrl, int pageNum,
			String id) {
		String nextUrl = "nomore";// 单页或者尾页
		Document doc = JsoupUtil.getDocByUrl(tieziPageUrl);
		if (null != doc) {
			System.out.println("进入帖子：" + tieziPageUrl);
			// 放入文件
			getMails(doc.toString(), id);
			Elements l_pager = doc.select("li.l_pager a");
			Elements l_tp = doc.select("li.l_pager span");
			String tp = l_tp.get(0).text();
			if (String.valueOf(pageNum).equals(tp)) {
				return nextUrl;
			} else {
				for (Element link : l_pager) {
					String linkHref = link.attr("href");
					String linkText = link.text();
					if ("下一页".equals(linkText)) {
						nextUrl = TB + linkHref;
						break;
					}
				}
				return nextUrl;
			}

		}
		return nextUrl;

	}

	/**
	 * 获取所有邮箱地址，并写入文件
	 * 
	 * @param html
	 */
	public static void getMails(String html, String id) {
		try {
			String regex = "\\w+@\\w+(\\.[a-zA-Z]+)+"; // 匹配邮箱地址
			Pattern p = Pattern.compile(regex); // 把正则封装成对象
			if (null != html) {
				Matcher m = p.matcher(html);
				while (m.find()) {
					IOUtil.writeTofile("D:/tieba/" + id + ".txt", m.group()
							+ ";");// 把获取到的邮箱存到文件中
				}
			}
			System.out.println("收集邮箱地址完成……");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
