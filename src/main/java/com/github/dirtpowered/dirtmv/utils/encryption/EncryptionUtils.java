/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.dirtmv.utils.encryption;

import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.server.codec.encryption.PacketDecryptionCodec;
import com.github.dirtpowered.dirtmv.network.server.codec.encryption.PacketEncryptionCodec;
import io.netty.channel.socket.SocketChannel;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

public class EncryptionUtils {

    public static KeyPair keyPair;

    static {
        // generate key pair
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);

            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Enables proxy-side connection encryption
     *
     * @param channel {@link io.netty.channel.socket.SocketChannel channel} netty channel
     * @param sharedKey {@link javax.crypto.SecretKey key} shared secret key
     */
    public static void setEncryption(SocketChannel channel, SecretKey sharedKey) {
        channel.pipeline().addBefore(
                "decoder", "packet_decryption",
                new PacketDecryptionCodec(getCipher(false, sharedKey))
        );

        channel.pipeline().addBefore(
                "encoder", "packet_encryption",
                new PacketEncryptionCodec(getCipher(true, sharedKey))
        );
    }

    public static SecretKey getSecretKey() {
        return new SecretKeySpec(new byte[16], "AES");
    }

    public static byte[] getSharedKey(SecretKey key, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(key.getEncoded());
    }

    public static byte[] encrypt(Key key, byte[] b) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(b);
    }

    private static Cipher getCipher(boolean forEncryption, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(forEncryption ? 1 : 2, key, new IvParameterSpec(key.getEncoded()));

            return cipher;
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static SecretKey getSecret(PacketData response, PacketData request) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

        byte[] sharedSecret = (byte[]) response.read(0).getObject();
        byte[] requestToken = (byte[]) request.read(2).getObject();
        byte[] responseToken = (byte[]) response.read(1).getObject();

        byte[] decrypted = cipher.doFinal(responseToken);

        if (!Arrays.equals(requestToken, decrypted)) {
            throw new IllegalStateException("Key pairs do not match!");
        }

        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return new SecretKeySpec(cipher.doFinal(sharedSecret), "AES");
    }
}
