package com.my.blog.website.service;

import com.my.blog.website.modal.Vo.OptionVo;

import java.util.List;
import java.util.Map;

/**
 * 系统设置
 * @author Taowd
 * @version 2019年3月7日
 * @see IOptionService
 */
public interface IOptionService {

	void insertOption(OptionVo optionVo);

	void insertOption(String name, String value);

	List<OptionVo> getOptions();

	/**
	 * 保存一组配置
	 * @param options
	 */
	void saveOptions(Map<String, String> options);

	OptionVo getOptionByName(String name);
}
