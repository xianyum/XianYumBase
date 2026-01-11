/**
 * 时间格式化工具函数（最终稳定版）
 * @param {Number|String|Date} time - 要格式化的时间
 * @param {String} format - 格式化字符串，默认：{y}-{m}-{d} {h}:{i}:{s}
 * @returns {String} 格式化后的时间（空值返回空字符串）
 */
export const formatTime = (time, format = '{y}-{m}-{d} {h}:{i}:{s}') => {
    // 空值直接返回空字符串（避免显示null）
    if (!time) return '';

    let date;
    // 处理10位时间戳（秒级）→ 转为13位（毫秒级）
    if (typeof time === 'number' && time.toString().length === 10) {
        time = time * 1000;
    }

    // 初始化Date对象
    try {
        date = new Date(time);
        // 兼容iOS不识别'-'的情况
        if (isNaN(date.getTime()) && typeof time === 'string') {
            date = new Date(time.replace(/-/g, '/'));
        }
    } catch (e) {
        return ''; // 解析失败返回空
    }

    // 解析失败兜底
    if (isNaN(date.getTime())) return '';

    // 提取时间分量（补零）
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    const second = String(date.getSeconds()).padStart(2, '0');

    // 逐个替换占位符
    return format
        .replace('{y}', year)
        .replace('{m}', month)
        .replace('{d}', day)
        .replace('{h}', hour)
        .replace('{i}', minute)
        .replace('{s}', second);
};