package com.yifan_zuo.creepymobile.utils;

/**
 * Created by YifanZuo on 20/03/15.
 */

public class Constants {

    // General keys
    public static final int FLICKR = 0;
    public static final int TWITTER = 1;
    public static final int INSTAGRAM = 2;


    // Flickr keys
    public static final String FLICKR_QUERY_URL = "https://api.flickr" +
            ".com/services/rest/?method=";
    public static final String FLICKR_SEARCH_USER_BY_NAME = "flickr.people" +
            ".findByUsername&username=";
    public static final String FLICKR_GET_PHOTOLIST = "flickr.people" +
            ".getPublicPhotos&user_id=";
    public static final String FLICKR_GET_INFO = "flickr.people" +
            ".getInfo&user_id=";
    public static final String FLICKR_SEARCH_PHOTOS = "flickr.photos" +
            ".search&lat=%s&lon=%s&radius=%s";
    public static final String FLICKR_EXTRAS = "&extras=geo";
    public static final String FLICKR_QUERY_TAIL =
            "&api_key=14735f4e700147e1960c9ca39e0ca197&nojsoncallback=1" +
                    "&format=json";
    public static final String FLICKR_PAGES = "&per_page=10&page=1";


    // Twitter keys
    public static final String TW_CONSUMER_KEY = "cLWaOVsN8q38HLlTcUaRIdOrK";
    public static final String TW_CONSUMER_SECRET =
            "6493Z0m56H0QahxLJW1whzs03apfqwHhhbyxirRoo2bwoJ63qj";
    public static final String TW_ACCESS_TOKEN =
            "3096917414-jIjsLY3h8XZgxB4T64QrUDVQbgl3c94Zf60DxJh";
    public static final String TW_ACCESS_TOKEN_SECRET =
            "csFhN1pWd0QRvIgKx89GJuheAffGaaK4gF26VPJXXcfDo";


    public static final String DEBUG_TAG = "CreepyMobile Debug";


    // Instagram keys
    public static final String INST_CLIENT_ID =
            "8e27fcbe99a94659a0f23f7653180187";
    public static final String INST_CLIENT_SECRET =
            "bcfedd6ee7cd462b9533a9f7c3460d0c";
    public static final String INST_AUTH_URL = "https://api.instagram" +
            ".com/oauth/authorize/";
    public static final String INST_ACCESS_TOKEN_URL = "https://api.instagram" +
            ".com/oauth/access_token";
    public static final String INST_API_URL = "https://api.instagram.com/v1";
    public static String INST_CALLBACK_URL = "https://www.instagram.com";
    public static final String INST_AUTH_URL_STR = INST_AUTH_URL +
            "?client_id=" +
            INST_CLIENT_ID +
            "&redirect_uri=" + INST_CALLBACK_URL +
            "&response_type=code&display=touch&scope=likes+comments" +
            "+relationships";

    public static final String INST_TOKEN_URL_STR =
            INST_ACCESS_TOKEN_URL +
                    "?client_id=" +
                    INST_CLIENT_ID +
                    "&client_secret=" + INST_CLIENT_SECRET +
                    "&redirect_uri=" +
                    INST_CALLBACK_URL + "&grant_type=authorization_code";


}
