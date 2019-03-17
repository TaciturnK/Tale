package com.ajr.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ajr.webapp.dao.CommentVoMapper;
import com.ajr.webapp.exception.TipException;
import com.ajr.webapp.modal.Bo.CommentBo;
import com.ajr.webapp.modal.Vo.CommentVo;
import com.ajr.webapp.modal.Vo.CommentVoExample;
import com.ajr.webapp.modal.Vo.ContentVo;
import com.ajr.webapp.service.ICommentService;
import com.ajr.webapp.service.IContentService;
import com.ajr.webapp.utils.DateKit;
import com.ajr.webapp.utils.TaleUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * Created by BlueT on 2017/3/16.
 */
@Service
public class CommentServiceImpl implements ICommentService {

	@Resource
	private CommentVoMapper commentDao;

	@Resource
	private IContentService contentService;

	@Override
	public void insertComment(CommentVo comments) {
		if (null == comments) {
			throw new TipException("评论对象为空");
		}
		if (StringUtils.isBlank(comments.getAuthor())) {
			comments.setAuthor("热心网友");
		}
		if (StringUtils.isNotBlank(comments.getMail()) && !TaleUtils.isEmail(comments.getMail())) {
			throw new TipException("请输入正确的邮箱格式");
		}
		if (StringUtils.isBlank(comments.getContent())) {
			throw new TipException("评论内容不能为空");
		}
		if (comments.getContent().length() < 5 || comments.getContent().length() > 2000) {
			throw new TipException("评论字数在5-2000个字符");
		}
		if (null == comments.getCid()) {
			throw new TipException("评论文章不能为空");
		}
		ContentVo contents = contentService.getContents(String.valueOf(comments.getCid()));
		if (null == contents) {
			throw new TipException("不存在的文章");
		}
		comments.setOwnerId(contents.getAuthorId());
		comments.setCreated(DateKit.getCurrentUnixTime());
		commentDao.insertSelective(comments);

		ContentVo temp = new ContentVo();
		temp.setCid(contents.getCid());
		temp.setCommentsNum(contents.getCommentsNum() + 1);
		contentService.updateContentByCid(temp);
	}

	@Override
	public PageInfo<CommentBo> getComments(Integer cid, int page, int limit) {

		if (null != cid) {
			PageHelper.startPage(page, limit);
			CommentVoExample commentVoExample = new CommentVoExample();
			commentVoExample.createCriteria().andCidEqualTo(cid).andParentEqualTo(0);
			commentVoExample.setOrderByClause("coid desc");
			List<CommentVo> parents = commentDao.selectByExampleWithBLOBs(commentVoExample);
			PageInfo<CommentVo> commentPaginator = new PageInfo<>(parents);
			PageInfo<CommentBo> returnBo = copyPageInfo(commentPaginator);
			if (parents.size() != 0) {
				List<CommentBo> comments = new ArrayList<>(parents.size());
				parents.forEach(parent -> {
					CommentBo comment = new CommentBo(parent);
					comments.add(comment);
				});
				returnBo.setList(comments);
			}
			return returnBo;
		}
		return null;
	}

	@Override
	public PageInfo<CommentVo> getCommentsWithPage(CommentVoExample commentVoExample, int page,
			int limit) {
		PageHelper.startPage(page, limit);
		List<CommentVo> commentVos = commentDao.selectByExampleWithBLOBs(commentVoExample);
		PageInfo<CommentVo> pageInfo = new PageInfo<>(commentVos);
		return pageInfo;
	}

	@Override
	public void update(CommentVo comments) {
		if (null != comments && null != comments.getCoid()) {
			commentDao.updateByPrimaryKeyWithBLOBs(comments);
		}
	}

	@Override
	public void delete(Integer coid, Integer cid) {
		if (null == coid) {
			throw new TipException("主键为空");
		}
		commentDao.deleteByPrimaryKey(coid);
		ContentVo contents = contentService.getContents(cid + "");
		if (null != contents && contents.getCommentsNum() > 0) {
			ContentVo temp = new ContentVo();
			temp.setCid(cid);
			temp.setCommentsNum(contents.getCommentsNum() - 1);
			contentService.updateContentByCid(temp);
		}
	}

	@Override
	public CommentVo getCommentById(Integer coid) {
		if (null != coid) {
			return commentDao.selectByPrimaryKey(coid);
		}
		return null;
	}

	/**
	 * copy原有的分页信息，除数据
	 * @param ordinal
	 * @param         <T>
	 * @return
	 */
	private <T> PageInfo<T> copyPageInfo(PageInfo<?> ordinal) {
		PageInfo<T> returnBo = new PageInfo<T>();
		returnBo.setPageSize(ordinal.getPageSize());
		returnBo.setPageNum(ordinal.getPageNum());
		returnBo.setEndRow(ordinal.getEndRow());
		returnBo.setTotal(ordinal.getTotal());
		returnBo.setHasNextPage(ordinal.isHasNextPage());
		returnBo.setHasPreviousPage(ordinal.isHasPreviousPage());
		returnBo.setIsFirstPage(ordinal.isIsFirstPage());
		returnBo.setIsLastPage(ordinal.isIsLastPage());
		returnBo.setNavigateFirstPage(ordinal.getNavigateFirstPage());
		returnBo.setNavigateLastPage(ordinal.getNavigateLastPage());
		returnBo.setNavigatepageNums(ordinal.getNavigatepageNums());
		returnBo.setSize(ordinal.getSize());
		returnBo.setPrePage(ordinal.getPrePage());
		returnBo.setNextPage(ordinal.getNextPage());
		return returnBo;
	}
}