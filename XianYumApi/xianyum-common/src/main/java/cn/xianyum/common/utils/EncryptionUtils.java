package cn.xianyum.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @description
 * @date 2022/7/17 14:09
 */
public class EncryptionUtils {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final WithSalt DEFAULT_WITH_SALT = new WithSalt() {
        public String withSalt(String content, String salt) {
            return content + salt;
        }
    };
    private static final WithoutSalt DEFAULT_WITHOUT_SALT = new WithoutSalt() {
        public String withoutSalt(String content, String salt) {
            if (!StringUtils.hasText(content)) {
                return content;
            } else if (content.endsWith(salt)) {
                return content.substring(0, content.length() - salt.length());
            } else {
                return null;
            }
        }
    };

    private EncryptionUtils() {
    }

    public static class XOR {
        private static final long MIN_SIZE = 1152921504606846976L;
        private static final long MAX_SIZE = Long.MAX_VALUE;

        public XOR() {
        }

        public static long generateKey() {
            SecureRandom random = new SecureRandom();
            double d = random.nextDouble();
            return (long)(d * 8.0704505322479288E18) + 1152921504606846976L;
        }

        public static String encrypt(long key, long content) {
            String contentBinary = Long.toBinaryString(content);
            String keyBinary = Long.toBinaryString(key);
            long subKey = Long.parseLong(contentBinary.length() < keyBinary.length() ? keyBinary.substring(0, contentBinary.length()) : keyBinary, 2);
            return String.valueOf(contentBinary.length() < 10 ? contentBinary.length() + 64 : contentBinary.length()) + (content ^ subKey);
        }

        public static long decrypt(long key, String encrypt) {
            int keySize = Integer.parseInt(encrypt.substring(0, 2)) % 64;
            String keyBinary = Long.toBinaryString(key);
            long subKey = Long.parseLong(keySize < keyBinary.length() ? keyBinary.substring(0, keySize) : keyBinary, 2);
            return Long.parseLong(encrypt.substring(2)) ^ subKey;
        }
    }

    public static class RSA2 {
        private static final String ALGORITHMS_SHA256 = "SHA256WithRSA";

        public RSA2() {
        }

        public static Pair<String, String> generateKeyPair() {
            return RSA.generateKeyPair();
        }

        private static RSAPublicKey getPublicKey(String publicKey) {
            return RSA.getPublicKey(publicKey);
        }

        private static RSAPrivateKey getPrivateKey(String privateKey) {
            return RSA.getPrivateKey(privateKey);
        }

        public static String sign(String content, String privateKey) {
            try {
                Signature signature = Signature.getInstance("SHA256WithRSA");
                signature.initSign(getPrivateKey(privateKey));
                signature.update(content.getBytes("UTF-8"));
                return Base64.getEncoder().encodeToString(signature.sign());
            } catch (Exception var3) {
                return null;
            }
        }

        public static boolean verify(String content, String sign, String publicKey) {
            try {
                Signature signature = Signature.getInstance("SHA256WithRSA");
                signature.initVerify(getPublicKey(publicKey));
                signature.update(content.getBytes("UTF-8"));
                return signature.verify(Base64.getDecoder().decode(sign));
            } catch (Exception var4) {

            }
            return false;
        }

        public static String encrypt(String content, Key key) {
            return RSA.encrypt(content, key);
        }

        public static String decrypt(String content, Key key) {
            return RSA.decrypt(content, key);
        }
    }

    public static class RSA {
        private static final String ALGORITHM = "RSA";
        private static final String ALGORITHMS_SHA1 = "SHA1WithRSA";

        public RSA() {
        }

        public static Pair<String, String> generateKeyPair() {
            KeyPairGenerator keygen = null;

            try {
                keygen = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException var2) {
                return null;
            }

            keygen.initialize(512, new SecureRandom());
            KeyPair keyPair = keygen.generateKeyPair();
            return Pair.of(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()), Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        }

        public static Pair<String, String> generateKeyPair(int keySize) {
            KeyPairGenerator keygen = null;

            try {
                keygen = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException var3) {
                return null;
            }

            keygen.initialize(keySize, new SecureRandom());
            KeyPair keyPair = keygen.generateKeyPair();
            return Pair.of(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()), Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        }

