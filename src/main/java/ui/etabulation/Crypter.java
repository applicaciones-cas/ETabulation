package ui.etabulation;


public class Crypter {

    public static String encrypt(String message, String key) {
        StringBuilder hexEncrypted = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char msgChar = message.charAt(i);
            char keyChar = key.charAt(i % key.length());
            int xorChar = msgChar ^ keyChar;
            hexEncrypted.append(String.format("%02x", xorChar));
        }
        return hexEncrypted.toString();
    }

    public static String decrypt(String hexEncrypted, String key) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < hexEncrypted.length(); i += 2) {
            String hexByte = hexEncrypted.substring(i, i + 2);
            int encryptedChar = Integer.parseInt(hexByte, 16);
            char keyChar = key.charAt((i / 2) % key.length());
            char originalChar = (char) (encryptedChar ^ keyChar);
            decrypted.append(originalChar);
        }
        return decrypted.toString();
    }
}
