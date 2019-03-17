package com.ajr.webapp.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.ajr.webapp.modal.Vo.OptionVo;
import com.ajr.webapp.service.IOptionService;
import com.ajr.webapp.utils.WebConst;

/**
 * 加载系统配置信息，缓存数据
 * @author Taowd
 * @version 2019年3月9日
 * @see AppRunBefer
 */
@Component
public class AppRunBefer implements ApplicationRunner {

	@Autowired
	private IOptionService optionService;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		if (WebConst.initConfig == null || WebConst.initConfig.size() == 0) {
			// 加载系统配置
			List<OptionVo> voList = optionService.getOptions();
			Map<String, String> options = new HashMap<>();
			voList.forEach((option) -> {
				options.put(option.getName(), option.getValue());
			});
			WebConst.initConfig = options;
		}

	}

}
