package cn.xianyum.system.dao;


import cn.xianyum.system.entity.po.UserEntity;
import cn.xianyum.system.entity.request.UserRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface UserMapper extends BaseMapper<UserEntity> {

    List<UserEntity> queryAll(@Param("user") UserRequest user, Page<UserEntity> page);

    List<UserEntity> getList(UserRequest request);

    int updateUser(UserRequest user);
}
