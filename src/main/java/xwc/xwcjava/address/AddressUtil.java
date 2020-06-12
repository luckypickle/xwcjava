package xwc.xwcjava.address;

public class AddressUtil {

    /**
     * 判断一个地址是否是一个合法的普通用户地址
     * @param address
     * @param isTestnet 是否是regtest链
     * @return
     */
    public static boolean validateNormalAddress(String address, boolean isTestnet) {
        try {
            String prefix = isTestnet ? Address.TESTNET_ADDRESS_PREFIX : Address.ADDRESS_PREFIX;
            Address xwcAddress = Address.fromString(address, prefix);
            return xwcAddress.getValue(prefix).equals(address) && AddressVersion.NORMAL == xwcAddress.getVersion();
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }
}
