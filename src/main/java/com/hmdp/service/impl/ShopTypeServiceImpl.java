package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {

        // 1.从redis中查询
        String key = RedisConstants.CACHE_TYPE_KEY;
        String typeJson = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotEmpty(typeJson)) {
            // 3.存在，直接返回
            List<ShopType> typeList = JSONUtil.toList(typeJson, ShopType.class);
            return  Result.ok(typeList);
        }

        // 4.不存在，从数据库中查询
        List<ShopType> typeList = query().orderByAsc("sort").list();

        // 5.不存在，返回错误信息
        if (typeList == null) {
            return Result.fail("店铺类别不存在！");
        }

        // 6.存在，保存到redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(typeList));

        // 7.返回
        return Result.ok(typeList);
    }
}
