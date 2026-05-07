package cn.xianyum.extension.controller;

import cn.hutool.core.text.StrPool;
import cn.xianyum.common.annotation.Permission;
import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.utils.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * @author zhangwei
 * @date 2023/9/20 12:37
 */
@RestController
@RequestMapping("xym-extension/v1/cache")
@Tag(name = "缓存接口")
public class CacheController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/getInfo")
    @Operation(summary = "查询Redis缓存基本信息")
    @Permission("@ps.hasPerm('monitor:cache:list')")
    public Results<Map<String, Object>> getInfo() {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info());
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize());

        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(2);
            String property = commandStats.getProperty(key);
            data.put("name", StringUtils.removeStart(key, "cmdstat_"));
            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
            pieList.add(data);
        });
        result.put("commandStats", pieList);
        return Results.success(result);
    }

    @DeleteMapping("/deleteByKey")
    @Operation(summary = "根据Redis Key删除缓存")
    @Permission("@ps.hasPerm('monitor:cache:delete')")
    public Results<Void> deleteByKey(@RequestParam String redisKey) {
        Set<String> keys = new HashSet<>();
        String[] keyArray = StringUtils.split(redisKey, StrPool.COMMA);
        for (String key : keyArray) {
            String trimKey = StringUtils.trim(key);
            if (StringUtils.isNotEmpty(trimKey)) {
                String redisProcessKey = Constants.DEFAULT_REDIS_KEY_PREFIX.concat(trimKey);
                if (redisProcessKey.contains("*")) {
                    // 使用scan命令遍历匹配的key
                    redisTemplate.execute((RedisCallback<Void>) connection -> {
                        try (var cursor = connection.scan(ScanOptions.scanOptions().match(redisProcessKey).count(100).build())) {
                            while (cursor.hasNext()) {
                                keys.add(new String(cursor.next()));
                            }
                        }
                        return null;
                    });
                } else {
                    keys.add(redisProcessKey);
                }
            }
        }
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return Results.success();
    }
}
