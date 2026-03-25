
import * as OTPAuth from './lib/otpauth.esm.min.js';

// 基于：https://github.com/hectorm/otpauth
// 演示：https://otpauth.molinero.dev/
// 导出给外部使用
export default OTPAuth;
export { OTPAuth };
export const { TOTP, HOTP, Secret, URI } = OTPAuth;