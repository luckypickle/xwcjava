package xwc.xwcjava.asset;

import xwc.xwcjava.config.Constants;

public class AssetUtil {
    public static Asset defaultAsset() {
        Asset asset = new Asset();
        asset.setAmount(0);
        asset.setAssetId(Constants.XWC_ASSET_ID);
        return asset;
    }
}
