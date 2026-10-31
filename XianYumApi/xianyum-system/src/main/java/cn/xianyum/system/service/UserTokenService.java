package cn.xianyum.system.service;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.system.entity.request.UserLoginRequest;
import cn.xianyum.system.entity.response.LoginTokenResponse;
import cn.xianyum.system.entity.response.QrLoginTicketResponse;

public interface UserTokenService {

    LoginTokenResponse createToken(LoginUser user);

    void logout();

    void setExtraUserInfo(LoginUser loginUser);

    void sendLoginCredentials(UserLoginRequest request);

    void sendLoginCredentialsByEmail(UserLoginRequest request, String code);

    LoginTokenResponse login(UserLoginRequest request);

    QrLoginTicketResponse generateQrCode();

    void scanQrCode(String ticket);

    void confirmQrCode(String ticket);

    QrLoginTicketResponse getQrCodeStatus(String ticket);
}
