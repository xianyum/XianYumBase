/**
 * 时间格式化工具函数
 * @param {Number|String|Date} time - 要格式化的时间（时间戳/Date对象/合法时间字符串）
 */
export const formatTime = (time) => {
    if (!time) return null;
    let date = new Date(time);
    if (isNaN(date.getTime())) {
        date = new Date(time.replace(/-/g, '/'));
    }
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    const second = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
};