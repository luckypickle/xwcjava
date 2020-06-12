package xwc.xwcjava.serializer;

import xwc.xwcjava.exceptions.DeserializeException;
import xwc.xwcjava.exceptions.SerializeException;
import xwc.xwcjava.pubkey.PubKeyBytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PubkeySerializer implements ISerializer<PubKeyBytes> {
    @Override
    public byte[] serialize(PubKeyBytes instance) throws SerializeException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(instance.getData());
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public PubKeyBytes deserialize(byte[] bytes) throws DeserializeException {
        return null;
    }
}
