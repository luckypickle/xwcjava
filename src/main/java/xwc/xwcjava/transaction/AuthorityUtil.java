package xwc.xwcjava.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AuthorityUtil {
    public static Authority defaultAuthority() {
        Authority authority = new Authority();
        authority.setWeightThreshold(1);
        authority.setAccountAuths(new ArrayList<>());
        authority.setKeyAuths(Collections.singletonList(Arrays.asList((Object)"", 1)));
        authority.setTransientKeyAuths("");
        return authority;
    }
}
