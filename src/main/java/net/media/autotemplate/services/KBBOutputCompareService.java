package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.network.NetworkResponse;
import net.media.autotemplate.util.network.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
    Created by shubham-ar
    on 14/5/18 7:41 PM   
*/
public class KBBOutputCompareService {
    private static final String[] urls = {
            "/keyword_api.php?d=msn.com&cc=US&kf=0&pt=10&type=1&ymp=&uftr=0&kwrd=0&py=1&dtld=com&combineExpired=1&lid=224&mcat=15458&fpid=800113225&maxno=9&actno=3&ykf=1&rurl=https%3A%2F%2Fwww.msn.com%2Fen-us%2Fnews%2Ffactcheck%2Fstelter-how-trumps-false-claim-about-african-american-support-happened%2Far-AAwRtN1%3F%26intrabottom%3D1&pid=8PO4J71H8&lmsc=0&hint=&hs=3&stag=11013780&stags=11013780&mtags=&crid=353566216&https=1&accrev=1&csid=8CU72YM44&ugd=2&rand=1944632021&tsize=622x220&calling_source=cm&bkt_block=202%7C204%7C205%7C282%7C265%7C266%7C281%7C270%7C271%7C275%7C295%7C297%7C299%7C300%7C325%7C326%7C306%7C307%7C318%7C317%7C337%7C340%7C344%7C347%7C348%7C349%7C350%7C360%7C364%7C372%7C387%7C388%7C389%7C410%7C411%7C412%7C413%7C416%7C417%7C418%7C419%7C420%7C374%7C376&stag_tq_block=1&img=1&fm_sb=1&json=1",
            "/kbb.php?cme=KkKUG6JLaWptcd7ygVoZgW_gowIx32vSl5tlbUoHIlTwG_Hajq4qm8TPaunw8hu-gs5H0j_cu9dNg1SpNWSXZrDePxhb2Ydcf4aFuiJhJ_eltvP9jBS5dC3z7TNxi4Tr%7C%7CNDHRnZ9Gz3KXlI-i9OnZqQ%3D%3D%7Cb0R63xgiNqRn8V2_z6g37mqH5a_5IZjh%7CvVsU98iBYsV0bAcAUTTazQ%3D%3D%7CPxzXeMabn5gBGN84f1gfK7_8V36HGhAh%7CN7fu2vKt8_s%3D%7CeEKdjipthjftEmmW9obiV-6EL9Vu697FfXXeVvA18hMg8WL5dtddyGi3DUtP8CYecNXE_F3AKxwEISaGvnmH93-yF68w3uefEtHG6gKRJKcn2LYz4Z5NEcHwdAFlrTKO99E6MdM9zRQTDgpnZ_ZBKmqHgjXfiCwk%7Cy2SqoJcE0s8gLHKSSh1cN8xT-LY0WUe8%7C&srp=ZPL-DvhZL6skXWLG3cujms7VMmV6IfbH6TcXT3XyrOZGjXhtEc1yOZNlLmACEh0t&klp=z_alOBwq_AwlcrlyGabI6ZIx5Pk656JELqDvvhmv3R8fW5tR0LoeNS2NSNmPc8jYC3oz2ECA6udaGfjiWK25fzEzOfBXFbIG7pxguA4QLlkfsntYcKj0x7TCvv5rnFbME-zOmq2DhXDSTTj9PukmNMqeflTCujgAD0gvffFtIoYcAFcz582RU3PSaFZ8JuXIdkjTHDZaSp-JD5dT4ewxYUd5JNpc90PztciJ_rSusb2JPE6HfkzQfeZvC4oR2-O9QR6r3EPXjfT5t5U02rm5gkwkW--Q2U-K2HDGi688CLH2gF68LsGYccEMVQ0rGCSl41vysSJKuPl_agZ0Zfu89WhO4ntaB4jtoX16PILyrfDEgpnT0K0Y9eGYZwclDsK8&kwrf=https%3A%2F%2Fwww.msn.com&cb=resultPageUtil.kwdRandmzn[%271526367717113353456%27]&pvid=1602978636786850&ip=178.48.219.196&ua=Mozilla%2f5.0%20(Macintosh%3b%20Intel%20Mac%20OS%20X%2010_13_4)%20AppleWebKit%2f605.1.15%20(KHTML,%20like%20Gecko)%20Version%2f11.1%20Safari%2f605.1.15",
            "/keyword_api.php?d=edition.cnn.com&cc=PL&kf=0&pt=60&type=1&uftr=0&kwrd=0&py=1&dtld=com&combineExpired=1&lid=255&mcat=15444&fpid=800141995&maxno=12&actno=8&ykf=1&rurl=https%3A%2F%2Fedition.cnn.com%2F2018%2F05%2F14%2Fus%2Fnew-york-apartment-rent-control-actress-trnd%2Findex.html%3Futm_source%3Dfeedburner&pid=8POJC548H&lmsc=1&hs=0&stag=mnet_mobile744_search&stags=mnet_mobile744_search%2Cskenzo_search903&mtags=hypecmdm%2Cperform&crid=505661222&https=1&accrev=1&csid=8CUIG2452&ugd=2&rand=484870793&tsize=780x218&calling_source=cm&bkt_block=355&fm_bt=1&fm_sb=1&json=1",
            "/kbb.php?cme=q3srXh-_ygEu1LkVndi5V2vMZa5r0P4gH9sQP6Z7BfWGC-0l6WBAj4kH46I6v84QOJOS-oouuNKQfQrRJ8xtGme_QyxuXstyH7ggB3Ik3MhTE1O5dVotE2ROOMnT547mtAiL6B0N-Sc%3D%7C%7CSKuUSfCLRUl4Qb_mAXte9p_dPjt9YFcQ%7C5gDUJdTGiJzedmq9hanWYg%3D%3D%7CN7fu2vKt8_s%3D%7C83SQRl78aOLSLAsaMaJ_gQVSb17gKjBd4CAHq_53vbn1_GuP6KIxRe9oKIqEOeMdyP7zo08Boohwz6XFgHq0pDCC4ATsTlFXkYOY-Gu_6KY%3D%7CJf0d-WoAdPtGmAMNOC7M6w8sojjWVumTED4wgS48XsE%3D%7C&srp=ypQJNGNB4URbFT0SCyMpEAs57QBph-NJicC44VujJbRrbBCwiYLRQuBqv_HZiyhJ&klp=smW6_BByc8Gli91P7AbDsSlbl7-GmOlh2IfG681ey-mNKC6bFoeD4E8coRvdt7KIEFzHGbw_qpfitkhrzJYYElzyzR3nRjBAN7CDEVyJNVTbI46iExi2uhOmeOkZcgDzjtb7dK6oAplVNPejXXS8YLLhjKhO3IzZ4TOoaBikTG9FF_gDRkqpWkted0i6VpDLtPG3k_hQffxzrl15TRO3EN9V55Kutb333TpjKeBZx2JzR8K-p98f8Ufxi7ixGZ0WTVQdHutf7WqZQEXCln4_fRqQu1iB1Vi2v8r9JlAywNJOrXqOGieEcw%3D%3D&nse=1&kwrf=http%3A%2F%2Fwww.drudgereport.com&cb=resultPageUtil.kwdRandmzn[%271526367205345940710%27]&pvid=1693688083100141&ip=101.188.44.153&ua=Mozilla%2f5.0%20(Windows%20NT%2010.0%3b%20Win64%3b%20x64)%20AppleWebKit%2f537.36%20(KHTML,%20like%20Gecko)%20Chrome%2f66.0.3359.139%20Safari%2f537.36&vstate=VIC",
            "/keyword_api.php?d=forbes.com&cc=IN&kf=0&pt=60&type=1&uftr=0&kwrd=0&py=1&dtld=com&combineExpired=1&lid=224&mcat=4067&fpid=800157588&maxno=12&actno=8&ykf=1&rurl=https%3A%2F%2Fwww.forbes.com%2Fsites%2Fallbusiness%2F2018%2F03%2F27%2F10-expert-social-media-tips-to-help-your-small-business-succeed%2F2%2F%23mnetcrid%3D803611558%23&pid=8POH51WS2&lmsc=0&hint=&hs=3&stag=mnet_mobile435_search&stags=mnet_mobile435_search%2Cskenzo_search1050&mtags=hypecmdm%2Cperform&crid=803611558&https=1&accrev=1&csid=8CU2T3HV4&ugd=4&rand=1497023066&tsize=1051x470&calling_source=cm&fm_bt=1&img=1&json=1",
            "/kbb.php?cme=ts7D9CqS1R3tQ8lwduG9R4W9G20MK-NlOo978eJKpQi-_vScszNVbvGAM73PQflCHU1q7PvO-IPj61IrbV_mToZHM8rYuHy9omGXQ5UKrqFEWb7YKkfj_-tKXpqYbFTa%7C%7CNDHRnZ9Gz3KXlI-i9OnZqQ%3D%3D%7C5gDUJdTGiJzedmq9hanWYg%3D%3D%7CN7fu2vKt8_s%3D%7CmKHKUfc2uYX_PA5WvlVoMCLxply_iX7YOQqWybVCw2v4LcEW27N4c6bvNVfcZj6qMXZOuS0qWwHZqqFdQKGJVP9p0BH7nQBiOsa01eUktICqqew9fNfcYTnyQAt9TT6UJUIBidKIl94zpKGdcCSjIQ%3D%3D%7Cy2SqoJcE0s8_YkEKQb2o70zWSqj1LngDCCacBk_UZDY%3D%7C&srp=ZPL-DvhZL6s6rad2t7KE_uwMwjgjTdRMhRZJN76xHVA_IkLvnN5zVVYDbpgYX8ay&klp=MkFzGAoabft-2Ft9AzWpsdiCzykLfLGrXiHLHJ_DhmsgAnSYW5gkdJntiUkbKO0Sx68LEo8x08fiXX8Di8w9tyxiNZkEBHwC0fEE1SOiMasDaMJ7F7soVNntmEfUdGaQkiiFWr5D34LJZSH7uhr33ctbCVFqLNGAcG_EeURhjD5sfcf_2as-dc3vIT4Vulj9WJyuQAPOKzyzvnIDzKYHejaNP2C9bb5Ap-Q-bgLyjYH9NdEUMdO4XU1PpMFoKKZ7EH1z6PGk2MEG22Lg5MOUmw%3D%3D&kwrf=https%3A%2F%2Fwww.forbes.com&bid=219272&cb=resultPageUtil.kwdRandmzn[%271526368197166919843%27]&olid=258&pvid=1452675087193061&ip=62.192.6.90&ua=Mozilla%2f5.0%20(Windows%20NT%2010.0%3b%20Win64%3b%20x64)%20AppleWebKit%2f537.36%20(KHTML,%20like%20Gecko)%20Chrome%2f60.0.3112.90%20Safari%2f537.36&vstate=ZH",
            "/kbb.php?cme=YSPXPsW_9zPyp2YjWMqnmdLPebC5-xxoptz-ARFrxz1RLdyx19FXpw4gIUvwBoOcIiRt20zU-jzELgBzG46mMVolkX5mRVWjlcPeCAr1aIP5RE0tC-35TbphKYFrw6NdYDWufxbtWeK98LF8SMqhsQ%3D%3D%7C%7CNDHRnZ9Gz3KXlI-i9OnZqQ%3D%3D%7C4aPqsxhcBU0Jbgq-08Xz1A%3D%3D%7CtrJ5NInYpv_fFbZnbBeN4sR4x1nqMfHL%7CRrUTbnOe6Nf6JuES_S-ui06Hj5VtiZHe%7CN7fu2vKt8_s%3D%7CYdjFvixrVaFiaRyfbhZDLtMhA2JBK6VSgKYUv_gt6aS0E_0B0m2ufGyPc4GuUCnqE_rMnDB8F5bJw7iFwE4Cow%3D%3D%7CJf0d-WoAdPvWEJ9F8_r-uiazs1u7lMQ6KywktZq0r50%3D%7C&srp=ZPL-DvhZL6txVpv3WNhv4KO_g4U6CC4djm5s2XmCXNuxAKBOdCRufTe-i74MkXBd&klp=8bcrN8-J85iePb2YK1b2a8Z-VqqOA5UnVeMob05IvZtzctWMJ8XuVRJ3n0ZpDsXuJCOwN7M0BnD-w_4LmfCdhMiltAZ3Hwye22Cr0dxEJ0kO82akuiF3FZM7AurK2A9k62HNjqUlQf1gvCYvxYkjrdJf6a-wyc-rVNNWSN67xE8C8gcU0dxzhIqIWypGvKl-nw4zIX-UFXRoRDbqz0sV89ChkWvAD3RRaW8CsBVpPwrCTd8TobFgB5H7AjpKz9lIwRENorajDV4%3D&nse=1&kwrf=http%3A%2F%2Fl.facebook.com&atid=800515861&cb=resultPageUtil.kwdRandmzn[%271526368916487651497%27]&pvid=1693705147509687&ip=31.13.114.249&ua=Mozilla%2f5.0%20(Windows%20NT%2010.0%3b%20Win64%3b%20x64)%20AppleWebKit%2f537.36%20(KHTML,%20like%20Gecko)%20Chrome%2f66.0.3359.139%20Safari%2f537.36&vstate=BD",
            "/keyword_api.php?d=thegrumpyfish.com&cc=US&kf=0&pt=60&type=1&uftr=0&kwrd=0&py=1&dtld=com&combineExpired=1&lid=224&mcat=15444&fpid=800054070&maxno=9&actno=6&ykf=1&rurl=https%3A%2F%2Fthegrumpyfish.com%2F2018%2F05%2F14%2Fdonald-trump-runs-for-iron-throne%2F&pid=8PONOHY63&lmsc=1&hint=&hs=3&stag=mnet_mobile1_search&stags=mnet_mobile1_search%2Cskenzo_search5&mtags=hypecmdm%2Cperform&crid=336865945&https=1&csid=8CU4CPO66&ugd=3&rand=2111629254&tsize=300x250&calling_source=cm&fm_bt=1&json=1"
    };
    private static final String[] tags = {
            "LONG_BLOCK_KEYWORD",
            "LONG_BLOCK_EXTERNAL",
            "SMALL_BLOCK_KEYWORD",
            "SMALL_BLOCK_EXTERNAL",
            "NO_BLOCK_KEYWORD",
            "NO_BLOCK_EXTERNAL",
            "RANDOM_EXTERNAL",
            "RANDOM_KEYWORD"
    };
    private static final String[] setups = {"c8", "c10", "c12-nc1c"};
    private static final String[] context = {":8989/kbb", ":8081/kbbTest_shubham_ar"};
    public static final String extras = "&nocache=1&srand=1&nc=1";
    public static final Gson GSON = Util.getGson();
    private static final int box = 1;

