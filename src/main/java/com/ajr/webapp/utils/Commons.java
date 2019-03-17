package com.ajr.webapp.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ajr.webapp.dto.MetaDto;
import com.ajr.webapp.dto.Types;
import com.ajr.webapp.modal.Vo.CommentVo;
import com.ajr.webapp.modal.Vo.ContentVo;
import com.ajr.webapp.service.ISiteService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.vdurmont.emoji.EmojiParser;

/**
 * 主题公共函数
 * <p>
 * By Taowd .
 */
@Component
public final class Commons {

	private static ISiteService siteService;

	/**
	 * 主题键.
	 */
	private static final String SITE_THEME = "site_theme";

	public static void setSiteService(ISiteService ss) {
		siteService = ss;
	}

	/**
	 * 判断分页中是否有数据
	 * @param paginator
	 * @return
	 */
	public static boolean is_empty(PageInfo<?> paginator) {
		return paginator == null || (paginator.getList() == null)
				|| (paginator.getList().size() == 0);
	}

	/**
	 * 网站链接
	 * @return
	 */
	public static String site_url() {
		return site_url("");
	}

	/**
	 * 获取附件地址.
	 * @param fkey
	 * @return
	 */
	public static String get_attach_url(String fkey) {
		// return site_option("site_url") + fkey;
		return fkey;
	}

	/**
	 * 返回网站链接下的全址
	 * @param sub 后面追加的地址
	 * @return
	 */
	public static String site_url(String sub) {

		// 取出主题并进行拼接地址
		String site_theme = WebConst.initConfig.get(SITE_THEME);
		if (StringUtils.isBlank(sub)) {
			// 返回网站首页
			return site_option("site_url") + "/";
		} else {
			return site_option("site_url") + "/" + site_theme + sub;
		}
	}

	/**
	 * 网站标题
	 * @return
	 */
	public static String site_title() {
		return site_option("site_title");
	}

	/**
	 * 网站配置项
	 * @param key
	 * @return
	 */
	public static String site_option(String key) {
		return site_option(key, "");
	}

	/**
	 * 网站配置项
	 * @param key
	 * @param defalutValue 默认值
	 * @return
	 */
	public static String site_option(String key, String defalutValue) {
		if (StringUtils.isBlank(key)) {
			return "";
		}
		String str = WebConst.initConfig.get(key);
		if (StringUtils.isNotBlank(str)) {
			return str;
		} else {
			return defalutValue;
		}
	}

	/**
	 * 截取字符串
	 * @param str
	 * @param len
	 * @return
	 */
	public static String substr(String str, int len) {
		if (str.length() > len) {
			return str.substring(0, len);
		}
		return str;
	}

	/**
	 * 返回github头像地址
	 * @param email
	 * @return
	 */
	public static String gravatar(String email) {
		// String avatarUrl = "https://github.com/identicons/";
		// if (StringUtils.isBlank(email)) {
		// email = "taowd@outlook.com";
		// }
		// String hash = TaleUtils.MD5encode(email.trim().toLowerCase());
		// return avatarUrl + hash + ".png";
		return "https://avatars0.githubusercontent.com/u/23622072?s=40&u=c617e91650b6456db27167c74c76ff616af532f6&v=4";
	}

	/**
	 * 返回文章链接地址
	 * @param contents
	 * @return
	 */
	public static String permalink(ContentVo contents) {
		return permalink(contents.getCid(), contents.getSlug());
	}

	/**
	 * 获取随机数
	 * @param max
	 * @param str
	 * @return
	 */
	public static String random(int max, String str) {
		return UUID.random(1, max) + str;
	}

	/**
	 * 返回文章链接地址
	 * @param cid
	 * @param slug
	 * @return
	 */
	public static String permalink(Integer cid, String slug) {
		return site_url("/article/" + (StringUtils.isNotBlank(slug) ? slug : cid.toString()));
	}

	/**
	 * 格式化unix时间戳为日期
	 * @param unixTime
	 * @return
	 */
	public static String fmtdate(Integer unixTime) {
		return fmtdate(unixTime, "yyyy-MM-dd");
	}

	/**
	 * 格式化unix时间戳为日期
	 * @param unixTime
	 * @param patten
	 * @return
	 */
	public static String fmtdate(Integer unixTime, String patten) {
		if (null != unixTime && StringUtils.isNotBlank(patten)) {
			return DateKit.formatDateByUnixTime(unixTime, patten);
		}
		return "";
	}

	/**
	 * 显示分类
	 * @param categories
	 * @return
	 */
	public static String show_categories(String categories) throws UnsupportedEncodingException {
		// 取出主题并进行拼接地址
		String site_theme = WebConst.initConfig.get(SITE_THEME);
		if (StringUtils.isNotBlank(categories)) {
			String[] arr = categories.split(",");
			StringBuffer sbuf = new StringBuffer();
			for (String c : arr) {
				sbuf.append("<a href=\"/" + site_theme + "/category/"
						+ URLEncoder.encode(c, "UTF-8") + "\">" + c + "</a>");
			}
			return sbuf.toString();
		}
		return show_categories("默认分类");
	}

