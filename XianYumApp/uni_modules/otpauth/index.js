
import * as OTPAuth from './lib/otpauth.esm.min.js';

// 导出给外部使用
export default OTPAuth;
export { OTPAuth };
export const { TOTP, HOTP, Secret, URI } = OTPAuth;