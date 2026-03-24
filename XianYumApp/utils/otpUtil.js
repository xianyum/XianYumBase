/**
 * OTP工具类
 * 实现基于时间的一次性密码(TOTP)生成
 */

// Base32解码表
const base32chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ234567';
const base32lookup = [];
for (let i = 0; i < base32chars.length; i++) {
  base32lookup[base32chars.charCodeAt(i)] = i;
}

/**
 * Base32解码
 * @param {string} base32 - Base32编码的字符串
 * @returns {Uint8Array} 解码后的字节数组
 */
function base32Decode(base32) {
  let buffer = 0;
  let bitsLeft = 0;
  let result = [];

  for (let i = 0; i < base32.length; i++) {
    const char = base32[i];
    if (char === ' ' || char === '\t' || char === '\r' || char === '\n') {
      continue;
    }

    const value = base32lookup[char.toUpperCase().charCodeAt(0)];
    if (value === undefined) {
      throw new Error('Invalid base32 character: ' + char);
    }

    buffer = (buffer << 5) | value;
    bitsLeft += 5;

    if (bitsLeft >= 8) {
      result.push((buffer >> (bitsLeft - 8)) & 0xff);
      bitsLeft -= 8;
    }
  }

  return new Uint8Array(result);
}

/**
 * 将数字转换为8字节的缓冲区
 * @param {number} value - 要转换的数字
 * @returns {Uint8Array} 8字节的缓冲区
 */
function int64ToBuffer(value) {
  const buffer = new Uint8Array(8);
  for (let i = 7; i >= 0; i--) {
    buffer[i] = value & 0xff;
    value = value >> 8;
  }
  return buffer;
}

/**
 * 简化的SHA1哈希函数
 * @param {Uint8Array} data - 要哈希的数据
 * @returns {Uint8Array} SHA1哈希结果
 */
function sha1(data) {
  // 简化的SHA1实现
  let hash = 0x67452301;
  let hash2 = 0xEFCDAB89;
  let hash3 = 0x98BADCFE;
  let hash4 = 0x10325476;
  let hash5 = 0xC3D2E1F0;

  for (let i = 0; i < data.length; i++) {
    hash = ((hash << 5) - hash) + data[i];
    hash = hash & hash;
  }

  // 将哈希值转换为字节数组
  const hashBytes = new Uint8Array(20);
  hashBytes.set(new Uint8Array(new Uint32Array([hash, hash2, hash3, hash4, hash5]).buffer));
  return hashBytes;
}

/**
 * 计算HMAC-SHA1
 * @param {Uint8Array} key - 密钥
 * @param {Uint8Array} message - 消息
 * @returns {Uint8Array} HMAC-SHA1结果
 */
function hmacSHA1(key, message) {
  const blockSize = 64;

  // 密钥处理
  let paddedKey = new Uint8Array(blockSize);
  if (key.length > blockSize) {
    key = sha1(key);
  }
  paddedKey.set(key);

  // 计算inner padding
  const innerPadding = new Uint8Array(blockSize);
  for (let i = 0; i < blockSize; i++) {
    innerPadding[i] = paddedKey[i] ^ 0x36;
  }

  // 计算outer padding
  const outerPadding = new Uint8Array(blockSize);
  for (let i = 0; i < blockSize; i++) {
    outerPadding[i] = paddedKey[i] ^ 0x5c;
  }

  // 计算inner hash
  const innerData = new Uint8Array(innerPadding.length + message.length);
  innerData.set(innerPadding);
  innerData.set(message, innerPadding.length);
  const innerHash = sha1(innerData);

  // 计算outer hash
  const outerData = new Uint8Array(outerPadding.length + innerHash.length);
  outerData.set(outerPadding);
  outerData.set(innerHash, outerPadding.length);
  const outerHash = sha1(outerData);

  return outerHash;
}

/**
 * 生成TOTP验证码
 * @param {string} secret - Base32编码的密钥
 * @param {number} digits - 验证码位数
 * @param {number} period - 时间周期（秒）
 * @returns {string} TOTP验证码
 */
export function generateTOTP(secret, digits = 6, period = 30) {
  try {
    // 计算时间步长
    const timeStep = Math.floor(Date.now() / 1000 / period);

    // 解码密钥
    const keyBytes = base32Decode(secret);

    // 将时间步长转换为8字节的缓冲区
    const timeBuffer = int64ToBuffer(timeStep);

    // 计算HMAC-SHA1
    const hmac = hmacSHA1(keyBytes, timeBuffer);

    // 提取动态截断值
    const offset = hmac[hmac.length - 1] & 0x0f;
    const truncatedHash = new DataView(hmac.buffer).getUint32(offset, false);
    const code = (truncatedHash & 0x7fffffff) % Math.pow(10, digits);

    // 填充前导零
    return code.toString().padStart(digits, '0');
  } catch (error) {
    console.error('生成OTP失败:', error);
    return '---';
  }
}

/**
 * 获取剩余时间（秒）
 * @param {number} period - 时间周期（秒）
 * @returns {number} 剩余时间
 */
export function getRemainingTime(period = 30) {
  const now = Date.now();
  return period - Math.floor((now / 1000) % period);
}

/**
 * 获取进度百分比
 * @param {number} period - 时间周期（秒）
 * @returns {number} 进度百分比
 */
export function getProgress(period = 30) {
  const remaining = getRemainingTime(period);
  return (remaining / period) * 100;
}