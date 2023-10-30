package cn.xianyum.system.controller;

import cn.xianyum.system.service.OssService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2019/10/22 16:09
 */
@RestController
@RequestMapping("/oss")
@Api(tags = "文件oss接口")
@Slf4j
public class OssController {

    @Autowired
    private OssService ossService;

    @RequestMapping(value = "/getImage", produces = "image/jpg", method = RequestMethod.GET)
    public byte[] getImage(@RequestParam("path")String path) {
        try {
            byte[] fr  = ossService.getImage(path);
            return  fr;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
