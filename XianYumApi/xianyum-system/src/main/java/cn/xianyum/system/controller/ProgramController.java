package cn.xianyum.system.controller;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.Results;
import cn.xianyum.system.entity.po.ExportProgramEntity;
import cn.xianyum.system.entity.po.ProgramEntity;
import cn.xianyum.system.entity.request.ProgramRequest;
import cn.xianyum.system.entity.response.ProgramResponse;
import cn.xianyum.system.service.ProgramService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author zhangwei
 * @date 2020/11/20 20:29
 */
@RestController
@RequestMapping("xianyum-system/v1/program")
@Api(tags = "程序设计接口")
@Slf4j
public class ProgramController {

    @Autowired
    private ProgramService programService;

    /**
     * 所有用户列表
     */
    @PostMapping("/getPage")
    @ApiOperation(value = "获取程序设计列表", httpMethod = "POST")
    public Results getPage(@RequestBody ProgramRequest request){
        PageResponse<ProgramResponse> list = programService.getPage(request);
        return Results.page(list);
    }


    /**
     * 根据Id查询用户
     */
    @PostMapping("/selectById")
    @ApiOperation(value = "根据Id查询程序设计", httpMethod = "POST")
    public Results selectOneById(@RequestBody ProgramRequest request){
        ProgramEntity info = programService.selectOneById(request);
        return Results.success(info);
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存程序记录操作", httpMethod = "POST")
    public Results save(@RequestBody ProgramRequest request){
        int count = programService.save(request);
        if(count>0){
            return Results.success();
        }else {
            return Results.error("保存程序记录失败！");
        }
    }


    @PostMapping("/update")
    @ApiOperation(value = "更新程序记录操作", httpMethod = "POST")
    public Results update(@RequestBody ProgramRequest request){
        int count = programService.update(request);
        if(count>0){
            return Results.success();
        }else {
            return Results.error("更新程序记录失败！");
        }
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除程序操作", httpMethod = "POST")
    public Results delete(@RequestBody String[] ids){
        try {
            programService.deleteById(ids);
            return Results.success();
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }


    @PostMapping("/complete")
    @ApiOperation(value = "完成程序订单操作", httpMethod = "POST")
    public Results complete(@RequestBody ProgramRequest request){
        try {
            programService.complete(request);
            return Results.success();
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }


    @PostMapping("/schedule")
    @ApiOperation(value = "查询订单实时进度", httpMethod = "POST")
    public Results schedule(@RequestBody ProgramRequest request){
        List<JSONObject> info = programService.schedule(request);
        return Results.success(info);
    }


    @PostMapping("/export")
    @ApiOperation(value = "导出程序订单列表", httpMethod = "POST")
    public void export(@RequestBody ProgramRequest request, HttpServletResponse response){
        ExcelWriter excelWriter = null;
        try {
            PageResponse<ProgramResponse> list = programService.getPage(request);
            response.setContentType("application/vnd.ms-exce");
            response.setCharacterEncoding("utf-8");
            response.addHeader("Access-Control-Expose-Headers","filename");
            String fileName= "程序申请单报表-"+System.currentTimeMillis();
            String encode = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("filename", encode);
            excelWriter = EasyExcel.write(response.getOutputStream(), ExportProgramEntity.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            excelWriter.write(BeanUtils.copyList(list.getDataList(), ExportProgramEntity.class),writeSheet);
        }catch (Exception e){
            throw new SoException("导出失败");
        }finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }
}