	/**
	 * 显示标签
	 * @param tags
	 * @return
	 */
	public static String show_tags(String tags) throws UnsupportedEncodingException {
		// 取出主题并进行拼接地址
		String site_theme = WebConst.initConfig.get(SITE_THEME);
		if (StringUtils.isNotBlank(tags)) {
			String[] arr = tags.split(",");
			StringBuffer sbuf = new StringBuffer();
			for (String c : arr) {
				sbuf.append("<a href=\"/" + site_theme + "/tag/" + URLEncoder.encode(c, "UTF-8")
						+ "\">" + c + "</a>");
			}
			return sbuf.toString();
		}
		return "";
	}

	/**
	 * 截取文章摘要
	 * @param value 文章内容
	 * @param len   要截取文字的个数
	 * @return
	 */
	public static String intro(String value, int len) {
		int pos = value.indexOf("<!--more-->");
		if (pos != -1) {
			String html = value.substring(0, pos);
			return TaleUtils.htmlToText(TaleUtils.mdToHtml(html));
		} else {
			String text = TaleUtils.htmlToText(TaleUtils.mdToHtml(value));
			if (text.length() > len) {
				return text.substring(0, len);
			}
			return text;
		}
	}

	/**
	 * 显示文章内容，转换markdown为html
	 * @param value
	 * @return
	 */
	public static String article(String value) {
		if (StringUtils.isNotBlank(value)) {
			value = value.replace("<!--more-->", "\r\n");
			return TaleUtils.mdToHtml(value);
		}
		return "";
	}

	/**
	 * 显示文章缩略图，顺序为：文章第一张图 -> 随机获取
	 * @return
	 */
	public static String show_thumb(ContentVo contents) {
		int cid = contents.getCid();
		int size = cid % 50;
		size = size == 0 ? 1 : size;
		return "/user/img/rand/" + size + ".jpg";
	}

	/**
	 * 最新文章
	 * @param limit
	 * @return
	 */
	public static List<ContentVo> recent_articles(int limit) {
		if (null == siteService) {
			return Lists.newArrayList();
		}
		return siteService.recentContents(limit);
	}

	/**
	 * 最新评论
	 * @param limit
	 * @return
	 */
	public static List<CommentVo> recent_comments(int limit) {
		if (null == siteService) {
			return Lists.newArrayList();
		}
		return siteService.recentComments(limit);
	}

	/**
	 * 获取分类列表
	 * @return
	 */
	public static List<MetaDto> categries(int limit) {
		return siteService.metas(Types.CATEGORY.getType(), null, limit);
	}

	/**
	 * 获取所有分类
	 * @return
	 */
	public static List<MetaDto> categries() {
		return categries(WebConst.MAX_POSTS);
	}

	/**
	 * 获取标签列表
	 * @return
	 */
	public static List<MetaDto> tags(int limit) {
		return siteService.metas(Types.TAG.getType(), null, limit);
	}

	/**
	 * 获取所有标签
	 * @return
	 */
	public static List<MetaDto> tags() {
		return tags(WebConst.MAX_POSTS);
	}

	/**
	 * 获取评论at信息
	 * @param coid
	 * @return
	 */
	public static String comment_at(Integer coid) {
		CommentVo comments = siteService.getComment(coid);
		if (null != comments) {
			return "<a href=\"#comment-" + coid + "\">@" + comments.getAuthor() + "</a>";
		}
		return "";
	}

	/**
	 * An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!
	 * <p>
	 * 这种格式的字符转换为emoji表情
	 * @param value
	 * @return
	 */
	public static String emoji(String value) {
		return EmojiParser.parseToUnicode(value);
	}

	/**
	 * 获取文章第一张图片
	 * @return
	 */
	public static String show_thumb(String content) {
		content = TaleUtils.mdToHtml(content);
		if (content.contains("<img")) {
			String img = "";
			String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
			Pattern p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
			Matcher m_image = p_image.matcher(content);
			if (m_image.find()) {
				img = img + "," + m_image.group();
				// //匹配src
				Matcher m = Pattern.compile("src\\s*=\\s*\'?\"?(.*?)(\'|\"|>|\\s+)").matcher(img);
				if (m.find()) {
					return m.group(1);
				}
			}
		}
		return "";
	}

	private static final String[] ICONS = { "bg-ico-book", "bg-ico-game", "bg-ico-note",
			"bg-ico-chat", "bg-ico-code", "bg-ico-image", "bg-ico-web", "bg-ico-link",
			"bg-ico-design", "bg-ico-lock" };

	/**
	 * 显示文章图标
	 * @param cid
	 * @return
	 */
	public static String show_icon(int cid) {
		return ICONS[cid % ICONS.length];
	}

	/**
	 * 获取社交的链接地址
	 * @return
	 */
	public static Map<String, String> social() {
		final String prefix = "social_";
		Map<String, String> map = new HashMap<>();
		map.put("weibo", WebConst.initConfig.get(prefix + "weibo"));
		map.put("zhihu", WebConst.initConfig.get(prefix + "zhihu"));
		map.put("github", WebConst.initConfig.get(prefix + "github"));
		map.put("twitter", WebConst.initConfig.get(prefix + "twitter"));
		return map;
	}

}
