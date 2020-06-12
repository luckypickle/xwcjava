package xwc.xwcjava.address;

import xwc.xwcjava.crypto.Base58;
import xwc.xwcjava.crypto.CryptoUtil;
import xwc.xwcjava.exceptions.AddressException;
import xwc.xwcjava.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Address {
    private static final Logger log = LoggerFactory.getLogger(Address.class);

    public static final String ADDRESS_PREFIX = "XWC"; // mainnet prefix
    public static final String TESTNET_ADDRESS_PREFIX = "XWCT"; // testnet prefix

    private byte[] addy;
    private byte version; // XwcAddressVersion

    public byte[] getAddy() {
        return addy;
    }

    public byte[] getAddyWithVersion() {
        return CryptoUtil.bytesMerge(CryptoUtil.singleBytes(version), addy);
    }

    public void setAddy(byte[] addy) {
        this.addy = addy;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public Address(byte[] addy, byte version) {
        this.addy = addy;
        this.version = version;
    }

    public Address(byte[] addy) {
        this(addy, AddressVersion.NORMAL);
    }

    public static Address fromPubKey(byte[] pubKeyBytes, byte version) {
        byte[] pubKeySha512 = CryptoUtil.sha512(pubKeyBytes);
        byte[] addrBytes = CryptoUtil.ripemd160(pubKeySha512);
        return new Address(addrBytes, version);
    }

    public static Address fromBuffer(byte[] buffer) {
        return new Address(buffer);
    }

    public static Address fromString(String addrString, String addrPrefix) throws AddressException {
        if(addrString==null || addrString.length() <= addrPrefix.length()) {
            throw new AddressException("invalid XWC address length");
        }
        String prefix = addrString.substring(0, addrPrefix.length());
        if(!prefix.equals(addrPrefix)) {
            throw new AddressException(String.format("Expecting address to begin with %s but got %s", addrPrefix, prefix));
        }
        String addyStr = addrString.substring(addrPrefix.length());
        try {
            byte[] addyBytes = Base58.decode(addyStr);
            if(addyBytes.length<=4) {
                throw new AddressException("invalid address checksum");
            }
            byte[] checksumBytes = CryptoUtil.bytesSlice(addyBytes, addyBytes.length-4, addyBytes.length);
            byte[] newAddyBytes = CryptoUtil.bytesSlice(addyBytes, 0, addyBytes.length-4);
            byte[] newChecksumBytes = CryptoUtil.ripemd160(newAddyBytes);
            byte[] newChecksumBytesFirst4Bytes = CryptoUtil.bytesSlice(newChecksumBytes, 0, 4);
            boolean isEqualChecksum = CryptoUtil.deepEqualsOfBytes(checksumBytes, newChecksumBytesFirst4Bytes);
            if(!isEqualChecksum) {
                log.debug("expect checksum {} got {}", Numeric.toHexStringNoPrefix(checksumBytes), Numeric.toHexStringNoPrefix(newChecksumBytesFirst4Bytes));
                throw new AddressException("invalid address checksum");
            }
            byte addrVersion = newAddyBytes[0];
            byte[] addrBytesWithoutVersion = CryptoUtil.bytesSlice(newAddyBytes, 1, newAddyBytes.length);
            return new Address(addrBytesWithoutVersion, addrVersion);
        } catch (Exception e) {
            throw new AddressException(e);
        }
    }

    public byte[] toBuffer() {
        byte[] allBuffer = new byte[1+addy.length];
        allBuffer[0] = version;
        for(int i=0;i<addy.length;i++) {
            allBuffer[i+1] = addy[i];
        }
        return allBuffer;
    }

    public String getValue(String prefix) {
        try {
            byte[] allBuffer = this.toBuffer();
            byte[] checksum = CryptoUtil.ripemd160(allBuffer);
            byte[] addyWithChecksum = new byte[allBuffer.length+4]; // addyWithChecksum = allBuffer = checksum[0:4]
            for(int i=0;i<allBuffer.length;i++) {
                addyWithChecksum[i] = allBuffer[i];
            }
            for(int i=0;i<4;i++) {
                addyWithChecksum[allBuffer.length+i] = checksum[i];
            }
            return prefix + Base58.encode(addyWithChecksum);
        } catch (Exception e) {
            log.debug("Address.getValue error: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return getValue(ADDRESS_PREFIX);
    }
}
