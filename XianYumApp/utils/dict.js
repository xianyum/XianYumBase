import { getDicts } from "@/api/system/dict/data";

// 字典缓存池，用于缓存已获取的字典数据，避免重复请求
const dictCache = {};

/**
 * 获取字典数据（带缓存）
 * @param {String|Array} dictTypes - 字典类型（单个字符串或字符串数组）
 * @returns {Promise<Object|Array>} - 返回字典对象（key: 字典类型, value: 字典数组）或单个字典数组
 */
export const getDictData = async (dictTypes) => {
    // 处理单个字典类型的情况
    const isSingleType = typeof dictTypes === 'string';
    const types = isSingleType ? [dictTypes] : [...dictTypes];

    // 筛选出未缓存的字典类型
    const needFetchTypes = types.filter(type => !dictCache[type]);

    // 如果有需要请求的字典类型，发起请求
    if (needFetchTypes.length > 0) {
        try {
            // 批量请求字典（如果接口支持批量，可优化为一次请求；否则循环请求）
            for (const type of needFetchTypes) {
                const response = await getDicts(type);
                dictCache[type] = response.data || [];
            }
        } catch (error) {
            console.error(`获取字典数据失败: ${needFetchTypes.join(', ')}`, error);
            // 失败时初始化缓存，避免重复请求失败的字典
            needFetchTypes.forEach(type => {
                dictCache[type] = [];
            });
        }
    }

    // 返回结果（单个类型返回数组，多个类型返回对象）
    if (isSingleType) {
        return dictCache[dictTypes];
    } else {
        const result = {};
        types.forEach(type => {
            result[type] = dictCache[type];
        });
        return result;
    }
};

/**
 * 根据字典类型和值获取字典标签
 * @param {String} dictType - 字典类型
 * @param {String|Number} dictValue - 字典值
 * @param {String} defaultValue - 默认值（找不到时返回）
 * @returns {String} - 字典标签
 */
export const getDictLabel = (dictType, dictValue, defaultValue = '') => {
    // 先检查缓存中是否有该字典
    if (!dictCache[dictType]) {
        console.warn(`字典类型 ${dictType} 未缓存，请先调用 getDictData 获取`);
        return defaultValue || dictValue;
    }

    // 查找对应的字典项
    const dictItem = dictCache[dictType].find(
        item => item.dictValue === String(dictValue) // 统一转为字符串对比，避免类型问题
    );

    return dictItem ? dictItem.dictLabel : (defaultValue || dictValue);
};

/**
 * 根据字典类型和标签获取字典值
 * @param {String} dictType - 字典类型
 * @param {String} dictLabel - 字典标签
 * @param {String|Number} defaultValue - 默认值（找不到时返回）
 * @returns {String|Number} - 字典值
 */
export const getDictValue = (dictType, dictLabel, defaultValue = '') => {
    if (!dictCache[dictType]) {
        console.warn(`字典类型 ${dictType} 未缓存，请先调用 getDictData 获取`);
        return defaultValue || dictLabel;
    }

    const dictItem = dictCache[dictType].find(item => item.dictLabel === dictLabel);

    return dictItem ? dictItem.dictValue : (defaultValue || dictLabel);
};

/**
 * 清空指定字典缓存（可选）
 * @param {String} dictType - 字典类型（不传则清空所有）
 */
export const clearDictCache = (dictType) => {
    if (dictType) {
        delete dictCache[dictType];
    } else {
        Object.keys(dictCache).forEach(key => delete dictCache[key]);
    }
};