package com.base.common.utils;


import com.alibaba.excel.support.ExcelTypeEnum;
import com.base.common.constant.EasyExcelParams;
import org.apache.commons.lang.StringUtils;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import org.apache.commons.io.Charsets;

/**
 * EasyExcel工具类
 */
public class EasyExcelUtils {


    /**s
     * 下载EXCEL文件2007版本
     *
     * @throws IOException IO异常
     */
    public static void exportExcel2007Format(EasyExcelParams excelParams) throws IOException {
        exportExcel(excelParams, ExcelTypeEnum.XLSX);
    }

    /**
     * 下载EXCEL文件2003版本
     *
     * @throws IOException IO异常
     */
    public static void exportExcel2003Format(EasyExcelParams excelParams) throws IOException {
        exportExcel(excelParams, ExcelTypeEnum.XLS);
    }

    /**
     * 根据参数和版本枚举导出excel文件
     *
     * @param excelParams 参数实体
     * @param typeEnum    excel类型枚举
     * @throws IOException
     */
    public static void exportExcel(EasyExcelParams excelParams, ExcelTypeEnum typeEnum) throws IOException {
        HttpServletResponse response = excelParams.getResponse();
        ServletOutputStream out = response.getOutputStream();
        ExcelWriter writer = new ExcelWriter(out, typeEnum, excelParams.isNeedHead());
        prepareResponds(response, excelParams.getExcelNameWithoutExt(), typeEnum);
        Sheet sheet1 = new Sheet(1, 0, excelParams.getDataModelClazz());
        if (StringUtils.isNotBlank(excelParams.getSheetName())) {
            sheet1.setSheetName(excelParams.getSheetName());
        }
        writer.write(excelParams.getData(), sheet1);
        writer.finish();
        out.flush();
    }


    /**
     * 将文件输出到浏览器（导出文件）
     *
     * @param response 响应
     * @param fileName 文件名（不含拓展名）
     * @param typeEnum excel类型
     */
    private static void prepareResponds(HttpServletResponse response, String fileName, ExcelTypeEnum typeEnum) {
        String fileName2Export = new String((fileName).getBytes(Charsets.UTF_8), Charsets.ISO_8859_1);
        response.setContentType("multipart/form-data");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName2Export + typeEnum.getValue());
    }

}

