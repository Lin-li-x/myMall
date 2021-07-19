package com.llx.mall.service.impl;

import com.llx.mall.common.api.CommonResult;
import com.llx.mall.service.RedisService;
import com.llx.mall.service.UmsMemberService;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class UmsMemberServiceImpl implements UmsMemberService{
	@Autowired
	private RedisService redisService;
	@Value("${redis.key.prefix.authCode}")
	private String REDIS_KEY_PREFIX_AUTH_CODE;
	@Value("${redis.key.expire.authCode}")
	private Long AUTH_CODE_EXPIRE_SECONDS;
	
	@Override
	public CommonResult generateAuthCode(String telephone) {
		StringBuilder str = new StringBuilder();
		Random random = new Random();
		
		for(int i = 0; i < 6; i++) {
			str.append(random.nextInt(10));
		}
		
		redisService.set(REDIS_KEY_PREFIX_AUTH_CODE + telephone, str.toString());
		redisService.expire(REDIS_KEY_PREFIX_AUTH_CODE + telephone, AUTH_CODE_EXPIRE_SECONDS);
		
		return CommonResult.success(str.toString(), "宸插彂閫侀獙璇佺爜");
		
	}

	@Override
	public CommonResult verifyAuthCode(String telephone, String authCode) {
		if(authCode.isEmpty()) {
			return CommonResult.failed("璇疯緭鍏ラ獙璇佺爜");
		}
		
		String realAuthCode = redisService.get(REDIS_KEY_PREFIX_AUTH_CODE + telephone);
		boolean result = authCode.equals(realAuthCode);
		
		if(result) {
			return CommonResult.success(null, "楠岃瘉鐮佹牎楠屾垚鍔�");
		}else {
			return CommonResult.failed("楠岃瘉鐮佷笉姝ｇ‘");
		}
	}
	
}