    public static void main(String[] args) {
        List<Pair<String, String>> urls = getUrls();
        AtomicInteger integer = new AtomicInteger(0);
        urls.forEach(item -> {
            try {
                System.out.println(tags[integer.getAndIncrement()]);
                for (int i = 0; i < setups.length; i++) {
                    String s = setups[i];
                    String live = getUrl(i, 1, item.first);
                    String test = getUrl(i, 1, item.second);
                    printTable(live, test);
                    System.out.println();
                    System.out.println();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    private static void printTable(String live, String test) {

        System.out.println("LIVE:" + live);
        List<String> liveKeywords = getKeywords(live);

        System.out.println("TEST:" + test);
        List<String> testKeywords = getKeywords(test);
        boolean unequal = false;

        System.out.println(String.format("%54s|%s", "LIVE", "TEST"));
        for (int i = 0; i < testKeywords.size(); i++) {
            String[] lk = liveKeywords.get(i).split("\\|");
            String[] tk = testKeywords.get(i).split("\\|");
            unequal = unequal | !lk[1].equals(tk[1]);
            System.out.println(String.format("%50s|%3s|%3s|%s", lk[1], lk[0], tk[0], tk[1]));
        }

        if (liveKeywords.size() != testKeywords.size() || unequal) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~MISMATCH~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
    }

    private static String getUrl(int setup, int box, String append) {
        return String.format("http://%s-kbb-%d.srv.media.net%s", setups[setup], box, append);
    }

    private static List<Pair<String, String>> getUrls() {
        List<Pair<String, String>> livevsLocal = new ArrayList<>();
        for (String params : urls) {
            livevsLocal.add(new Pair<>(context[0] + params + extras, context[1] + params + extras));
        }
        return livevsLocal;
    }

    private static List<String> getKeywords(String url) {
        List<String> kys = new ArrayList<>();
        try {
            NetworkResponse resp = NetworkUtil.getRequest(url);
            String response = resp.getBody();
            if (response.indexOf('(') > -1) {
                int i = response.indexOf('(') + 1;
                int j = response.indexOf(')');
                response = response.substring(i, j);
            }
            JsonObject jsonObject = GSON.fromJson(response, JsonObject.class);
            jsonObject.get("r").getAsJsonArray().forEach(item -> {
                item.getAsJsonArray().forEach(item2 -> {
                    item2.getAsJsonObject().get("bg").getAsJsonArray().forEach(keyword -> {
                        keyword.getAsJsonObject().get("k").getAsJsonArray().forEach(it -> {
                            JsonObject kb = it.getAsJsonObject();
                            kys.add(String.format("%s|%s", kb.get("ty").getAsString(), kb.get("t").getAsString()));
                        });
                    });
                });
            });
        } catch (IOException e) {

            e.printStackTrace();
        }
        return kys;
    }
}
