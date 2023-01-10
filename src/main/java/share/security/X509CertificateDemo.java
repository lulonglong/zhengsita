package share.security;

import com.alibaba.fastjson.JSON;
import sun.security.rsa.RSAPublicKeyImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * @author liyuxiang
 * @date 2022-02-22
 */
public class X509CertificateDemo {

	public static void main(String[] args) throws Exception {

		File file = new File("/Users/lyxiang/coding/crm-tech/src/main/java/share/security/alipayCertPublicKey_RSA2.crt");
		InputStream inStream = new FileInputStream(file);
		//创建X509工厂类
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		//创建证书对象
		X509Certificate x509Certificate = (X509Certificate)cf.generateCertificate(inStream);
		inStream.close();

		RSAPublicKeyImpl publicKey =  (RSAPublicKeyImpl) x509Certificate.getPublicKey();
		System.out.println(new String(publicKey.getEncoded()));
		System.out.println(publicKey.getFormat());
		System.out.println(publicKey.toString());

		System.out.println(JSON.toJSONString(x509Certificate));

	}

}
