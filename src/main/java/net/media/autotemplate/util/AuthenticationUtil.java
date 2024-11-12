package net.media.autotemplate.util;

import com.onelogin.saml2.Auth;
import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.enums.AuthenticationType;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Created by Jatin Warade
 * on 13-February-2023
 * at 11:31 am
 */
public class AuthenticationUtil {
    public static Pair<Boolean, String> vpnLogout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Auth auth = new Auth(request, response);
            String nameId = (String) request.getSession().getAttribute("nameId");
            String sessionId = (String) request.getSession().getAttribute("sessionId");
            auth.logout(request.getContextPath() + "web/logout", nameId, sessionId);
            return new Pair<>(true, "Logout Success");
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<>(false, "Logout Failed");
        }
    }

    public static Pair<Boolean, String> vpnLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            Auth auth = new Auth(request, response);
            auth.login();
            return new Pair<>(true, "Login Success");
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<>(false, "Login Failed");
        }
    }

    public static Pair<Boolean, String> cfLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (getAuthenticationType(request) == AuthenticationType.CLOUD_FLARE_AUTHENTICATION)
                return new Pair<>(true, "Login Success");
            return new Pair<>(false, "Login Failed");
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<>(false, "Login Failed");
        }
    }

    public static AuthenticationType getAuthenticationType(HttpServletRequest request) {
        AuthenticationType authenticationType = AuthenticationType.VPN_AUTHENTICATION;
        try {
            Optional<Cookie> cookie = Util.getCookie(request, AuthenticationType.CLOUD_FLARE_AUTHENTICATION.getCookieName());
            if (Util.isSet(cookie.get())) {
                authenticationType = AuthenticationType.CLOUD_FLARE_AUTHENTICATION;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authenticationType;
    }
}
