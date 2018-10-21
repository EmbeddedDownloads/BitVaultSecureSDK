package securityencryption;

import android.util.Base64;

import org.spongycastle.util.Strings;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.util.Arrays;

import valle.btc.BTCUtils;


public class SignatureECDSA {

	/**
	 * @param args
	 */
	final private static String BTCprivKey = "18E14A7B6A307F426A94F8114701E7C8E774E7F9A47E2C2035DB29A206321725";
	//final private static String Message = "Hi, This is a message for signing";
	final private static String ECurve = "secp256k1";
	static {
		Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
	}

	public String signBytesVal(String data, String BTCpubKey){
		// TODO Auto-generated method stub
		
		// Get Bitcoin Keys
		try {
			System.out.println("\nWallet Keys...");
			PublicKey PubKey = getBTCPublicKey(BTCpubKey);
			System.out.println(PubKey);
			PrivateKey PrivKey = getBTCPrivateKey(BTCprivKey);
			System.out.println(PrivKey);

			// get wallet address from public key
			BTCUtils btc = new BTCUtils();
			String BTCAdd = btc.publicKeyToAddress(Hex.decode(BTCpubKey));
			System.out.println("Wallet Address: " + BTCAdd + "\n");
        
        /* Signature Generation */
			// initialize signature generation algorithm with Private key
			Signature signature = Signature.getInstance("ECDSA", "SC");
			signature.initSign(PrivKey, new SecureRandom());

			// convert message to bytes
			byte[] input = Strings.toUTF8ByteArray(data + "|$$|" + BTCpubKey);
			//System.out.println("Message:" + Base64.toBase64String(input) + "  len: " + input.length);

			// find signature
			signature.update(input);
			byte[] sigBytes = signature.sign();
			//System.out.println("Signature:" + Base64.toBase64String(sigBytes) + "  len: " + sigBytes.length);
        
        /* Signature verification */
			// initialize signature verification algorithm with Public Key.
			Signature signatureVerify = Signature.getInstance("ECDSA", "SC");
			signatureVerify.initVerify(PubKey);
			signatureVerify.update(input);

			// Print success or failure.
			if (signatureVerify.verify(sigBytes)) {
				System.out.println("signature verification succeeded.");
				return Base64.encodeToString(sigBytes,Base64.NO_WRAP);
			} else {
				System.out.println("signature verification failed.");
				return null;
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}
	
	/* ------------------------------ METHODS ------------------------------------- */
	
	/* Get EC Public Key from BTC public key */
	public PublicKey getBTCPublicKey ( String pubKeyHex ) throws Exception {
	    
		byte[] keyRaw = Hex.decode( pubKeyHex );
		BigInteger xInt = new BigInteger(1, Arrays.copyOfRange(keyRaw, 1, 33) );
		BigInteger yInt = new BigInteger(1, Arrays.copyOfRange(keyRaw, 33, 65) );
		
		ECPoint pubPoint = new ECPoint( xInt, yInt  );
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "SC");
        parameters.init(new ECGenParameterSpec( ECurve ));
        ECParameterSpec ecParameters = (ECParameterSpec) parameters.getParameterSpec(ECParameterSpec.class);
 
        ECPublicKeySpec pubSpec = new ECPublicKeySpec(pubPoint, ecParameters);
        KeyFactory kf = KeyFactory.getInstance("EC", "SC");
        PublicKey key = kf.generatePublic(pubSpec);
		
        return key;
	}
	
	/* Get EC Private Key from BTC private key */
	public PrivateKey getBTCPrivateKey ( String privKeyHex ) throws Exception {
		
		byte[] keyRaw = Hex.decode( privKeyHex );
		BigInteger privInt = new BigInteger(1, keyRaw );
		
		AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "SC");
        parameters.init(new ECGenParameterSpec( ECurve ));
        ECParameterSpec ecParameters = (ECParameterSpec) parameters.getParameterSpec(ECParameterSpec.class);
		
		ECPrivateKeySpec privSpec = new ECPrivateKeySpec(privInt, ecParameters );
        KeyFactory kf = KeyFactory.getInstance("EC", "SC");
        PrivateKey key = kf.generatePrivate(privSpec);
		
        return key;
	}
}
