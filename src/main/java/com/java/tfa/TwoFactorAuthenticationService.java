package com.java.tfa;

import org.springframework.stereotype.Service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;

@Service
public class TwoFactorAuthenticationService {
	
	public String generateNewSecret() {
		
		
		return new DefaultSecretGenerator().generate();
	}
	
	public String generateQrCodeImageUri(String secret) {
		
		QrData data = new QrData.Builder()
				.label("Ehtishamul Coding 2FA example")
				.secret(secret)
				.issuer("Ehtishamul Hassan")
				.algorithm(HashingAlgorithm.SHA256)
				.digits(6)
				.period(30)
				.build();
		
		QrGenerator generator = new ZxingPngQrGenerator();
		byte[] imageDate =new byte[0];
		
		try {
			
			imageDate = generator.generate(data);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return Utils.getDataUriForImage(imageDate, generator.getImageMimeType());
		
	}
	
	public boolean isOtpValid(String secret, String code) {
		
		TimeProvider timeProvider = new SystemTimeProvider();
		
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		
		CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
		
		return verifier.isValidCode(secret, code);
		
	}
	
	public boolean isOtpNotValid(String secret,String code) {
		
		return !this.isOtpValid(secret, code);
	}

}
