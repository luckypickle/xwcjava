package xwc.xwcjava.serializer;

import xwc.xwcjava.crypto.CryptoUtil;
import xwc.xwcjava.exceptions.DeserializeException;
import xwc.xwcjava.exceptions.PubKeyInvalidException;
import xwc.xwcjava.exceptions.SerializeException;
import xwc.xwcjava.pubkey.PubKeyUtil;
import xwc.xwcjava.transaction.Authority;

public class AuthoritySerializer implements ISerializer<Authority> {
    @Override
    public byte[] serialize(Authority instance) throws SerializeException {
        try {
            Uint32Serializer uint32Serializer = Uint32Serializer.defaultInstance();
            Uint16Serializer uint16Serializer = Uint16Serializer.defaultInstance();
            byte[] weightThresholdBytes = uint32Serializer.serialize(instance.getWeightThreshold());
            byte[] zero = CryptoUtil.singleBytes((byte) 0);
            int keyAuthsSize = instance.getKeyAuths() != null ? instance.getKeyAuths().size() : 0;
            byte[] keyAuthsLenBytes = CryptoUtil.singleBytes((byte) keyAuthsSize);
            byte[] keyAuthsBytes = PubKeyUtil.getPubKeyBytes(instance.getTransientKeyAuths());
            byte[] oneBytes = uint16Serializer.serialize(1);
            byte[] zero2Bytes = CryptoUtil.singleBytes((byte) 0);
            return CryptoUtil.bytesMerge(weightThresholdBytes, zero, keyAuthsLenBytes, keyAuthsBytes, oneBytes, zero2Bytes);
        } catch (PubKeyInvalidException e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public Authority deserialize(byte[] bytes) throws DeserializeException {
        return null;
    }
}