        public static RSAPublicKey getPublicKey(String publicKey) {
            try {
                return (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
            } catch (Exception var2) {
                return null;
            }
        }

        public static RSAPrivateKey getPrivateKey(String privateKey) {
            try {
                return (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
            } catch (Exception var2) {
                return null;
            }
        }

        public static String sign(String content, String privateKey) {
            try {
                Signature signature = Signature.getInstance("SHA1WithRSA");
                signature.initSign(getPrivateKey(privateKey));
                signature.update(content.getBytes("UTF-8"));
                return Base64.getEncoder().encodeToString(signature.sign());
            } catch (Exception var3) {
                return null;
            }
        }

        public static boolean verify(String content, String sign, String publicKey) {
            try {
                Signature signature = Signature.getInstance("SHA1WithRSA");
                signature.initVerify(getPublicKey(publicKey));
                signature.update(content.getBytes("UTF-8"));
                return signature.verify(Base64.getDecoder().decode(sign));
            } catch (Exception var4) {

            }
            return false;
        }

        public static String encrypt(String content, Key key) {
            try {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(1, key);
                return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes("UTF-8")));
            } catch (Exception var3) {
                return null;
            }
        }

        public static String decrypt(String content, Key key) {
            try {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(2, key);
                return new String(cipher.doFinal(Base64.getDecoder().decode(content)), StandardCharsets.UTF_8);
            } catch (Exception var3) {
                return null;
            }
        }

        public static String encrypt(String content, String publicKey) {
            return encrypt(content, (String)null, publicKey, (WithSalt)null);
        }

        public static String encrypt(String content, String key, String publicKey) {
            return encrypt(content, key, publicKey, EncryptionUtils.DEFAULT_WITH_SALT);
        }

        public static String encrypt(String content, String key, String publicKey, WithSalt withSalt) {
            return encrypt(withSalt != null ? withSalt.withSalt(content, key) : content, (Key)getPublicKey(publicKey));
        }

        public static String decrypt(String content, String privateKey) {
            return decrypt(content, (String)null, privateKey, (WithoutSalt)null);
        }

        public static String decrypt(String content, String key, String privateKey) {
            return decrypt(content, key, privateKey, EncryptionUtils.DEFAULT_WITHOUT_SALT);
        }

        public static String decrypt(String content, String key, String privateKey, WithoutSalt withoutSalt) {
            return withoutSalt != null ? withoutSalt.withoutSalt(decrypt(content, (Key)getPrivateKey(privateKey)), key) : decrypt(content, (Key)getPrivateKey(privateKey));
        }
    }

    @FunctionalInterface
    interface WithoutSalt {
        String withoutSalt(String content, String salt);
    }

    @FunctionalInterface
    interface WithSalt {
        String withSalt(String content, String salt);
    }

    public static class AES {
        private static final String ALGORITHM = "AES";

        public AES() {
        }

        public static String generaterKey() {
            KeyGenerator keygen = null;

            try {
                keygen = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException var2) {
                return null;
            }

            keygen.init(128, new SecureRandom());
            SecretKey secretKey = keygen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        }

        public static String generaterKey(int keySize) {
            KeyGenerator keygen = null;

            try {
                keygen = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException var3) {
                return null;
            }

            keygen.init(keySize, new SecureRandom());
            SecretKey secretKey = keygen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        }

        private static SecretKeySpec getSecretKeySpec(String secretKeyStr) {
            return new SecretKeySpec(Base64.getDecoder().decode(secretKeyStr), "AES");
        }

        public static String encrypt(String content, String secretKey) {
            Key key = getSecretKeySpec(secretKey);

            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(1, key);
                return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes("UTF-8")));
            } catch (Exception var4) {
                return null;
            }
        }

        public static String encryptWithUrlEncoder(String content, String secretKey) {
            Key key = getSecretKeySpec(secretKey);

            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(1, key);
                return Base64.getUrlEncoder().encodeToString(cipher.doFinal(content.getBytes("UTF-8")));
            } catch (Exception var4) {
                return null;
            }
        }

        public static String decrypt(String content, String secretKey) {
            Key key = getSecretKeySpec(secretKey);

            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(2, key);
                return new String(cipher.doFinal(Base64.getDecoder().decode(content)), StandardCharsets.UTF_8);
            } catch (Exception var4) {
                return null;
            }
        }

        public static String decryptWithUrlDecoder(String content, String secretKey) {
            Key key = getSecretKeySpec(secretKey);

            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(2, key);
                return new String(cipher.doFinal(Base64.getUrlDecoder().decode(content.getBytes("UTF-8"))), StandardCharsets.UTF_8);
            } catch (Exception var4) {
                return null;
            }
        }
    }

    public static class MD5 {
        public MD5() {
        }

        public static String encrypt(String content) {
            return DigestUtils.md5Hex(content);
        }

        public static String encrypt(byte[] content) {
            return DigestUtils.md5Hex(content);
        }

        public static String encrypt(InputStream contentStream) {
            try {
                return DigestUtils.md5Hex(contentStream);
            } catch (IOException var2) {
                return null;
            }
        }
    }
}
