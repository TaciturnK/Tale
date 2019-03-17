package com.ajr.webapp.controller.admin;

import com.ajr.webapp.controller.BaseController;
import com.ajr.webapp.dto.LogActions;
import com.ajr.webapp.dto.Types;
import com.ajr.webapp.exception.TipException;
import com.ajr.webapp.modal.Bo.RestResponseBo;
import com.ajr.webapp.modal.Vo.ContentVo;
import com.ajr.webapp.modal.Vo.ContentVoExample;
import com.ajr.webapp.modal.Vo.MetaVo;
import com.ajr.webapp.modal.Vo.UserVo;
import com.ajr.webapp.service.IContentService;
import com.ajr.webapp.service.ILogService;
import com.ajr.webapp.service.IMetaService;
import com.github.pagehelper.PageInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * By Taowd .
 */
@Controller
@RequestMapping("/admin/article")
@Transactional(rollbackFor = TipException.class)
public class ArticleController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

	@Resource
	private IContentService contentsService;

	@Resource
	private IMetaService metasService;

	@Resource
	private ILogService logService;

	@GetMapping(value = "")
	public String index(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "15") int limit,
			HttpServletRequest request) {
		ContentVoExample contentVoExample = new ContentVoExample();
		contentVoExample.setOrderByClause("created desc");
		contentVoExample.createCriteria().andTypeEqualTo(Types.ARTICLE.getType());
		PageInfo<ContentVo> contentsPaginator = contentsService
				.getArticlesWithpage(contentVoExample, page, limit);
		request.setAttribute("articles", contentsPaginator);
		return "admin/article_list";
	}

	@GetMapping(value = "/publish")
	public String newArticle(HttpServletRequest request) {
		List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
		request.setAttribute("categories", categories);
		return "admin/article_edit";
	}

	@GetMapping(value = "/{cid}")
	public String editArticle(@PathVariable String cid, HttpServletRequest request) {
		ContentVo contents = contentsService.getContents(cid);
		request.setAttribute("contents", contents);
		List<MetaVo> categories = metasService.getMetas(Types.CATEGORY.getType());
		request.setAttribute("categories", categories);
		request.setAttribute("active", "article");
		return "admin/article_edit";
	}

	@PostMapping(value = "/publish")
	@ResponseBody
	@Transactional(rollbackFor = TipException.class)
	public RestResponseBo<?> publishArticle(ContentVo contents, HttpServletRequest request) {
		UserVo users = this.user(request);
		contents.setAuthorId(users.getUid());
		contents.setType(Types.ARTICLE.getType());
		if (StringUtils.isBlank(contents.getCategories())) {
			contents.setCategories("默认分类");
		}
		try {
			contentsService.publish(contents);
		} catch (Exception e) {
			String msg = "文章发布失败";
			if (e instanceof TipException) {
				msg = e.getMessage();
			} else {
				LOGGER.error(msg, e);
			}
			return RestResponseBo.fail(msg);
		}
		return RestResponseBo.ok();
	}

	@PostMapping(value = "/modify")
	@ResponseBody
	@Transactional(rollbackFor = TipException.class)
	public RestResponseBo<?> modifyArticle(ContentVo contents, HttpServletRequest request) {
		UserVo users = this.user(request);
		contents.setAuthorId(users.getUid());
		contents.setType(Types.ARTICLE.getType());
		try {
			contentsService.updateArticle(contents);
		} catch (Exception e) {
			String msg = "文章编辑失败";
			if (e instanceof TipException) {
				msg = e.getMessage();
			} else {
				LOGGER.error(msg, e);
			}
			return RestResponseBo.fail(msg);
		}
		return RestResponseBo.ok();
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	@Transactional(rollbackFor = TipException.class)
	public RestResponseBo<?> delete(@RequestParam int cid, HttpServletRequest request) {
		try {
			contentsService.deleteByCid(cid);
			logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid + "",
					request.getRemoteAddr(), this.getUid(request));
		} catch (Exception e) {
			String msg = "文章删除失败";
			if (e instanceof TipException) {
				msg = e.getMessage();
			} else {
				LOGGER.error(msg, e);
			}
			return RestResponseBo.fail(msg);
		}
		return RestResponseBo.ok();
	}
}