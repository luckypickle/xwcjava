package xwc.xwcjava.serializer;

import xwc.xwcjava.crypto.CryptoUtil;
import xwc.xwcjava.exceptions.DeserializeException;
import xwc.xwcjava.exceptions.SerializeException;
import xwc.xwcjava.transaction.Memo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MemoSerializer implements ISerializer<Memo> {
    @Override
    public byte[] serialize(Memo instance) throws SerializeException {
        if(instance == null) {
            return CryptoUtil.singleBytes((byte) 0);
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(1); // optional value have 1 as first byte when exists
            byte[] pubKeyBytes = new byte[74]; // from and to public bytes
            CryptoUtil.setBytesZero(pubKeyBytes);
            bos.write(pubKeyBytes);
            byte[] messageStrBytes = instance.getTransientMessage().getBytes();
            bos.write(messageStrBytes.length + 4);
            byte[] nonceBytes = new byte[4];
            CryptoUtil.setBytesZero(nonceBytes);
            bos.write(nonceBytes);
            bos.write(messageStrBytes);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public Memo deserialize(byte[] bytes) throws DeserializeException {
        return null;
    }
}
