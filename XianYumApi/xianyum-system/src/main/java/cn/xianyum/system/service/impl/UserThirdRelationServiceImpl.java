package cn.xianyum.system.service.impl;

import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.system.entity.po.UserThirdRelationEntity;
import cn.xianyum.system.entity.response.UserThirdRelationResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import cn.xianyum.system.service.UserThirdRelationService;
import cn.xianyum.system.dao.UserThirdRelationMapper;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * (UserThirdRelation)service层实现
 *
 * @author zhangwei
 * @since 2024-03-05 15:03:57
 */
@Service
@Slf4j
public class UserThirdRelationServiceImpl implements UserThirdRelationService {

	@Autowired
	private UserThirdRelationMapper userThirdRelationMapper;


	/**
	 * 解除绑定
	 * @param id
	 * @return
	 */
	@Override
	public Integer unbind(String id) {
		return userThirdRelationMapper.deleteById(id);
	}

	@Override
	public List<UserThirdRelationResponse> getCurrentUserThirdRelation() {
		LambdaQueryWrapper<UserThirdRelationEntity> lambdaQueryWrapper = Wrappers.<UserThirdRelationEntity>lambdaQuery()
				.eq(UserThirdRelationEntity::getUserId, SecurityUtils.getLoginUser().getId())
				.orderByDesc(UserThirdRelationEntity::getCreateTime);
		List<UserThirdRelationEntity> userThirdRelationEntities = userThirdRelationMapper.selectList(lambdaQueryWrapper);
		return BeanUtils.copyList(userThirdRelationEntities,UserThirdRelationResponse.class);
	}
}

