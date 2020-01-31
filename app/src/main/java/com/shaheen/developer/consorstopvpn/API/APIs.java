package com.shaheen.developer.consorstopvpn.API;

import android.text.TextUtils;
import android.util.Patterns;

public class APIs {



    public static final String LOGIN = "http://csvpn.com/api/login";
    public static final String GENERATE_VPN_ACCOUNT = "https://api.atom.purevpn.com/vam/v1/generate";
    public static final String GET_ACCESS_TOKEN = "https://api.atom.purevpn.com/auth/v1/accessToken";
    public static final String GET_ChannelsList = "https://api.atom.purevpn.com/inventory/v1/getAllChannels";
    public static final String FORGOTPASSLINK = "http://csvpn.com/registration/forgotpassword";

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    /// login credentials in my.atom.purevpn.com ---->>>>    username: censorstop.atom@gmail.com    pass: 7c85111d5230bcc58

}
