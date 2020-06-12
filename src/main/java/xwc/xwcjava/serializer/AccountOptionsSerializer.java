package xwc.xwcjava.serializer;

import xwc.xwcjava.crypto.CryptoUtil;
import xwc.xwcjava.exceptions.DeserializeException;
import xwc.xwcjava.exceptions.PubKeyInvalidException;
import xwc.xwcjava.exceptions.SerializeException;
import xwc.xwcjava.pubkey.PubKeyUtil;
import xwc.xwcjava.transaction.AccountOptions;

public class AccountOptionsSerializer implements ISerializer<AccountOptions> {
    @Override
    public byte[] serialize(AccountOptions instance) throws SerializeException {
        try {
            Uint16Serializer uint16Serializer = Uint16Serializer.defaultInstance();
            byte[] memoKeyBytes = PubKeyUtil.getPubKeyBytes(instance.getMemoKey());
            byte[] five = CryptoUtil.singleBytes((byte) 5);
            byte[] zero1 = uint16Serializer.serialize(0);
            byte[] zero2 = uint16Serializer.serialize(0);
            byte[] zero3 = CryptoUtil.singleBytes((byte) 0);
            byte[] ten = CryptoUtil.singleBytes((byte) 10);
            byte[] zero4 = CryptoUtil.singleBytes((byte) 0);
            return CryptoUtil.bytesMerge(memoKeyBytes, five, zero1, zero2, zero3, ten, zero4);
        } catch (PubKeyInvalidException e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public AccountOptions deserialize(byte[] bytes) throws DeserializeException {
        return null;
    }
}
