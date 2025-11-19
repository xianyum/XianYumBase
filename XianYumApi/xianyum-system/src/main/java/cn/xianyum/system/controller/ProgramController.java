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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author zhangwei
 * @date 2020/11/20 20:29
 */
@RestController
@RequestMapping("xym-system/v1/program")
@Tag(name = "程序设计接口")
@Slf4j
public class ProgramController {

    @Autowired
    private ProgramService programService;

    /**
     * 所有用户列表
     */
    @PostMapping("/getPage")
    @Operation(summary = "获取程序设计列表")
    public Results getPage(@RequestBody ProgramRequest request){
        PageResponse<ProgramResponse> list = programService.getPage(request);
        return Results.page(list);
    }


    /**
     * 根据Id查询用户
     */
    @PostMapping("/selectById")
    @Operation(summary = "根据Id查询程序设计")
    public Results selectOneById(@RequestBody ProgramRequest request){
        ProgramEntity info = programService.selectOneById(request);
        return Results.success(info);
    }

    @PostMapping("/save")
    @Operation(summary = "保存程序记录操作")
    public Results save(@RequestBody ProgramRequest request){
        int count = programService.save(request);
        if(count>0){
            return Results.success();
        }else {
            return Results.error("保存程序记录失败！");
        }
    }


    @PostMapping("/update")
    @Operation(summary = "更新程序记录操作")
    public Results update(@RequestBody ProgramRequest request){
        int count = programService.update(request);
        if(count>0){
            return Results.success();
        }else {
            return Results.error("更新程序记录失败！");
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除程序操作")
    public Results delete(@RequestBody String[] ids){
        try {
            programService.deleteById(ids);
            return Results.success();
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }


    @PostMapping("/complete")
    @Operation(summary = "完成程序订单操作")
    public Results complete(@RequestBody ProgramRequest request){
        try {
            programService.complete(request);
            return Results.success();
        }catch(SoException exception){
            return Results.error(exception.getMsg());
        }
    }


    @PostMapping("/schedule")
    @Operation(summary = "查询订单实时进度")
    public Results schedule(@RequestBody ProgramRequest request){
        List<JSONObject> info = programService.schedule(request);
        return Results.success(info);
    }


    @PostMapping("/export")
    @Operation(summary = "导出程序订单列表")
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
