package net.media.autotemplate.enums;

import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.util.AuthenticationUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiFunction;

/**
 * Created by Jatin Warade
 * on 13-February-2023
 * at 11:22 am
 */
public enum AuthenticationType {
    CLOUD_FLARE_AUTHENTICATION("CF_Authorization", AuthenticationUtil::cfLogin, (req, resp) -> new Pair(true, "Logout Success")),
    VPN_AUTHENTICATION("vpn", AuthenticationUtil::vpnLogin, AuthenticationUtil::vpnLogout);

    String cookieName;
    BiFunction<HttpServletRequest, HttpServletResponse, Pair<Boolean, String>> loginFunction;
    BiFunction<HttpServletRequest, HttpServletResponse, Pair<Boolean, String>> logOutFunction;

    AuthenticationType(String cookieName, BiFunction<HttpServletRequest, HttpServletResponse, Pair<Boolean, String>> loginFunction, BiFunction<HttpServletRequest, HttpServletResponse, Pair<Boolean, String>> logOutFunction) {
        this.cookieName = cookieName;
        this.loginFunction = loginFunction;
        this.logOutFunction = logOutFunction;
    }

    public String getCookieName() {
        return cookieName;
    }

    public BiFunction<HttpServletRequest, HttpServletResponse, Pair<Boolean, String>> getLoginFunction() {
        return loginFunction;
    }

    public BiFunction<HttpServletRequest, HttpServletResponse, Pair<Boolean, String>> getLogOutFunction() {
        return logOutFunction;
    }
}
